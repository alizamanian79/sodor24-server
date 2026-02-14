package app.signature.service.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.annotation.Nullable;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ContractMicroRequestDto {

    @JsonIgnore
    private MultipartFile file;

    private String userId;
    private String fileName;
    private String reason;
    private String country;
    private String createDate;
    private String expireDate;

    @Nullable
    private String fileContentBase64;

    public ContractMicroRequestDto(MultipartFile file, String username, String fileName, String reason, String country, String createDate, String expireDate) {
    }
}
