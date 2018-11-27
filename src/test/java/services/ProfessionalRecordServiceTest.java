package services;

import javax.transaction.Transactional;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import utilities.AbstractTest;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class ProfessionalRecordServiceTest extends AbstractTest {

	@Autowired
	public CurriculumService			curriculumService;
	@Autowired
	public ActorService					actorService;
	@Autowired
	public ProfessionalRecordService	professionalRecordService;

	@Rule
	public final ExpectedException		exception	= ExpectedException.none();


	@Test
	public void testFindAll() {
		Assert.isTrue(this.professionalRecordService.findAll().size() >= 1);
	}
	@Test
	public void testFindOne() {
		Assert.notNull(this.professionalRecordService.findOne(super.getEntityId("professionalRecord1")));
	}

}
