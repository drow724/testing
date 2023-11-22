package me.whiteship.inflearnthejavatest.study;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.Optional;

import org.junit.ClassRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import lombok.extern.slf4j.Slf4j;
import me.whiteship.inflearnthejavatest.domain.Member;
import me.whiteship.inflearnthejavatest.domain.Study;
import me.whiteship.inflearnthejavatest.domain.StudyStatus;
import me.whiteship.inflearnthejavatest.member.MemberService;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
@ExtendWith(MockitoExtension.class)
@ContextConfiguration(initializers = StudyServiceTest.ContainerPropertyInitializer.class)
public class StudyServiceTest {
	
	//static Logger LOGGER = LoggerFactory.getLogger(StudyServiceTest.class);
	
	@Mock
	MemberService memberService;
	
	@Autowired
	StudyRepository studyRepository;
	
	@Autowired
	Environment environment;

	@Value("${container.port}")
	int port;
	
//	@Container
//	static GenericContainer<?> postgreSQLContainer = new GenericContainer<>(DockerImageName.parse("postgres"))
//			.withExposedPorts(5432)
//			.withEnv("POSTGRES_HOST_AUTH_METHOD", "trust")
//			.withEnv("POSTGRES_DB", "studytest")
//			//.withEnv("POSTGRES_PASSWORD", "studytest")
//			.waitingFor(Wait.forListeningPort());
//			//.waitingFor(Wait.forHttp("/hello"))

	@Container
	static DockerComposeContainer composeContainer =
			new DockerComposeContainer(new File("src/test/resources/docker-compose.yml"))
					.withExposedService("study-db", 5432);

	static {
		composeContainer.start();
	}
	
//	@BeforeAll
//	static void beforeAll() {
//		Slf4jLogConsumer logConsumer = new Slf4jLogConsumer(log);
//		postgreSQLContainer.followOutput(logConsumer);
//	}
//
//	@BeforeEach
//	void beforeEach() {
//		System.out.println("mapped port = " + environment.getProperty("container.port"));
//		System.out.println("port = " + port);
//		System.out.println(postgreSQLContainer.getLogs());
//		studyRepository.deleteAll();
//	}
	
	@Test
	void createStudyService(@Mock MemberService memberService, @Mock StudyRepository studyRepository) {
	
		StudyService studyService = new StudyService(memberService, studyRepository);
		
		assertNotNull(studyService);
	}
	
	@Test
	void createNewStudy(@Mock MemberService memberService, @Mock StudyRepository studyRepository) {
		System.out.println(" ====== ");
		System.out.println(port);

		StudyService studyService = new StudyService(memberService, studyRepository);
		assertNotNull(studyService);
		
		Member member = new Member();
		member.setId(1L);
		member.setEmail("keesun@email.com");
		
		when(memberService.findById(any())).thenReturn(Optional.of(member));

		Study study = new Study(10, "Java");
		

		studyService.createNewStudy(1L, study);
		
		assertEquals("keesun@email.com", memberService.findById(1L).get().getEmail());
		assertEquals("keesun@email.com", memberService.findById(2L).get().getEmail());
		
		doThrow(new IllegalArgumentException()).when(memberService).validate(1L);
		
		assertThrows(IllegalArgumentException.class, () -> {
			memberService.validate(1L);
		});
		
		when(memberService.findById(any())).thenReturn(Optional.of(member)).thenThrow(new RuntimeException()).thenReturn(Optional.empty());
		
		Optional<Member> byId = memberService.findById(1L);
		
		assertEquals("keesun@email.com", byId.get().getEmail());
		
		assertThrows(RuntimeException.class, () -> {
			memberService.findById(1L);
		});
		
		assertTrue(memberService.findById(1L).isEmpty());
	
	}
	
	@Test
	void stubbingQuiz(@Mock MemberService memberService, @Mock StudyRepository studyRepository) {
		StudyService studyService = new StudyService(memberService, studyRepository);
		assertNotNull(studyService);
		
		Member member = new Member();
		member.setId(1L);
		member.setEmail("keesun@email.com");
		
		when(memberService.findById(1L)).thenReturn(Optional.of(member));
		
		Study study = new Study(10, "테스트");
		
		when(studyRepository.save(study)).thenReturn(study);
		
		studyService.createNewStudy(1L, study);
		
		assertNotNull(study.getOwnerId());
		assertEquals(member.getId(), study.getOwnerId());
	}
	
	@Test
	void verifying(@Mock MemberService memberService, @Mock StudyRepository studyRepository) {
		StudyService studyService = new StudyService(memberService, studyRepository);
		assertNotNull(studyService);
		
		Member member = new Member();
		member.setId(1L);
		member.setEmail("keesun@email.com");
		
		when(memberService.findById(1L)).thenReturn(Optional.of(member));
		
		Study study = new Study(10, "테스트");
		
		when(studyRepository.save(study)).thenReturn(study);
		
		studyService.createNewStudy(1L, study);
		
		assertEquals(member.getId(), study.getOwnerId());
		
		verify(memberService, times(1)).notify(member);
		verify(memberService, times(1)).notify(study);
		verify(memberService, never()).validate(any());
		
		InOrder inOrder = inOrder(memberService);
		
		inOrder.verify(memberService).notify(study);
		inOrder.verify(memberService).notify(member);
		

		verifyNoMoreInteractions(memberService);
	}
	
	@DisplayName("should~~")
	@Test
	void behaviorDrivenDevelopement(@Mock MemberService memberService, @Mock StudyRepository studyRepository) {
		//Given
		StudyService studyService = new StudyService(memberService, studyRepository);
		assertNotNull(studyService);
		
		Member member = new Member();
		member.setId(1L);
		member.setEmail("keesun@email.com");
		
		//when(memberService.findById(1L)).thenReturn(Optional.of(member));
		given(memberService.findById(1L)).willReturn(Optional.of(member));
		
		Study study = new Study(10, "테스트");
		
		//when(studyRepository.save(study)).thenReturn(study);
		given(studyRepository.save(study)).willReturn(study);
		
		//When
		studyService.createNewStudy(1L, study);
		
		//Then
		assertEquals(member.getId(), study.getOwnerId());
		
		//verify(memberService, times(1)).notify(study);
		then(memberService).should(times(1)).notify(study);
		
		//verify(memberService, times(1)).notify(member);
		then(memberService).should(times(1)).notify(member);
		
		then(memberService).shouldHaveNoMoreInteractions();
	}
	
	@DisplayName("다른 사용자가 볼 수 있도록 스터디를 공개한다.")
	@Test
	void openStudy(@Mock MemberService memberService, @Mock StudyRepository studyRepository) {
		//Given
		StudyService studyService = new StudyService(memberService, studyRepository);
		assertNotNull(studyService);
		
		Study study = new Study(10, "테스트");
		assertNull(study.getOpenedDateTime());
		
		//when(studyRepository.save(study)).thenReturn(study);
		given(studyRepository.save(study)).willReturn(study);
		
		//When
		studyService.openStudy(study);
		
		//Then
		assertEquals(study.getStatus(), StudyStatus.OPENED);
		assertNotNull(study.getOpenedDateTime());
		then(memberService).should(times(1)).notify(study);
	}

	static class ContainerPropertyInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

		@Override
		public void initialize(ConfigurableApplicationContext context) {
			TestPropertyValues.of("container.port=" + composeContainer.getServicePort("study-db", 5432))
					.applyTo(context);
		}
	}

}
