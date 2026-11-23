package com.app.server.dto.response.user;

import lombok.Data;
import java.util.List;

@Data
public class ServiceDataDto {

    private String sub;
    private Long id;
    private String walletId;
    private List<CompanyDto> companies;
    private List<UserCaDto> userCas;
    private String slug;
}
