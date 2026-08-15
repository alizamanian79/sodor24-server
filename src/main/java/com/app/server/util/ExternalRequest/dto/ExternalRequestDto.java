package com.app.server.util.ExternalRequest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExternalRequestDto {
    private String url;
    private Object body;
    private String token;
    private Method method;
    private String param;
}
