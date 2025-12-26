package com.gradeportal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class GradePortalApplication {

    public static void main(String[] args) {
        SpringApplication.run(GradePortalApplication.class, args);
    }
}
