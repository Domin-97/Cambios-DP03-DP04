
package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

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
import domain.EducationRecord;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class EducationRecordServiceTest extends AbstractTest {

	@Autowired
	private CurriculumService		curriculumService;
	@Autowired
	private EducationRecordService	educationRecordService;

	@Rule
	public final ExpectedException	exception	= ExpectedException.none();


	//Testing findAll on education record
	@Test
	public void testFindAll() {
		Assert.isTrue(this.educationRecordService.findAll().size() >= 1);
	}
	//Testing findOne on educationRecord1
	@Test
	public void testFindOne() {
		Assert.notNull(this.educationRecordService.findOne(super.getEntityId("educationRecord1")));
	}
	//Testing creating an education record succesfully
	@Test
	public void testCreateEducationRecord() {
		super.authenticate("handyWorker");
		EducationRecord ed, saved;
		Collection<EducationRecord> educations;
		final String x = "Lo realice en 2 meses";
		final Collection<String> xd = new ArrayList<String>();
		xd.add(x);

		ed = this.educationRecordService.create();
		ed.setEduTitle("Curso en Pythuon");
		ed.setInstitution("StackOverFlow");
		ed.setEduStartDate(new Date(System.currentTimeMillis() - 1000));
		ed.setEduEndDate(null);
		ed.setEduAttachment("https://stackoverflow.com/");
		ed.setComments(xd);
		saved = this.educationRecordService.save(ed);
		educations = this.educationRecordService.findAll();
		Assert.isTrue(educations.contains(saved));
		super.unauthenticate();
	}
	//Testing saving an education record succesfully
	@Test
	public void testSaveEducationRecord() {
		EducationRecord old, clon;
		super.authenticate("handyWorker");
		old = this.educationRecordService.findOne(super.getEntityId("educationRecord1"));
		clon = (EducationRecord) old.clone();
		clon.setEduTitle("Titulo homosterio");
		old = clon;
		this.educationRecordService.save(old);
		super.unauthenticate();

	}
	//Testing saving an education record expecting a fail
	@Test
	public void testSaveEducationRecordFail() {
		super.authenticate("customer");
		EducationRecord old, nuevo;
		old = this.educationRecordService.findOne(super.getEntityId("educationRecord1"));
		Assert.notNull(old);
		old.setEduEndDate(new Date(System.currentTimeMillis() - 1000));
		this.exception.expect(IllegalArgumentException.class);
		nuevo = this.educationRecordService.save(old);
		super.unauthenticate();
		Assert.isNull(nuevo);
	}
	//Testing deleting an education record succesfully
	@Test
	public void testDeleteEducationRecord() {
		super.authenticate("handyWorker");
		final int id = super.getEntityId("educationRecord1");
		final EducationRecord borrado = this.educationRecordService.findOne(id);
		Assert.notNull(borrado);
		this.educationRecordService.delete(borrado);
		this.educationRecordService.flush();
		final Collection<EducationRecord> eduRecord = this.educationRecordService.findAll();
		Assert.isTrue(!eduRecord.contains(borrado));
		super.unauthenticate();

	}
}
