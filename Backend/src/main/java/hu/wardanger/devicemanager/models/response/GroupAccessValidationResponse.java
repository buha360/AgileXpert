package hu.wardanger.devicemanager.dto.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupAccessValidationResponse {
    private boolean valid;
    private String message;
}