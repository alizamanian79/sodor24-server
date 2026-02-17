package com.app.server.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ContractRequestDto {
    private Long userId;
    private String title;
    private String description;
    private String pdf;
    private String signedLink;
    private String unSignedLink;


}
