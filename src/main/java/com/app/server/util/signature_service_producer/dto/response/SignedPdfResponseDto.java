package com.app.server.util.signature_service_producer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Metadata about a freshly-signed PDF contract, returned to the caller
 * right after signing. `fileBase64` carries the full signed PDF so the
 * client can download/save it directly from this response — no separate
 * download endpoint needed.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignedPdfResponseDto {

    /**
     * Stable id shared between the unsigned and signed archive copies
     * on disk (see PdfSigningServiceImpl's contracts/unsigned and
     * contracts/signed folders).
     */
    private String contractId;

    /**
     * Original PDF filename as uploaded by the client.
     */
    private String fileName;

    private String companyId;

    private String userId;

    /**
     * Common Name (CN) extracted from the signer's certificate subject.
     */
    private String signerName;

    private LocalDateTime signedAt;

    private long sizeBytes;

    /**
     * Base64-encoded bytes of the signed PDF. Decode this to get the
     * downloadable file.
     */
    private String fileBase64;
}