package hu.wardanger.devicemanager.controller;

import hu.wardanger.devicemanager.dto.request.CreateGroupRequest;
import hu.wardanger.devicemanager.dto.request.CreateUserRequest;
import hu.wardanger.devicemanager.dto.request.UserLoginRequest;
import hu.wardanger.devicemanager.dto.request.ValidateGroupAccessRequest;
import hu.wardanger.devicemanager.dto.response.GroupAccessValidationResponse;
import hu.wardanger.devicemanager.dto.response.GroupSummaryResponse;
import hu.wardanger.devicemanager.dto.response.UserSummaryResponse;
import hu.wardanger.devicemanager.entity.UserAccount;
import hu.wardanger.devicemanager.entity.UserGroup;
import hu.wardanger.devicemanager.mapper.GroupMapper;
import hu.wardanger.devicemanager.mapper.UserMapper;
import hu.wardanger.devicemanager.service.GroupService;
import hu.wardanger.devicemanager.service.UserAccountService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import lombok.AllArgsConstructor;
import lombok.Data;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(name = "Groups", description = "Endpoints for managing groups and group users")
@RestController
@RequestMapping("/api/groups")
@Data
@AllArgsConstructor
public class GroupController {

    private final GroupService groupService;
    private final UserAccountService userAccountService;
    private final GroupMapper groupMapper;
    private final UserMapper userMapper;

    @Operation(
            summary = "List all groups",
            description = "Returns all registered groups."
    )
    @ApiResponse(responseCode = "200", description = "Groups returned successfully")
    @GetMapping
    public List<GroupSummaryResponse> getAllGroups() {
        List<UserGroup> groups = groupService.findAllGroups();
        return groupMapper.toResponseList(groups);
    }

    @Operation(
            summary = "Create a new group",
            description = "Registers a new group and automatically creates its admin user."
    )
    @ApiResponse(responseCode = "201", description = "Group created successfully")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GroupSummaryResponse createGroup(@RequestBody CreateGroupRequest request) {
        UserGroup createdGroup = groupService.registerGroup(
                request.getGroupName(),
                request.getAccessCode(),
                request.getAdminUserName(),
                request.getAdminPassword()
        );

        return groupMapper.toResponse(createdGroup);
    }

    @Operation(
            summary = "Validate group access code",
            description = "Checks whether the provided access code is valid for the given group."
    )
    @ApiResponse(responseCode = "200", description = "Access code validation finished")
    @PostMapping("/{groupId}/access-validation")
    public GroupAccessValidationResponse validateAccessCode(@PathVariable String groupId,
                                                            @RequestBody ValidateGroupAccessRequest request) {
        boolean valid = groupService.validateAccessCode(groupId, request.getAccessCode());

        if (valid) {
            return new GroupAccessValidationResponse(true, "Az access code helyes.");
        }

        return new GroupAccessValidationResponse(false, "Hibás access code.");
    }

    @Operation(
            summary = "List users of a group",
            description = "Returns all users belonging to the selected group."
    )
    @ApiResponse(responseCode = "200", description = "Users returned successfully")
    @GetMapping("/{groupId}/users")
    public List<UserSummaryResponse> getUsersByGroup(@PathVariable String groupId) {
        List<UserAccount> users = userAccountService.findUsersByGroup(groupId);
        return userMapper.toResponseList(users);
    }

    @Operation(
            summary = "Create user in group",
            description = "Creates a new user inside the selected group."
    )
    @ApiResponse(responseCode = "201", description = "User created successfully")
    @PostMapping("/{groupId}/users")
    @ResponseStatus(HttpStatus.CREATED)
    public UserSummaryResponse createUser(@PathVariable String groupId,
                                          @RequestBody CreateUserRequest request) {
        UserAccount createdUser = userAccountService.createUser(
                groupId,
                request.getName(),
                request.getPassword()
        );

        return userMapper.toResponse(createdUser);
    }

    @Operation(
            summary = "Delete user from group",
            description = "Deletes the selected user from the group."
    )
    @ApiResponse(responseCode = "204", description = "User deleted successfully")
    @DeleteMapping("/{groupId}/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable String groupId, @PathVariable String userId) {
        userAccountService.deleteUserById(userId);
    }

    @Operation(
            summary = "Login user in group",
            description = "Authenticates the selected user with the provided password."
    )
    @ApiResponse(responseCode = "200", description = "User authenticated successfully")
    @PostMapping("/{groupId}/users/{userId}/login")
    public UserSummaryResponse loginUser(@PathVariable String groupId,
                                         @PathVariable String userId,
                                         @RequestBody UserLoginRequest request) {
        UserAccount authenticatedUser = userAccountService.authenticateUser(userId, request.getPassword());
        return userMapper.toResponse(authenticatedUser);
    }
}