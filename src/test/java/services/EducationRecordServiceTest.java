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
import utilities.internal.SchemaPrinter;
import domain.EducationRecord;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class EducationRecordServiceTest extends AbstractTest {

	@Autowired
	public CurriculumService		curriculumService;
	@Autowired
	public ActorService				actorService;
	@Autowired
	public EducationRecordService	educationRecordService;

	@Rule
	public final ExpectedException	exception	= ExpectedException.none();


	@Test
	public void testFindAll() {
		Assert.isTrue(this.educationRecordService.findAll().size() >= 1);
	}
	@Test
	public void testFindOne() {
		Assert.notNull(this.educationRecordService.findOne(super.getEntityId("educationRecord1")));
	}

	@Test
	public void testSaveEducationRecord() {
		EducationRecord old, clon;
		super.authenticate("handyWorker");
		old = this.educationRecordService.findOne(super.getEntityId("educationRecord1"));
		clon = (EducationRecord) old.clone();
		SchemaPrinter.print(clon);
		clon.setEduTitle("Titulo homosterio");
		old = clon;
		SchemaPrinter.print(clon);
		this.educationRecordService.save(old);
		SchemaPrinter.print(this.curriculumService.findCurriculumByEduRecordId(old.getId()).getEduRecord());
		super.unauthenticate();

	}

}
