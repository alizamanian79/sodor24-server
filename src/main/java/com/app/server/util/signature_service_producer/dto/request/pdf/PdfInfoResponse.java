package com.app.server.util.signature_service_producer.dto.request.pdf;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PdfInfoResponse {

    // File
    private String fileName;
    private String fileExtension;
    private long sizeBytes;
    private String sizeReadable;
    private String sha256;
    private String mimeType;

    // PDF
    private String pdfVersion;
    private int pageCount;
    private boolean encrypted;
    private boolean linearized;

    // Metadata
    private String title;
    private String author;
    private String subject;
    private String keywords;
    private String creator;
    private String producer;
    private String creationDate;
    private String modificationDate;

    // Document
    private String documentId;
    private String language;
    private String trapped;

    // Signatures
    private int signatureCount;
    private int maxAllowedSignatures;
    private int remainingSignatureCount;
    private boolean signed;
    private boolean fullySigned;

    private List<PdfSignerInfo> signers;

    // Contract
    private String companyId;
    private String contractId;

    private List<String> allowedSignerIds;
    private List<String> signedUserIds;
    private List<String> remainingSignerIds;
}

