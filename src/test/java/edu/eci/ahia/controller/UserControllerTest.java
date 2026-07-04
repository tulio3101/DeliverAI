package edu.eci.ahia.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import edu.eci.ahia.exception.UserNotFoundException;
import edu.eci.ahia.mapper.UserMapper;
import edu.eci.ahia.model.dto.request.UserRequestDTO;
import edu.eci.ahia.model.dto.response.UserResponseDTO;
import edu.eci.ahia.model.entity.User;
import edu.eci.ahia.service.UserService;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserMapper userMapper;

    @Test
    void createUser_ShouldReturn201() throws Exception {
        UserRequestDTO request = new UserRequestDTO("Alice", "alice@example.com", 1234567890L, null);
        User entity = User.builder().name("Alice").email("alice@example.com").phoneNumber(1234567890L).build();
        User saved = User.builder().id(1L).name("Alice").email("alice@example.com").phoneNumber(1234567890L).build();
        UserResponseDTO response = UserResponseDTO.builder()
            .id(1L).name("Alice").email("alice@example.com").phoneNumber(1234567890L).build();

        when(userMapper.toEntity(any(UserRequestDTO.class))).thenReturn(entity);
        when(userService.createUser(entity)).thenReturn(saved);
        when(userMapper.toDto(saved)).thenReturn(response);

        mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Alice\",\"email\":\"alice@example.com\",\"phoneNumber\":1234567890}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Alice"))
            .andExpect(jsonPath("$.email").value("alice@example.com"));
    }

    @Test
    void createUser_WithEmptyBody_ShouldReturn400() throws Exception {
        mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
            .andExpect(status().isBadRequest());
    }

    @Test
    void createUser_WithInvalidEmail_ShouldReturn400() throws Exception {
        mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Alice\",\"email\":\"invalid-email\",\"phoneNumber\":1234567890}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void getUserById_ShouldReturn200() throws Exception {
        User entity = User.builder().id(1L).name("Alice").email("alice@example.com").phoneNumber(1234567890L).build();
        UserResponseDTO response = UserResponseDTO.builder()
            .id(1L).name("Alice").email("alice@example.com").phoneNumber(1234567890L).build();

        when(userService.getUserById(1L)).thenReturn(entity);
        when(userMapper.toDto(entity)).thenReturn(response);

        mockMvc.perform(get("/user/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Alice"));
    }

    @Test
    void getUserById_WhenUserNotFound_ShouldReturn404() throws Exception {
        when(userService.getUserById(anyLong()))
            .thenThrow(new UserNotFoundException("not found"));

        mockMvc.perform(get("/user/99"))
            .andExpect(status().isNotFound());
    }

    @Test
    void getAllUsers_ShouldReturn200() throws Exception {
        UserResponseDTO user1 = UserResponseDTO.builder().id(1L).name("Alice").build();
        UserResponseDTO user2 = UserResponseDTO.builder().id(2L).name("Bob").build();
        User entity1 = User.builder().id(1L).name("Alice").build();
        User entity2 = User.builder().id(2L).name("Bob").build();

        when(userService.getAllUsers()).thenReturn(List.of(entity1, entity2));
        when(userMapper.toDtoList(List.of(entity1, entity2))).thenReturn(List.of(user1, user2));

        mockMvc.perform(get("/user/all"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.size()").value(2))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    void updateUser_ShouldReturn200() throws Exception {
        UserRequestDTO request = new UserRequestDTO("Alice Updated", "alice@example.com", 1234567890L, null);
        User entity = User.builder().name("Alice Updated").email("alice@example.com").phoneNumber(1234567890L).build();
        User updated = User.builder().id(1L).name("Alice Updated").email("alice@example.com").phoneNumber(1234567890L).build();
        UserResponseDTO response = UserResponseDTO.builder()
            .id(1L).name("Alice Updated").email("alice@example.com").phoneNumber(1234567890L).build();

        when(userMapper.toEntity(any(UserRequestDTO.class))).thenReturn(entity);
        when(userService.updateUser(1L, entity)).thenReturn(updated);
        when(userMapper.toDto(updated)).thenReturn(response);

        mockMvc.perform(put("/user/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Alice Updated\",\"email\":\"alice@example.com\",\"phoneNumber\":1234567890}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Alice Updated"));
    }

    @Test
    void updateUser_WhenUserNotFound_ShouldReturn404() throws Exception {
        when(userMapper.toEntity(any(UserRequestDTO.class)))
            .thenReturn(User.builder().name("Alice").build());
        when(userService.updateUser(anyLong(), any(User.class)))
            .thenThrow(new UserNotFoundException("not found"));

        mockMvc.perform(put("/user/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Alice\",\"email\":\"alice@example.com\",\"phoneNumber\":1234567890}"))
            .andExpect(status().isNotFound());
    }

    @Test
    void deleteUser_ShouldReturn204() throws Exception {
        mockMvc.perform(delete("/user/1"))
            .andExpect(status().isNoContent());
    }

    @Test
    void deleteUser_WhenUserNotFound_ShouldReturn404() throws Exception {
        doThrow(new UserNotFoundException("not found"))
            .when(userService).deleteUser(99L);

        mockMvc.perform(delete("/user/99"))
            .andExpect(status().isNotFound());
    }
}
