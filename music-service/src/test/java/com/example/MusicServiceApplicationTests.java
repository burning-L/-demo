package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.DigestUtils;


import java.nio.charset.StandardCharsets;

@SpringBootTest
class MusicServiceApplicationTests {

    @Test
    void contextLoads() {
        String regex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}(?:\\.[a-zA-Z]{2,})?$";
        System.out.println("2212155@qq.com".matches(regex));
    }

}
