package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import me.whiteship.inflearnthejavatest.App;

@SpringBootTest
@ContextConfiguration(classes = App.class)
class AppTests {

	@Test
	void contextLoads() {
	}

}
