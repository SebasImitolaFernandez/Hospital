package com.stucom.Hospital;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Disabled("Requiere conexión a la base de datos real; no apto para CI")
@SpringBootTest
class HospitalApplicationTests {

	@Test
	void contextLoads() {
	}
}