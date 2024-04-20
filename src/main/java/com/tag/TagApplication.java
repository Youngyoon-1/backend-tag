package com.tag;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude={SecurityAutoConfiguration.class})
@Slf4j
public class TagApplication {
    public static void main(String[] args) {
        SpringApplication.run(TagApplication.class, args);
    }
}
