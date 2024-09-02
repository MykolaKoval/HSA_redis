package com.course.hsa;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class ApplicationIT {

    @Autowired
    private ApplicationContext context;

    //@Test
    void contextLoads() {
        assertNotNull(context, "context is null");
    }

}
