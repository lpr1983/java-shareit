package ru.practicum.shareit.server;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ServerAppTest {

    @Test
    void contextLoads() {
        //
    }

    @Test
    void main() {
        ServerApp.main(new String[]{});
    }
}