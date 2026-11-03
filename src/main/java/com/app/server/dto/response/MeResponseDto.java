package com.app.server.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MeResponseDto<AccountData,ServiceData,WalletData,SignatureData> {
    private String sub;
    private AccountData accountData;
    private ServiceData serviceData;
    private WalletData walletData;
    private SignatureData signatureData;
}
