package com.app.server.dto.response.user;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Builder
@Data
public class UserProfileResponseDto {

    private String sub;
    private AccountDataDto accountData;
    private ServiceDataDto serviceData;
    private WalletDataDto walletData;
    private Object signatureData;
}
