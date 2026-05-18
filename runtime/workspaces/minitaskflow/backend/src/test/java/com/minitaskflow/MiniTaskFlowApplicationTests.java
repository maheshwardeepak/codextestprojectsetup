package com.minitaskflow;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.minitaskflow.users.AppUser;
import com.minitaskflow.users.AuthProvider;
import com.minitaskflow.users.UserRepository;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MiniTaskFlowApplicationTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void healthEndpointIsPublic() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("UP")));
    }

    @Test
    void userCanRegisterCreateProjectCreateAndCompleteTaskThenSeeDashboardCounts() throws Exception {
        String email = "flow-" + System.nanoTime() + "@example.com";
        MvcResult registerResult = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "email", email,
                                "password", "correct-password"
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.user.email", is(email)))
                .andExpect(jsonPath("$.user.authProvider", is("LOCAL")))
                .andExpect(jsonPath("$.user.role", is("USER")))
                .andReturn();

        JsonNode registerJson = objectMapper.readTree(registerResult.getResponse().getContentAsString());
        String token = registerJson.get("token").asText();

        MvcResult projectResult = mockMvc.perform(post("/api/projects")
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("name", "Validation Project"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Validation Project")))
                .andReturn();

        long projectId = objectMapper.readTree(projectResult.getResponse().getContentAsString()).get("id").asLong();

        MvcResult taskResult = mockMvc.perform(post("/api/projects/" + projectId + "/tasks")
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("title", "Run runtime proof"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Run runtime proof")))
                .andExpect(jsonPath("$.completed", is(false)))
                .andReturn();

        long taskId = objectMapper.readTree(taskResult.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(patch("/api/tasks/" + taskId + "/complete")
                        .header("Authorization", bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completed", is(true)));

        mockMvc.perform(get("/api/dashboard/summary")
                        .header("Authorization", bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projectCount", is(1)))
                .andExpect(jsonPath("$.taskCount", is(1)))
                .andExpect(jsonPath("$.completedTaskCount", is(1)));

        mockMvc.perform(get("/api/users/me")
                        .header("Authorization", bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is(email)));
    }

    @Test
    void protectedEndpointsRejectUnauthenticatedRequests() throws Exception {
        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("name", "No token"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void registrationStoresPasswordHashInsteadOfPlaintext() throws Exception {
        String email = "hash-" + System.nanoTime() + "@example.com";
        String password = "correct-password";

        register(email, password);

        AppUser user = userRepository.findByEmailIgnoreCase(email).orElseThrow();
        org.assertj.core.api.Assertions.assertThat(user.getPasswordHash()).isNotEqualTo(password);
        org.assertj.core.api.Assertions.assertThat(passwordEncoder.matches(password, user.getPasswordHash())).isTrue();
    }

    @Test
    void usersCannotAccessOtherUsersProjectsOrTasks() throws Exception {
        String ownerToken = register("owner-" + System.nanoTime() + "@example.com", "correct-password");
        String otherToken = register("other-" + System.nanoTime() + "@example.com", "correct-password");

        long projectId = createProject(ownerToken, "Owner Project");
        long taskId = createTask(ownerToken, projectId, "Owner Task");

        mockMvc.perform(get("/api/projects/" + projectId + "/tasks")
                        .header("Authorization", bearer(otherToken)))
                .andExpect(status().isNotFound());

        mockMvc.perform(post("/api/projects/" + projectId + "/tasks")
                        .header("Authorization", bearer(otherToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("title", "Cross-user write"))))
                .andExpect(status().isNotFound());

        mockMvc.perform(patch("/api/tasks/" + taskId + "/complete")
                        .header("Authorization", bearer(otherToken)))
                .andExpect(status().isNotFound());
    }

    @Test
    void googleLoginUsesStableGoogleSubAndSafeCsrfValidation() throws Exception {
        String email = "google-" + System.nanoTime() + "@example.com";
        MvcResult result = mockMvc.perform(post("/api/auth/google")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "credential", "test-google-token|google-sub-" + System.nanoTime() + "|" + email + "|true",
                                "csrfToken", "minitaskflow-test-csrf"
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.user.email", is(email)))
                .andExpect(jsonPath("$.user.authProvider", is("GOOGLE")))
                .andReturn();

        String token = objectMapper.readTree(result.getResponse().getContentAsString()).get("token").asText();
        AppUser user = userRepository.findByEmailIgnoreCase(email).orElseThrow();
        org.assertj.core.api.Assertions.assertThat(user.getAuthProvider()).isEqualTo(AuthProvider.GOOGLE);
        org.assertj.core.api.Assertions.assertThat(user.getGoogleSub()).startsWith("google-sub-");
        org.assertj.core.api.Assertions.assertThat(user.getPasswordHash()).isNull();

        mockMvc.perform(get("/api/users/me").header("Authorization", bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authProvider", is("GOOGLE")));

        mockMvc.perform(post("/api/auth/google")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "credential", "test-google-token|bad-sub|" + email + "|true",
                                "csrfToken", "wrong-csrf"
                        ))))
                .andExpect(status().isUnauthorized());
    }

    private String register(String email, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "email", email,
                                "password", password
                        ))))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("token").asText();
    }

    private long createProject(String token, String name) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/projects")
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("name", name))))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
    }

    private long createTask(String token, long projectId, String title) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/projects/" + projectId + "/tasks")
                        .header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("title", title))))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }
}
