package hu.wardanger.devicemanager.dto.request;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValidateGroupAccessRequest {
    private String accessCode;
}