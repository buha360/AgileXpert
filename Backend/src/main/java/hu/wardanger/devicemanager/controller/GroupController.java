package hu.wardanger.devicemanager.controller;

import hu.wardanger.devicemanager.entity.UserGroup;
import hu.wardanger.devicemanager.generated.api.GroupsApi;
import hu.wardanger.devicemanager.generated.model.CreateGroupRequest;
import hu.wardanger.devicemanager.generated.model.GroupAccessValidationResponse;
import hu.wardanger.devicemanager.generated.model.GroupSummaryResponse;
import hu.wardanger.devicemanager.generated.model.ValidateGroupAccessRequest;
import hu.wardanger.devicemanager.mapper.GroupMapper;
import hu.wardanger.devicemanager.service.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Data
public class GroupController implements GroupsApi {

    private final GroupService groupService;
    private final GroupMapper groupMapper;

    @Override
    public ResponseEntity<List<GroupSummaryResponse>> getAllGroups() {
        List<UserGroup> groups = groupService.findAllGroups();

        List<GroupSummaryResponse> response = groupMapper.toResponseList(groups);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<GroupSummaryResponse> createGroup(CreateGroupRequest request) {
        UserGroup createdGroup = groupService.registerGroup(
                request.getGroupName(),
                request.getAccessCode(),
                request.getAdminUserName(),
                request.getAdminPassword()
        );

        GroupSummaryResponse response = groupMapper.toResponse(createdGroup);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @Override
    public ResponseEntity<GroupAccessValidationResponse> validateAccessCode(
            String groupId,
            ValidateGroupAccessRequest request
    ) {
        boolean valid = groupService.validateAccessCode(groupId, request.getAccessCode());

        GroupAccessValidationResponse response = new GroupAccessValidationResponse();
        response.setValid(valid);

        if (valid) {
            response.setMessage("Access code is valid.");
        } else {
            response.setMessage("Invalid access code.");
        }

        return ResponseEntity.ok(response);
    }
}