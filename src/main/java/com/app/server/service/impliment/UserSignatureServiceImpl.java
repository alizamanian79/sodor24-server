package com.app.server.service.impliment;

import com.app.server.exception.AppConflicException;
import com.app.server.exception.AppNotFoundException;
import com.app.server.model.Signature;
import com.app.server.model.User;
import com.app.server.model.UserSignature;
import com.app.server.repository.UserSignatureRepository;
import com.app.server.service.SignatureService;
import com.app.server.service.UserService;
import com.app.server.service.UserSignatureService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserSignatureServiceImpl implements UserSignatureService {

    private final SignatureService signatureService;
    private final UserService userService;
    private final UserSignatureRepository userSignatureRepository;

    @Override
    public List<UserSignature> findAll() {
        List<UserSignature> list = userSignatureRepository.findAll();
        Collections.reverse(list);
        return list;
    }

    @Override
    public UserSignature findById(Long usersignatureId) {
        return userSignatureRepository.findById(usersignatureId).orElseThrow(()-> new AppNotFoundException("امضا پیدا نشد"));
    }


    @Transactional
    @Override
    public UserSignature generateUserSignature(Long userId, Long signatureId) {
        // find user and signature plan
        User existUser = userService.findUserById(userId);
        Signature existSignature =signatureService.findSignatureById(signatureId);

        if (!existSignature.isActive()){
            throw new AppConflicException("اعتبار این پلن از امضا تایید نشده","به محض تعویض وضعیت این پلن به شما اطلاع خواهیم داد");
        }

        // Generate Signature
        UserSignature userSignature = UserSignature.builder()
                .user(existUser)
                .signature(existSignature)
                .valid(false)
                .usageCount(existSignature.getUsageCount())
                .expiredAt(LocalDateTime.now().plusDays(existSignature.getPeriod()))
                .build();
        UserSignature saved = userSignatureRepository.save(userSignature);
        return saved;

    }
}
