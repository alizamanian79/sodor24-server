package com.app.server.util.signature_service_producer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DownloadFileResponseDto<T> {
    private String fileName;
    private byte[] content;
}