package com.app.server.util.rabbitMQ.dto.request;


import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Data
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContractRequestDto {


    private MultipartFile file;
    private MultipartFile privateKeyFile;
    private String keyPassword;

    private String reason;
    private String country;

}