package web;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
class SpringBootSecurityApplicationTests {

    @Autowired
    MockMvc mockMvc;
    String baseUrl = "http://localhost:8082";

    @Test
    void testHomePageAccessibleWithoutAuthentication() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(baseUrl + "/"))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andReturn();

        String redirectUrl = result.getResponse().getRedirectedUrl();

        Assertions.assertEquals("http://localhost:8082/login", redirectUrl);
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void testUserPageAccessibleWithUserAuthentication() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(baseUrl + "/user"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void testManagerPageAccessibleWithUserAuthentication() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(baseUrl + "/manager"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void testAdminPageAccessibleWithUserAuthentication() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(baseUrl + "/admin"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @WithMockUser(username = "manager", roles = "MANAGER")
    void testUserPageAccessibleWithoutManagerAuthentication() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(baseUrl + "/user"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

//    @Test
//    @WithMockUser(username = "manager", roles = "MANAGER")
//    void testManagerPageAccessibleWithoutManagerAuthentication() throws Exception {
//        mockMvc.perform(MockMvcRequestBuilders.get(baseUrl + "/manager"))
//                .andExpect(MockMvcResultMatchers.status().isOk());
//    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testUserPageAccessibleWithAdminAuthentication() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(baseUrl + "/user"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

//    @Test
//    @WithMockUser(username = "admin", roles = "ADMIN")
//    void testManagerPageAccessibleWithAdminAuthentication() throws Exception {
//        mockMvc.perform(MockMvcRequestBuilders.get(baseUrl + "/manager"))
//                .andExpect(MockMvcResultMatchers.status().isOk());
//    }

//    @Test
//    @WithMockUser(username = "admin", roles = "ADMIN")
//    void testAdminPageAccessibleWithAdminAuthentication() throws Exception {
//        mockMvc.perform(MockMvcRequestBuilders.get(baseUrl + "/admin"))
//                .andExpect(MockMvcResultMatchers.status().isOk());
//    }
}
