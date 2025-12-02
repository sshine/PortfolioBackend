package org.ek.portfoliobackend.service;

import org.ek.portfoliobackend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class UserIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void contextLoads() {
        // This test will pass if the application context loads successfully
        assertThat(userService).isNotNull();
        assertThat(userRepository).isNotNull();
    }
}
