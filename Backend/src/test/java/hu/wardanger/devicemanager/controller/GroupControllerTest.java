package hu.wardanger.devicemanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hu.wardanger.devicemanager.dto.request.CreateGroupRequest;
import hu.wardanger.devicemanager.dto.request.CreateUserRequest;
import hu.wardanger.devicemanager.dto.request.UserLoginRequest;
import hu.wardanger.devicemanager.dto.request.ValidateGroupAccessRequest;
import hu.wardanger.devicemanager.dto.response.GroupSummaryResponse;
import hu.wardanger.devicemanager.dto.response.UserSummaryResponse;
import hu.wardanger.devicemanager.entity.UserAccount;
import hu.wardanger.devicemanager.entity.UserGroup;
import hu.wardanger.devicemanager.entity.UserRole;
import hu.wardanger.devicemanager.mapper.GroupMapper;
import hu.wardanger.devicemanager.mapper.UserMapper;
import hu.wardanger.devicemanager.service.GroupService;
import hu.wardanger.devicemanager.service.UserAccountService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GroupController.class)
class GroupControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private GroupService groupService;

    @MockitoBean
    private UserAccountService userAccountService;

    @MockitoBean
    private GroupMapper groupMapper;

    @MockitoBean
    private UserMapper userMapper;

    @Test
    @DisplayName("GET /api/groups - listázza a groupokat")
    void getAllGroups_shouldReturnGroups() throws Exception {
        UserGroup group = new UserGroup();
        group.setId("group-1");
        group.setName("Buha Family");

        GroupSummaryResponse response = new GroupSummaryResponse("group-1", "Buha Family");

        when(groupService.findAllGroups()).thenReturn(List.of(group));
        when(groupMapper.toResponseList(List.of(group))).thenReturn(List.of(response));

        mockMvc.perform(get("/api/groups"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("group-1"))
                .andExpect(jsonPath("$[0].name").value("Buha Family"));
    }

    @Test
    @DisplayName("POST /api/groups - létrehoz egy új groupot")
    void createGroup_shouldReturnCreatedGroup() throws Exception {
        CreateGroupRequest request = new CreateGroupRequest("Test Family", "asd", "Buha", "1234");

        UserGroup createdGroup = new UserGroup();
        createdGroup.setId("group-1");
        createdGroup.setName("Test Family");

        GroupSummaryResponse response = new GroupSummaryResponse("group-1", "Test Family");

        when(groupService.registerGroup("Test Family", "asd", "Buha", "1234")).thenReturn(createdGroup);
        when(groupMapper.toResponse(createdGroup)).thenReturn(response);

        mockMvc.perform(post("/api/groups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("group-1"))
                .andExpect(jsonPath("$.name").value("Test Family"));
    }

    @Test
    @DisplayName("POST /api/groups/{groupId}/access-validation - helyes access code")
    void validateAccessCode_shouldReturnValidTrue() throws Exception {
        ValidateGroupAccessRequest request = new ValidateGroupAccessRequest("asd");

        when(groupService.validateAccessCode("group-1", "asd")).thenReturn(true);

        mockMvc.perform(post("/api/groups/group-1/access-validation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.message").value("Az access code helyes."));
    }

    @Test
    @DisplayName("POST /api/groups/{groupId}/access-validation - hibás access code")
    void validateAccessCode_shouldReturnValidFalse() throws Exception {
        ValidateGroupAccessRequest request = new ValidateGroupAccessRequest("wrong");

        when(groupService.validateAccessCode("group-1", "wrong")).thenReturn(false);

        mockMvc.perform(post("/api/groups/group-1/access-validation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(false))
                .andExpect(jsonPath("$.message").value("Hibás access code."));
    }

    @Test
    @DisplayName("GET /api/groups/{groupId}/users - listázza a usereket")
    void getUsersByGroup_shouldReturnUsers() throws Exception {
        UserAccount user = new UserAccount();
        user.setId("user-1");
        user.setName("Buha");
        user.setRole(UserRole.ADMIN);

        UserSummaryResponse response = new UserSummaryResponse("user-1", "Buha", "ADMIN");

        when(userAccountService.findUsersByGroup("group-1")).thenReturn(List.of(user));
        when(userMapper.toResponseList(List.of(user))).thenReturn(List.of(response));

        mockMvc.perform(get("/api/groups/group-1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("user-1"))
                .andExpect(jsonPath("$[0].name").value("Buha"))
                .andExpect(jsonPath("$[0].role").value("ADMIN"));
    }

    @Test
    @DisplayName("POST /api/groups/{groupId}/users - létrehoz usert")
    void createUser_shouldReturnCreatedUser() throws Exception {
        CreateUserRequest request = new CreateUserRequest("Niki", "asd");

        UserAccount createdUser = new UserAccount();
        createdUser.setId("user-2");
        createdUser.setName("Niki");
        createdUser.setRole(UserRole.MEMBER);

        UserSummaryResponse response = new UserSummaryResponse("user-2", "Niki", "MEMBER");

        when(userAccountService.createUser("group-1", "Niki", "asd")).thenReturn(createdUser);
        when(userMapper.toResponse(createdUser)).thenReturn(response);

        mockMvc.perform(post("/api/groups/group-1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("user-2"))
                .andExpect(jsonPath("$.name").value("Niki"))
                .andExpect(jsonPath("$.role").value("MEMBER"));
    }

    @Test
    @DisplayName("DELETE /api/groups/{groupId}/users/{userId} - törli a usert")
    void deleteUser_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/groups/group-1/users/user-2"))
                .andExpect(status().isNoContent());

        verify(userAccountService).deleteUserById("user-2");
    }

    @Test
    @DisplayName("POST /api/groups/{groupId}/users/{userId}/login - bejelentkeztet usert")
    void loginUser_shouldReturnAuthenticatedUser() throws Exception {
        UserLoginRequest request = new UserLoginRequest("asd");

        UserAccount authenticatedUser = new UserAccount();
        authenticatedUser.setId("user-2");
        authenticatedUser.setName("Niki");
        authenticatedUser.setRole(UserRole.MEMBER);

        UserSummaryResponse response = new UserSummaryResponse("user-2", "Niki", "MEMBER");

        when(userAccountService.authenticateUser("user-2", "asd")).thenReturn(authenticatedUser);
        when(userMapper.toResponse(authenticatedUser)).thenReturn(response);

        mockMvc.perform(post("/api/groups/group-1/users/user-2/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("user-2"))
                .andExpect(jsonPath("$.name").value("Niki"))
                .andExpect(jsonPath("$.role").value("MEMBER"));
    }
}