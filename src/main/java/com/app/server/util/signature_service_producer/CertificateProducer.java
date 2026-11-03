package com.app.server.util.signature_service_producer;


import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class CertificateProducer {

    private final RabbitTemplate rabbitTemplate;

    @Value("${application.signature-service.rabbitmq.exchange}")
    private String exchangeName;


    /*
     * ============================================================
     * ROUTING KEYS
     * ============================================================
     */

    @Value("${application.signature-service.rabbitmq.routing-keys.create-company-certificate-routing-key}")
    private String createCompanyCertificateRoutingKey;

    @Value("${application.signature-service.rabbitmq.routing-keys.create-user-certificate-routing-key}")
    private String createUserCertificateRoutingKey;

    @Value("${application.signature-service.rabbitmq.routing-keys.verify-user-certificate-routing-key}")
    private String verifyUserCertificateRoutingKey;

    @Value("${application.signature-service.rabbitmq.routing-keys.verify-user-certificate-with-key-routing-key}")
    private String verifyUserCertificateWithKeyRoutingKey;

    @Value("${application.signature-service.rabbitmq.routing-keys.revoke-user-certificate-routing-key}")
    private String revokeUserCertificateRoutingKey;

    @Value("${application.signature-service.rabbitmq.routing-keys.unrevoke-user-certificate-routing-key}")
    private String unrevokeUserCertificateRoutingKey;

    @Value("${application.signature-service.rabbitmq.routing-keys.revoke-company-ca-routing-key}")
    private String revokeCompanyCaRoutingKey;

    @Value("${application.signature-service.rabbitmq.routing-keys.unrevoke-company-ca-routing-key}")
    private String unrevokeCompanyCaRoutingKey;

    @Value("${application.signature-service.rabbitmq.routing-keys.company-ca-status-routing-key}")
    private String companyCaStatusRoutingKey;

    @Value("${application.signature-service.rabbitmq.routing-keys.user-certificate-status-routing-key}")
    private String userCertificateStatusRoutingKey;

    @Value("${application.signature-service.rabbitmq.routing-keys.download-company-private-key-routing-key}")
    private String downloadCompanyPrivateKeyRoutingKey;

    @Value("${application.signature-service.rabbitmq.routing-keys.download-user-private-key-routing-key}")
    private String downloadUserPrivateKeyRoutingKey;

    @Value("${application.signature-service.rabbitmq.routing-keys.download-company-certificate-routing-key}")
    private String downloadCompanyCertificateRoutingKey;

    @Value("${application.signature-service.rabbitmq.routing-keys.download-user-certificate-routing-key}")
    private String downloadUserCertificateRoutingKey;

    @Value("${application.signature-service.rabbitmq.routing-keys.save-user-signature-image-routing-key}")
    private String saveUserSignatureImageRoutingKey;

    @Value("${application.signature-service.rabbitmq.routing-keys.update-user-signature-image-routing-key}")
    private String updateUserSignatureImageRoutingKey;

    @Value("${application.signature-service.rabbitmq.routing-keys.download-user-signature-image-routing-key}")
    private String downloadUserSignatureImageRoutingKey;

    @Value("${application.signature-service.rabbitmq.routing-keys.delete-user-signature-image-routing-key}")
    private String deleteUserSignatureImageRoutingKey;

    @Value("${application.signature-service.rabbitmq.routing-keys.base64-to-file-routing-key}")
    private String base64ToFileRoutingKey;

    @Value("${application.signature-service.rabbitmq.routing-keys.validate-private-key-routing-key}")
    private String validatePrivateKeyRoutingKey;


    /*
     * ============================================================
     * HELPER
     * ============================================================
     */

    @SuppressWarnings("unchecked")
    private Map<String, Object> sendAndReceive(String routingKey, Object request) {

        Object response = rabbitTemplate.convertSendAndReceive(
                exchangeName,
                routingKey,
                request
        );

        if (response == null) {
            throw new IllegalStateException("RabbitMQ پاسخ خالی برگرداند");
        }

        if (response instanceof Map<?, ?> map) {
            return (Map<String, Object>) map;
        }

        throw new IllegalStateException("فرمت پاسخ RabbitMQ معتبر نیست");
    }


    /*
     * ============================================================
     * CREATE
     * ============================================================
     */

    public Map<String, Object> createCompanyCa(CreateCompanyCaRequestDto request) {
        return sendAndReceive(createCompanyCertificateRoutingKey, request);
    }

    public Map<String, Object> createPersonCertificate(CreatePersonCertificateRequestDto request) {
        return sendAndReceive(createUserCertificateRoutingKey, request);
    }


    /*
     * ============================================================
     * VERIFY
     * ============================================================
     */

    public Map<String, Object> verifyUserCertificate(Map<String, String> request) {
        return sendAndReceive(verifyUserCertificateRoutingKey, request);
    }

    public Map<String, Object> verifyUserCertificateWithKey(Map<String, String> request) {
        return sendAndReceive(verifyUserCertificateWithKeyRoutingKey, request);
    }


    /*
     * ============================================================
     * REVOKE / UNREVOKE USER
     * ============================================================
     */

    public Map<String, Object> revokeUserCertificate(Map<String, String> request) {
        return sendAndReceive(revokeUserCertificateRoutingKey, request);
    }

    public Map<String, Object> unrevokeUserCertificate(Map<String, String> request) {
        return sendAndReceive(unrevokeUserCertificateRoutingKey, request);
    }


    /*
     * ============================================================
     * REVOKE / UNREVOKE COMPANY CA
     * ============================================================
     */

    public Map<String, Object> revokeCompanyCa(Map<String, String> request) {
        return sendAndReceive(revokeCompanyCaRoutingKey, request);
    }

    public Map<String, Object> unrevokeCompanyCa(Map<String, String> request) {
        return sendAndReceive(unrevokeCompanyCaRoutingKey, request);
    }


    /*
     * ============================================================
     * STATUS
     * ============================================================
     */

    public Map<String, Object> getCompany(Map<String, String> request) {
        return sendAndReceive(companyCaStatusRoutingKey, request);
    }

    public Map<String, Object> getUserCertificateStatus(Map<String, String> request) {
        return sendAndReceive(userCertificateStatusRoutingKey, request);
    }


    /*
     * ============================================================
     * DOWNLOAD PRIVATE KEYS
     * ============================================================
     */

    public Map<String, Object> downloadCompanyPrivateKey(Map<String, String> request) {
        return sendAndReceive(downloadCompanyPrivateKeyRoutingKey, request);
    }

    public Map<String, Object> downloadUserPrivateKey(Map<String, String> request) {
        return sendAndReceive(downloadUserPrivateKeyRoutingKey, request);
    }


    /*
     * ============================================================
     * DOWNLOAD CERTIFICATES
     * ============================================================
     */

    public Map<String, Object> downloadCompanyCertificate(Map<String, String> request) {
        return sendAndReceive(downloadCompanyCertificateRoutingKey, request);
    }

    public Map<String, Object> downloadUserCertificate(Map<String, String> request) {
        return sendAndReceive(downloadUserCertificateRoutingKey, request);
    }


    /*
     * ============================================================
     * SIGNATURE IMAGE
     * ============================================================
     */

    public Map<String, Object> saveUserSignatureImage(Map<String, Object> request) {
        return sendAndReceive(saveUserSignatureImageRoutingKey, request);
    }

    public Map<String, Object> updateUserSignatureImage(Map<String, Object> request) {
        return sendAndReceive(updateUserSignatureImageRoutingKey, request);
    }

    public Map<String, Object> downloadUserSignatureImage(Map<String, String> request) {
        return sendAndReceive(downloadUserSignatureImageRoutingKey, request);
    }

    public Map<String, Object> deleteUserSignatureImage(Map<String, String> request) {
        return sendAndReceive(deleteUserSignatureImageRoutingKey, request);
    }


    /*
     * ============================================================
     * BASE64 TO FILE
     * ============================================================
     */

    public Map<String, Object> base64ToFile(Map<String, String> request) {
        return sendAndReceive(base64ToFileRoutingKey, request);
    }


    /*
     * ============================================================
     * VALIDATE PRIVATE KEY
     * ============================================================
     */

    public Map<String, Object> validateUploadedPrivateKey(Map<String, Object> request) {
        return sendAndReceive(validatePrivateKeyRoutingKey, request);
    }
}