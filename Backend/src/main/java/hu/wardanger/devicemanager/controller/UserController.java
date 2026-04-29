package hu.wardanger.devicemanager.controller;

import hu.wardanger.devicemanager.entity.UserAccount;
import hu.wardanger.devicemanager.generated.api.UsersApi;
import hu.wardanger.devicemanager.generated.model.CreateUserRequest;
import hu.wardanger.devicemanager.generated.model.UserLoginRequest;
import hu.wardanger.devicemanager.generated.model.UserSummaryResponse;
import hu.wardanger.devicemanager.mapper.UserMapper;
import hu.wardanger.devicemanager.service.UserAccountService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Data
public class UserController implements UsersApi {

    private final UserAccountService userAccountService;
    private final UserMapper userMapper;

    @Override
    public ResponseEntity<List<UserSummaryResponse>> getUsersByGroup(String groupId) {
        List<UserAccount> users = userAccountService.findUsersByGroup(groupId);

        List<UserSummaryResponse> response = userMapper.toResponseList(users);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<UserSummaryResponse> createUser(String groupId, CreateUserRequest request) {
        UserAccount createdUser = userAccountService.createUser(
                groupId,
                request.getName(),
                request.getPassword()
        );

        UserSummaryResponse response = userMapper.toResponse(createdUser);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @Override
    public ResponseEntity<Void> deleteUser(String groupId, String userId) {
        userAccountService.deleteUserById(userId);

        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<UserSummaryResponse> loginUser(
            String groupId,
            String userId,
            UserLoginRequest request
    ) {
        UserAccount authenticatedUser = userAccountService.authenticateUser(
                userId,
                request.getPassword()
        );

        UserSummaryResponse response = userMapper.toResponse(authenticatedUser);

        return ResponseEntity.ok(response);
    }
}