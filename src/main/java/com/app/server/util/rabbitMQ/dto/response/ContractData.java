package com.app.server.util.rabbitMQ.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContractData {
    private String fileName;
    private String unsignedPdf;
    private String signedPdf;
    private String message;

}
