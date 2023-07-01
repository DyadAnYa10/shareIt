package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ShareItTests {
	@Autowired
	private ShareItApp shareItApp;

	@Test
	void contextLoads() {
	}

	@Test
	void mainTest() {
		shareItApp.main(new String[]{});
	}
}
