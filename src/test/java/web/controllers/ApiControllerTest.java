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

    private static Long userId;


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
        // Arrange
        Map<String, Object> user = new LinkedHashMap<>();
        user.put("firstName", "testName");
        user.put("lastName", "testLastName");
        user.put("sex", "MALE");
        user.put("age", 25);
        user.put("login", "testLogin");
        user.put("password", "testPassword");
        user.put("email", "testEmail");
        String[] roles = {"ROLE_ADMIN"};
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
    void testSaveCar() throws Exception {
        Map<String, String> car = new LinkedHashMap<>();
        car.put("brand", "Toyota");
        car.put("series", "Corolla");
        car.put("model", "3");
        car.put("color", "White");
        System.out.println(userId);

        mockMvc.perform(post(baseUrl + "/api/saveCar")
                        .param("userId", Long.toString(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(car)))
                .andExpect(status().isCreated());
    }

    @Test
    @Order(4)
    @WithUserDetails("admin")
    void testGetCarCheck1() throws Exception {
        mockMvc.perform(get(baseUrl + "/api/getCar/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.brand").value("Toyota"))
                .andExpect(jsonPath("$.model").value("3"))
                .andExpect(jsonPath("$.series").value("Corolla"))
                .andExpect(jsonPath("$.color").value("White"));
    }

    @Test
    @Order(5)
    @WithUserDetails("admin")
    void testUpdateCar() throws Exception {
        Map<String, String> car = new LinkedHashMap<>();
        car.put("brand", "Mercedes-Benz");
        car.put("series", "SLK");
        car.put("model", "500");
        car.put("color", "Red");
        System.out.println(userId);

        mockMvc.perform(post(baseUrl + "/api/saveCar")
                        .param("userId", Long.toString(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(car)))
                .andExpect(status().isCreated());
    }

    @Test
    @Order(6)
    @WithUserDetails("admin")
    void testGetCarCheck2() throws Exception {
        mockMvc.perform(get(baseUrl + "/api/getCar/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.brand").value("Mercedes-Benz"))
                .andExpect(jsonPath("$.model").value("500"))
                .andExpect(jsonPath("$.series").value("SLK"))
                .andExpect(jsonPath("$.color").value("Red"));
    }

    @Test
    @Order(7)
    @WithUserDetails("admin")
    void testUpdateUser() throws Exception {
        // Arrange
        Map<String, String> userMap = new LinkedHashMap<>();
        userMap.put("id", Long.toString(userId));
        userMap.put("firstName", "testName2");
        userMap.put("lastName", "testLastName2");
        userMap.put("sex", "FEMALE");
        userMap.put("age", "52");
        userMap.put("login", "testLogin2");
        userMap.put("password", "testPassword2");
        userMap.put("email", "testEmail2");

        mockMvc.perform(put(baseUrl + "/api/updateUser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userMap)))
                .andExpect(status().isAccepted());
    }

    @Test
    @Order(8)
    @WithUserDetails("admin")
    void testGetRoles1() throws Exception {
        mockMvc.perform(get(baseUrl + "/api/getRoles/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0]").exists());
    }

    @Test
    @Order(9)
    @WithUserDetails("admin")
    void testUpdateRoles() throws Exception {
        String[] roles = {"ROLE_ADMIN","ROLE_USER"};

        mockMvc.perform(put(baseUrl + "/api/updateRoles/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(roles)))
                .andExpect(status().isAccepted());
    }


    @Test
    @Order(10)
    @WithUserDetails("admin")
    void testGetRoles2() throws Exception {
        mockMvc.perform(get(baseUrl + "/api/getRoles/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0]").exists())
                .andExpect(jsonPath("$[1]").exists());
    }

    @Test
    @Order(11)
    @WithUserDetails("admin")
    void testDeleteCar() throws Exception {
        mockMvc.perform(delete(baseUrl + "/api/deleteCar/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @Order(12)
    @WithUserDetails("admin")
    void testDeleteUser() throws Exception {
        mockMvc.perform(delete(baseUrl + "/api/deleteUser/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @Order(13)
    void testLogout() throws Exception {
        mockMvc.perform(post(baseUrl + "/logout")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isFound());
    }
}