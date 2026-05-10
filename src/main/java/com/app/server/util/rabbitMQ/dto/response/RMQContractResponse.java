package com.app.server.util.rabbitMQ.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RMQContractResponse<T> {
    private boolean success;
    private ContractData data;
    private ContractError error;
}
