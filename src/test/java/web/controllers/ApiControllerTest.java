package web.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    String baseUrl = "http://localhost:8082";

    private static long userId;


    @Test
    @Order(1)
    void testLogin() throws Exception {
        mockMvc.perform(post(baseUrl + "/login")
                        .param("username", "admin")
                        .param("password", "admin"))
                .andExpect(status().isFound());
    }

    @Test
    @Order(2)
    @WithUserDetails("admin")
    void testSaveUser() throws Exception {
        Map<String, Object> user = new LinkedHashMap<>();
        user.put("firstName", "testName");
        user.put("lastName", "testLastName");
        user.put("sex", "MALE");
        user.put("age", 25);
        user.put("login", "testLogin");
        user.put("password", "testPassword");
        user.put("email", "testEmail");
        String[] roles = {"ADMIN", "USER"};
        Map<String, Object> combinedData = new HashMap<>();
        combinedData.put("user", user);
        combinedData.put("roles", roles);


        MvcResult result = mockMvc.perform(post(baseUrl + "/api/saveUser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(combinedData)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value(user.get("firstName")))
                .andExpect(jsonPath("$.lastName").value(user.get("lastName")))
                .andExpect(jsonPath("$.age").value(user.get("age")))
                .andExpect(jsonPath("$.login").value(user.get("login")))
                .andExpect(jsonPath("$.email").value(user.get("email")))
                .andReturn();

        String response = result.getResponse().getContentAsString();
        JsonNode jsonNode = new ObjectMapper().readTree(response);
        ApiControllerTest.userId = jsonNode.get("id").asLong();
        System.out.println(userId);
    }

    @Test
    @Order(3)
    @WithUserDetails("admin")
    void testUpdateUser() throws Exception {
        Map<String, String> user = new HashMap<>();

        user.put("id", Long.toString(userId));
        user.put("firstName", "testName2");
        user.put("lastName", "testLastName2");
        user.put("sex", "FEMALE");
        user.put("age", "52");
        user.put("login", "testLogin2");
        user.put("password", "testPassword2");
        user.put("email", "testEmail2");
        String[] roles = {"ADMIN"};
        Map<String, Object> combinedData = new HashMap<>();
        combinedData.put("user", user);
        combinedData.put("roles", roles);

        mockMvc.perform(put(baseUrl + "/api/updateUser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(combinedData)))
                .andExpect(status().isAccepted());
    }

    @Test
    @Order(4)
    @WithUserDetails("admin")
    void testUpdateRoles() throws Exception {
        Map<String, String> user = new HashMap<>();
        user.put("id", Long.toString(userId));
        user.put("firstName", "testName2");
        user.put("lastName", "testLastName2");
        user.put("sex", "FEMALE");
        user.put("age", "52");
        user.put("login", "testLogin2");
        user.put("password", "testPassword2");
        user.put("email", "testEmail2");
        String[] roles = {"ADMIN", "USER", "MODERATOR"};
        Map<String, Object> combinedData = new HashMap<>();
        combinedData.put("user", user);
        combinedData.put("roles", roles);


        mockMvc.perform(put(baseUrl + "/api/updateUser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(combinedData)))
                .andExpect(status().isAccepted());
    }


    @Test
    @Order(5)
    @WithUserDetails("admin")
    void testCheckUpdates() throws Exception {
        mockMvc.perform(get(baseUrl + "/api/getUser/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("testName2"))
                .andExpect(jsonPath("$.lastName").value("testLastName2"))
                .andExpect(jsonPath("$.age").value("52"))
                .andExpect(jsonPath("$.login").value("testLogin2"))
                .andExpect(jsonPath("$.email").value("testEmail2"))
                .andExpect(jsonPath("$.roles").isArray())
                .andExpect(jsonPath("$.roles").value(hasSize(3)));
    }

    @Test
    @Order(6)
    @WithUserDetails("admin")
    void testDeleteUser() throws Exception {
        mockMvc.perform(delete(baseUrl + "/api/deleteUser/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @Order(7)
    void testLogout() throws Exception {
        mockMvc.perform(post(baseUrl + "/api/logout")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isFound());
    }
}