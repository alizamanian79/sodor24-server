package com.app.server.util.signature_service_producer.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DownloadFileRequestDto {

    private String fileType;
    private String fileName;

}
