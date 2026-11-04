package com.app.server.util.signature_service_producer.dto.request.ca;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddOrRemoveSignersRequestDto {
    private String companyId;
    private String contractId;
    private List<String> userIds;
}
