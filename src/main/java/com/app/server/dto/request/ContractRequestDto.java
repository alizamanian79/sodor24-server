package com.app.server.dto.request;

import com.app.server.model.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContractRequestDto {

    private String title;
    private String description;
    private String signedLink;
    private String unSignedLink;
    private String slug;
    private Long userId;

}
