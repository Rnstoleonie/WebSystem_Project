package com.gradeportal;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(locations = "classpath:application.properties")
class GradePortalApplicationTests {

    @Test
    void contextLoads() {
        // Test that the application context loads successfully
    }
}
