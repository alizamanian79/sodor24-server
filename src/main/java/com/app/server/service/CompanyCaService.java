package com.app.server.service;

import com.app.server.dto.request.companies.CreateCompanyCaRequestDto;
import com.app.server.dto.response.CustomResponseDto;
import com.app.server.model.companies.CompanyCa;
import com.app.server.util.wallet_service_producer.dto.response.WalletResponseDto;

import java.util.List;
import java.util.Map;

public interface CompanyCaService {

    List<CompanyCa> companyCaList();
    CompanyCa createCa(CreateCompanyCaRequestDto req);
    CompanyCa findCompanyCaBySlug(String slug);
    CustomResponseDto deleteCompanyCaBySlug(String slug);
    CompanyCa setActiveBySlug(String slug,boolean value);
    CompanyCa setStatusBySlug(String slug,String status);


    WalletResponseDto payment(String slug,String accessToken);

    Map<String, Object> callBackCompanyCa(String caSub,String walletSub ,
                                          boolean value ,
                                          Integer transactionSlug);

}
