package com.example.demo;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.jupiter.api.Assumptions.assumingThat;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.condition.EnabledOnJre;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.JRE;
import org.junit.jupiter.api.condition.OS;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class StudyTest {

	@Test
	@Tag("fast")
	@DisplayName("스터디 만들기")
	@EnabledOnJre({JRE.JAVA_17})
	@EnabledOnOs(OS.WINDOWS)
	void create_new_study_assume() {
		assumeTrue("LOCAL".equalsIgnoreCase(System.getenv("TEST_ENV")));
		
		Study actual = new Study(10);
		org.assertj.core.api.Assertions.assertThat(actual.getLimit()).isGreaterThan(0);
		
		
		assumingThat("LOCAL".equalsIgnoreCase(System.getenv("TEST_ENV")), () -> {
			Study study = new Study(10);
			org.assertj.core.api.Assertions.assertThat(study.getLimit()).isGreaterThan(0);
		});
		
	}
	
	@Test
	@Tag("slow")
	@DisplayName("다시 스터디 만들기")
	@EnabledOnOs({OS.WINDOWS, OS.LINUX})
	@EnabledIfEnvironmentVariable(named = "TEST_ENV", matches = "local")
	void create_new_study() {
		Study study = new Study(10);
		assertAll(() -> assertNotNull(study),
			//Supplier로 넘기면 Lazy 연산
			() -> assertEquals(StudyStatus.DRAFT, study.getStatus()
					, () -> "스터디를 처음 만들면 상태값이 " + StudyStatus.DRAFT + "상태다."),
			() -> assertTrue(study.getLimit() > 0, "스터디 최대 참석 가능 인원은 0보다 커야한다.")
		);
	}
	
	@Test
	@DisplayName("스터디 만들기 with AssertJ")
	void create_new_study_with_AssertJ() {
		Study study = new Study(10);
		org.assertj.core.api.Assertions.assertThat(study.getLimit()).isGreaterThan(0);
	}
	
	@Test
	@DisplayName("스터디 참여 인원은 0보다 커야한다.")
	void limit_validation() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new Study(-10));
		String message = exception.getMessage();
		assertEquals("limit은 0보다 커야 한다.", message);
	}
	
	@Test
	@DisplayName("타임아웃 테스트")
	void timeout() {
//		assertTimeout(Duration.ofMillis(100), () -> {
//			new Study(10);
//			Thread.sleep(300L);
//		});
		//별도의 쓰레드에서 테스트하기 때문에 ThreadLocal사용 여부 판단 ex) Spring Security, @Transational
//		assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
//			new Study(10);
//			Thread.sleep(300L);
//		});
	}
	
	@BeforeAll
	static void beforeAll() {
		System.out.println("before all");
	}
	
	@AfterAll
	static void afterAll() {
		System.out.println("after all");
	}
	
	@BeforeEach
	void beforeEach() {
		System.out.println("before each");
	}
	
	@AfterEach
	void afterEach() {
		System.out.println("after each");
	}
}
