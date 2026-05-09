package com.project.velo.integration.profile;

import com.project.velo.integration.BaseIT;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ProfileActivityControllerIT extends BaseIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Sql("/sql/profile/init_profiles.sql")
    void getMySales_shouldReturnMySales() throws Exception {

        String expectedJson = Files.readString(Path.of("src/test/resources/json/profile/profile_sales_private_response.json"));

        mockMvc.perform(get("/api/profiles/my/sales")
                        .with(user("owner_user")))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    @Sql("/sql/profile/init_profiles.sql")
    void getUserSales_shouldReturnUserSalesPublic() throws Exception {

        String expectedJson = Files.readString(Path.of("src/test/resources/json/profile/profile_sales_public_response.json"));

        mockMvc.perform(get("/api/profiles/{username}/sales", "third_user"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    @Sql("/sql/profile/init_profiles.sql")
    void getMyPurchases_shouldReturnMyPurchases() throws Exception {

        String expectedJson = Files.readString(Path.of("src/test/resources/json/profile/profile_purchases_response.json"));

        mockMvc.perform(get("/api/profiles/my/purchases")
                .with(user("other_user")))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    @Sql("/sql/profile/init_profiles.sql")
    void getMyActiveAdvertisements_shouldReturnMyActiveAdvertisements() throws Exception {
        String expectedJson = Files.readString(Path.of("src/test/resources/json/profile/profile_ads_my_response.json"));

        mockMvc.perform(get("/api/profiles/my/advertisements")
                        .with(user("owner_user")))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    @Sql("/sql/profile/init_profiles.sql")
    void getUserActiveAdvertisements_shouldReturn404WhenUserNotFound() throws Exception {

        mockMvc.perform(get("/api/profiles/non-existent/sales"))
                .andExpect(status().isNotFound());
    }
}
