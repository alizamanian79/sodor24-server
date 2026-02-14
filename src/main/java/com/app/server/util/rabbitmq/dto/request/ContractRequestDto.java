package app.signature.service.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.annotation.Nullable;
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