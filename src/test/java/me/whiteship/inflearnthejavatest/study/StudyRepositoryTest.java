package me.whiteship.inflearnthejavatest.study;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import me.whiteship.inflearnthejavatest.domain.Study;

@DataJpaTest
public class StudyRepositoryTest {

	@Autowired
	StudyRepository repository;
	
	@Test
	void save() {
		repository.deleteAll();
		Study study = new Study(10, "Java");
		repository.save(study);
		List<Study> all = repository.findAll();
		assertEquals(1,  all.size());
	}
}
