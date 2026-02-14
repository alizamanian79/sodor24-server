package app.signature.service.dto.response;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class ContractResponseDto {

    private String fileName;
    private String unsignedPdf;
    private String signedPdf;
    private String message;

}
