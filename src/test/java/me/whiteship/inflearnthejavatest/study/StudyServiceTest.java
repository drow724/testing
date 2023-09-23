package me.whiteship.inflearnthejavatest.study;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import me.whiteship.inflearnthejavatest.member.MemberService;

@ExtendWith(MockitoExtension.class)
public class StudyServiceTest {
	
	@Test
	void createStudyService(@Mock MemberService memberService, @Mock StudyRepository studyRepository) {
	
		StudyService studyService = new StudyService(memberService, studyRepository);
		
		assertNotNull(studyService);
	}
}
