package com.app.server.util.wallet_service_producer.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDateRangeRequest {

    private String sub;
    private LocalDateTime from;
    private LocalDateTime to;

}