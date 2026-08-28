package com.app.server.service.impliment;

import com.app.server.dto.request.ContractRequestDto;
import com.app.server.model.Contract;
import com.app.server.model.Signature;
import com.app.server.model.User;
import com.app.server.model.UserContract;
import com.app.server.repository.ContractRepository;
import com.app.server.repository.UserContractRepository;
import com.app.server.service.ContractService;
import com.app.server.service.SignatureService;
import com.app.server.service.UserService;
import com.app.server.util.signature_service_producer.producer.ContractProducer;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;


@Slf4j
@Service
@RequiredArgsConstructor
public class ContractServiceImpl implements ContractService {

    private static final int MAX_SLUG_GENERATION_ATTEMPTS = 20;

    private final ContractRepository contractRepository;
    private final UserService userService;
    private final UserContractRepository userContractRepository;
    private final SignatureService signatureService;
    private final ContractProducer contractProducer;


    @Override
    public List<Contract> contractList() {
        // Sorting delegated to the DB instead of loading everything and reversing in memory.
        return contractRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }


    @Transactional
    @Override
    public Contract preparationContract(ContractRequestDto req) {

        if (req.getSlug() == null) {
            throw new RuntimeException("اسلاگ قرارداد نامعتبر است");
        }
        if (req.getPdfFile() == null) {
            throw new RuntimeException("فایل قرارداد ارسال نشده است");
        }

        Signature signature = checkSignature(req.getSignatureId());

        Contract contract = req.getSlug().isBlank()
                ? signingNewContract(req)
                : signingContractExist(req);

        if (contract == null) {
            throw new RuntimeException("شما قبلا این قرارداد رو امضا کرده اید");
        }

        String pdfName = resolvePdfFileName(contract);
        MultipartFile renamedFile = renameMultipartFile(req.getPdfFile(), pdfName);

        com.app.server.util.signature_service_producer.dto.request.ContractRequestDto producerRequest =
                com.app.server.util.signature_service_producer.dto.request.ContractRequestDto.builder()
                        .file(renamedFile)
                        .privateKeyFile(req.getPrivateKeyFile())
                        .keyPassword(req.getPassword())
                        .country(signature.getCountry())
                        .reason(signature.getReason())
                        .build();

        Map<String, Object> data = callSignatureProducer(producerRequest);
        String fileName = extractFileName(data);

        contract.setSlug(fileName.replace(".pdf", ""));
        contractRepository.save(contract);

        return contract;
    }


    @Override
    public Contract findContractById(Long contractId) {
        return contractRepository.findById(contractId)
                .orElseThrow(() -> new RuntimeException
                        ("قرارداد با ایدی مورد نظر شما پیدا نشد"));
    }

    @Override
    public Contract findContractBySlug(String slug) {
        return contractRepository.findContractBySlug(slug)
                .orElseThrow(() -> new RuntimeException("قرارداد با ایدی مورد نظر شما پیدا نشد"));
    }

    @Override
    public String deleteContractById(Long id) {
        Contract findContract = findContractById(id);
        contractRepository.delete(findContract);
        return "قرارداد شما با موفقیت حذف شد";
    }

    @Override
    public boolean isExistContract(String slug) {
        return contractRepository.existsBySlug(slug);
    }


    // Generate a 5-digit numeric slug candidate.
    public String createSlug() {
        StringBuilder result = new StringBuilder(5);
        for (int i = 0; i < 5; i++) {
            result.append(ThreadLocalRandom.current().nextInt(10));
        }
        return result.toString();
    }

    // Guarantees uniqueness before persisting — createSlug() alone gave only 100,000
    // combinations with no collision check, so collisions were only a matter of time.
    private String generateUniqueSlug() {
        String slug;
        int attempts = 0;
        do {
            if (attempts++ >= MAX_SLUG_GENERATION_ATTEMPTS) {
                throw new RuntimeException("امکان تولید شناسه یکتا برای قرارداد وجود ندارد");
            }
            slug = createSlug();
        } while (contractRepository.existsBySlug(slug));
        return slug;
    }


    // Main part

    @Transactional
    public Signature checkSignature(Long signatureId) {
        Signature signature = signatureService.findById(signatureId);

        if (!signature.isValid()) {
            throw new RuntimeException
                    ("امضای شما معتبر نمیباشد - به پنل مراجعه کرده و تاریخ و تعداد استفاده رو بررسی نمایید");
        }
        if (signature.getUsageCount() <= 0) {
            throw new RuntimeException("تعداد امضای شما تمام شده");
        }
        if (signature.getSignatureExpired().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("تاریخ امضای شما تمام شده");
        }

        // Single decrement, single evaluation, single save — the previous version decremented
        // once, saved, then re-subtracted 1 from the *already-decremented* value to decide
        // whether to invalidate, which is an off-by-one bug and cost an extra DB write.
        int remaining = signature.getUsageCount() - 1;
        signature.setUsageCount(remaining);
        if (remaining <= 0) {
            signature.setStatus("حجم امضا به پایان رسیده");
            signature.setValid(false);
        }

        return signatureService.updateSignatureIntenral(signature);
    }


