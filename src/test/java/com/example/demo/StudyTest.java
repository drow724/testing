package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.jupiter.api.Assumptions.assumingThat;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.condition.EnabledOnJre;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.JRE;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.AggregateWith;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.aggregator.ArgumentsAggregationException;
import org.junit.jupiter.params.aggregator.ArgumentsAggregator;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.converter.SimpleArgumentConverter;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StudyTest {

	int value = 0;
	
	@Test
	@Order(1)
	@DisplayName("스터디 만들기 instance1")
	void create_study_instance1() {
		System.out.println("creat1 " + value++);
	}
	
	@Test
	@Order(2)
	@DisplayName("스터디 만들기 instance2")
	void create_study_instance2() {
		System.out.println("creat2 " + value++);
	}
	
	@Test
	@Order(3)
	void create_study_instance3() {
		System.out.println("creat3 " + value++);
	}
	
	@DisplayName("스터디 만들기 반복")
	@RepeatedTest(value = 10, name = "{displayName}, {currentRepetition}/{totalRepetitions}")
	void repeat_create_new_study(RepetitionInfo repetitionInfo) {
		System.out.println("test " + repetitionInfo.getCurrentRepetition() + "/" + repetitionInfo.getTotalRepetitions());
	}
	
	@DisplayName("스터디 만들기 with value source")
	@ParameterizedTest(name = "{index} {displayName}, message={0}")
	@ValueSource(strings = {"날씨가", "많이", "추워지고", "있네요."})
	@EmptySource
	@NullSource
	@NullAndEmptySource
	void parameterizedTest(String message) {
		System.out.println(message);
	}
	
	@DisplayName("스터디 만들기 with converter")
	@ParameterizedTest(name = "{index} {displayName}, message={0}")
	@ValueSource(ints = {10, 20, 40})
	@CsvSource({"10, '자바 스터디'", "20, '스프링 스터디'"})
	void parameterizedTestWithConverter(@ConvertWith(StudyConverter.class) Study study) {
		System.out.println(study.getLimit());
	}
	
	static class StudyConverter extends SimpleArgumentConverter {

		@Override
		protected Object convert(Object source, Class<?> targetType) throws ArgumentConversionException {
			assertEquals(Study.class, targetType, "Can only convert to Study");
			return new Study(Integer.parseInt(source.toString()));
		}
		
	}
	
	@DisplayName("스터디 만들기 with argumentAccessor")
	@ParameterizedTest(name = "{index} {displayName}, message={0}")
	@CsvSource({"10, '자바 스터디'", "20, '스프링 스터디'"})
	void parameterizedTestWithArgumentsAccessor(ArgumentsAccessor argumentsAccessor) {
		Study study = new Study(argumentsAccessor.getInteger(0), argumentsAccessor.getString(1));
		System.out.println(study);
	}
	
	@DisplayName("스터디 만들기 with argumentsAggregator")
	@ParameterizedTest(name = "{index} {displayName}, message={0}")
	@CsvSource({"10, '자바 스터디'", "20, '스프링 스터디'"})
	void parameterizedTestWithArgumentsAggregator(@AggregateWith(StudyAggregator.class) Study study) {
		System.out.println(study);
	}
	
	static class StudyAggregator implements ArgumentsAggregator {

		@Override
		public Object aggregateArguments(ArgumentsAccessor accessor, ParameterContext context)
				throws ArgumentsAggregationException {
			return new Study(accessor.getInteger(0), accessor.getString(1));
		}
		
	}
	
	@DisplayName("스터디 만들기 with cvs")
	@ParameterizedTest(name = "{index} {displayName}, message={0}")
	@CsvSource({"10, '자바 스터디'", "20, '스프링 스터디'"})
	void parameterizedTestWithCvsAggregate(Integer limit, String name) {
		Study study = new Study(limit, name);
		System.out.println(study);
	}
	
	@FastTest
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
	
	@SlowTest
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
	void beforeAll() {
		System.out.println("before all");
	}
	
	@AfterAll
	void afterAll() {
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
