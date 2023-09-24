package me.whiteship.inflearnthejavatest.study;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import me.whiteship.inflearnthejavatest.domain.Member;
import me.whiteship.inflearnthejavatest.domain.Study;
import me.whiteship.inflearnthejavatest.member.MemberService;

@ExtendWith(MockitoExtension.class)
public class StudyServiceTest {
	
	@Test
	void createStudyService(@Mock MemberService memberService, @Mock StudyRepository studyRepository) {
	
		StudyService studyService = new StudyService(memberService, studyRepository);
		
		assertNotNull(studyService);
	}
	
	@Test
	void createNewStudy(@Mock MemberService memberService, @Mock StudyRepository studyRepository) {
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
}
