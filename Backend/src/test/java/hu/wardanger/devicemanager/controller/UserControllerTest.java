package hu.wardanger.devicemanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hu.wardanger.devicemanager.entity.UserAccount;
import hu.wardanger.devicemanager.entity.UserRole;
import hu.wardanger.devicemanager.generated.model.CreateUserRequest;
import hu.wardanger.devicemanager.generated.model.UserLoginRequest;
import hu.wardanger.devicemanager.generated.model.UserSummaryResponse;
import hu.wardanger.devicemanager.mapper.UserMapper;
import hu.wardanger.devicemanager.service.UserAccountService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserAccountService userAccountService;

    @MockitoBean
    private UserMapper userMapper;

    @Test
    @DisplayName("GET /api/groups/{groupId}/users - listázza a group usereit")
    void getUsersByGroup_shouldReturnUsers() throws Exception {
        UserAccount user = new UserAccount();
        user.setId("user-1");
        user.setName("Buha");
        user.setRole(UserRole.ADMIN);

        UserSummaryResponse response = new UserSummaryResponse();
        response.setId("user-1");
        response.setName("Buha");
        response.setRole("ADMIN");

        when(userAccountService.findUsersByGroup("group-1"))
                .thenReturn(List.of(user));
        when(userMapper.toResponseList(List.of(user)))
                .thenReturn(List.of(response));

        mockMvc.perform(get("/api/groups/group-1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("user-1"))
                .andExpect(jsonPath("$[0].name").value("Buha"))
                .andExpect(jsonPath("$[0].role").value("ADMIN"));
    }

    @Test
    @DisplayName("POST /api/groups/{groupId}/users - létrehoz egy usert")
    void createUser_shouldReturnCreatedUser() throws Exception {
        CreateUserRequest request = new CreateUserRequest();
        request.setName("Niki");
        request.setPassword("asd");

        UserAccount createdUser = new UserAccount();
        createdUser.setId("user-2");
        createdUser.setName("Niki");
        createdUser.setRole(UserRole.MEMBER);

        UserSummaryResponse response = new UserSummaryResponse();
        response.setId("user-2");
        response.setName("Niki");
        response.setRole("MEMBER");

        when(userAccountService.createUser("group-1", "Niki", "asd"))
                .thenReturn(createdUser);
        when(userMapper.toResponse(createdUser))
                .thenReturn(response);

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
        UserLoginRequest request = new UserLoginRequest();
        request.setPassword("asd");

        UserAccount authenticatedUser = new UserAccount();
        authenticatedUser.setId("user-2");
        authenticatedUser.setName("Niki");
        authenticatedUser.setRole(UserRole.MEMBER);

        UserSummaryResponse response = new UserSummaryResponse();
        response.setId("user-2");
        response.setName("Niki");
        response.setRole("MEMBER");

        when(userAccountService.authenticateUser("user-2", "asd"))
                .thenReturn(authenticatedUser);
        when(userMapper.toResponse(authenticatedUser))
                .thenReturn(response);

        mockMvc.perform(post("/api/groups/group-1/users/user-2/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("user-2"))
                .andExpect(jsonPath("$.name").value("Niki"))
                .andExpect(jsonPath("$.role").value("MEMBER"));
    }
}