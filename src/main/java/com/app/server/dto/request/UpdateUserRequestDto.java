package com.app.server.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateUserRequestDto {
    private String username;
    private String password;


    private String email;
    private String firstName;
    private String lastName;
    private String nationalCode;


    @Pattern(
            regexp = "^\\d{11}$",
            message = "شماره تماس باید 11 رقم باشد 0912XXXXXXX"
    )
    private String phoneNumber;
}
