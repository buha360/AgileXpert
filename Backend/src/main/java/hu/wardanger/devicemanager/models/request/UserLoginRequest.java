package hu.wardanger.devicemanager.dto.request;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginRequest {
    private String password;
}