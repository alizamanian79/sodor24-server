package com.app.server.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserCaRequestDto {

    private String userSlug;

    private String companyCaSlug;

    private String privateKeyPassword;
}
