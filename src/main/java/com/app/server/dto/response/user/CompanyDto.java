package com.app.server.dto.response.user;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CompanyDto {

    private Long id;
    private String slug;
    private String website;
    private String companyName;
    private String description;
    private String completeDescriptions;
    private String location;
    private String state;
    private String country;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean active;
    private Boolean valid;
}
