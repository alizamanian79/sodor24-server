package com.app.server.util.signature_service_producer.dto.request.pdf;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PdfSignerInfo {

    // Signature
    private int signatureIndex;

    private String userId;
    private String signerName;

    // Certificate
    private String certificateSubject;
    private String certificateIssuer;
    private String certificateSerialNumber;
    private String certificateVersion;
    private String certificateAlgorithm;
    private String certificateNotBefore;
    private String certificateNotAfter;
    private boolean certificateCurrentlyValid;

    // PDF Signature
    private String filter;
    private String subFilter;
    private String reason;
    private String location;
    private String signDate;
    private String contactInfo;
    private String signatureName;

    private String byteRange;

    private boolean certification;
    private String certificationPermission;

    private boolean hasContents;
    private boolean hasByteRange;
}
