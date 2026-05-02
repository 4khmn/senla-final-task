package com.project.velo.integration.advertisement;

import com.project.velo.integration.BaseIT;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class CategoryControllerIT extends BaseIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Sql("/sql/advertisement/init_categories.sql")
    void getAllCategories_ShouldReturnAllCategories() throws Exception{

        String expectedJson = Files.readString(Path.of("src/test/resources/json/advertisement/categories_response.json"));

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }
}