    // New contract and signer
    public Contract signingNewContract(ContractRequestDto req) {
        User existUser = userService.findUserById(req.getUserId());

        Contract contract = Contract.builder()
                .title(req.getTitle())
                .description(req.getDescription())
                .slug(generateUniqueSlug())
                .signedLink(req.getSignedLink())
                .unSignedLink(req.getUnSignedLink())
                .signers(List.of())
                .build();
        contractRepository.save(contract);

        UserContract userContract = UserContract.builder()
                .user(existUser)
                .signature(signatureService.findById(req.getSignatureId()))
                .contract(contract)
                .build();

        userContractRepository.save(userContract);
        return contract;
    }

    // Contract exists and a new signer wants to sign
    public Contract signingContractExist(ContractRequestDto req) {
        Contract existContract = findContractBySlug(req.getSlug());
        User existUser = userService.findUserById(req.getUserId());

        if (userContractRepository.existsByContractAndUser(existContract, existUser)) {
            return null;
        }

        UserContract userContract = UserContract.builder()
                .user(existUser)
                .contract(existContract)
                .build();

        try {
            // The existsByContractAndUser check above is a check-then-act race under
            // concurrent requests. A DB-level UNIQUE constraint on (contract_id, user_id)
            // is required for this catch to actually protect against duplicate signers —
            // please add one if it doesn't already exist.
            userContractRepository.save(userContract);
        } catch (DataIntegrityViolationException e) {
            return null;
        }

        return existContract;
    }


    private String resolvePdfFileName(Contract contract) {
        String signedLink = contract.getSignedLink();
        // contract.getSignedLink().equals("") threw NPE whenever signedLink was null
        // (the normal/default case), instead of just falling back to slug + ".pdf".
        return (signedLink == null || signedLink.isBlank()) ? contract.getSlug() + ".pdf" : signedLink;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> callSignatureProducer(
            com.app.server.util.signature_service_producer.dto.request.ContractRequestDto req) {
        Object rawResult;
        try {
            rawResult = contractProducer.createOrSignedContract(req);
        } catch (Exception e) {
            log.error("خطا در ارتباط با سرویس امضا", e);
            throw new RuntimeException("خطا در برقراری ارتباط با سرویس امضا رخ داد", e);
        }

        if (!(rawResult instanceof Map<?, ?> resultMap)) {
            throw new RuntimeException("پاسخ نامعتبر از سرویس امضا دریافت شد");
        }
        log.debug("پاسخ سرویس امضا: {}", resultMap);

        Object dataObj = resultMap.get("data");
        if (!(dataObj instanceof Map<?, ?> dataMap)) {
            throw new RuntimeException("رمز کیلید اشتباه میباشد");
        }
        return (Map<String, Object>) dataMap;
    }

    private String extractFileName(Map<String, Object> data) {
        Object fileName = data.get("fileName");
        if (fileName == null) {
            throw new RuntimeException("نام فایل در پاسخ سرویس امضا موجود نیست");
        }
        return fileName.toString();
    }

    private MultipartFile renameMultipartFile(MultipartFile file, String newFileName) {
        // MockMultipartFile is a *test-only* class (org.springframework.mock.web) that was
        // being used in production code. Besides being semantically wrong, spring-boot's
        // test starter is normally on the `test` classpath scope only — this can blow up
        // with NoClassDefFoundError in a production build. It also eagerly reads the whole
        // InputStream into memory on construction.
        // RenamedMultipartFile below is a thin, lazy delegating wrapper: no test dependency,
        // no eager buffering, so large PDFs stream through instead of being copied twice.
        return new RenamedMultipartFile(file, newFileName);
    }


    /**
     * Lightweight {@link MultipartFile} wrapper that only overrides the file name and
     * delegates everything else (bytes/stream/size/content-type) to the original file.
     */
    private static class RenamedMultipartFile implements MultipartFile {

        private final MultipartFile original;
        private final String newFileName;

        RenamedMultipartFile(MultipartFile original, String newFileName) {
            this.original = original;
            this.newFileName = newFileName;
        }

        @Override
        public String getName() {
            return newFileName;
        }

        @Override
        public String getOriginalFilename() {
            return newFileName;
        }

        @Override
        public String getContentType() {
            return original.getContentType();
        }

        @Override
        public boolean isEmpty() {
            return original.isEmpty();
        }

        @Override
        public long getSize() {
            return original.getSize();
        }

        @Override
        public byte[] getBytes() throws IOException {
            return original.getBytes();
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return original.getInputStream();
        }

        @Override
        public void transferTo(File dest) throws IOException, IllegalStateException {
            original.transferTo(dest);
        }
    }
}