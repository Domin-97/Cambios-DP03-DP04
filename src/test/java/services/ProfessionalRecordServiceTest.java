
package services;

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
import domain.Curriculum;
import domain.ProfessionalRecord;

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


	//Testing findAll on professional record

	@Test
	public void testFindAll() {
		Assert.isTrue(this.professionalRecordService.findAll().size() >= 1);
	}
	//Testing findOne on professionalRecord1

	@Test
	public void testFindOne() {
		Assert.notNull(this.professionalRecordService.findOne(super.getEntityId("professionalRecord1")));
	}
	//Testing creating a professional record succesfully
	@Test
	public void testCreateProfessionalRecord() {
		super.authenticate("handyWorker");
		ProfessionalRecord pro, saved;
		Collection<ProfessionalRecord> pros;

		pro = this.professionalRecordService.create();
		pro.setCompanyName("Apple");
		pro.setRole("Camarero");
		pro.setProAttachment("https://www.apple.com/es/");
		pro.setProStartDate(new Date(System.currentTimeMillis() - 1000));
		pro.setProEndDate(null);
		saved = this.professionalRecordService.save(pro);
		pros = this.professionalRecordService.findAll();
		Assert.isTrue(pros.contains(saved));
	}
	//Testing saving a professional record succesfully
	@Test
	public void testSaveProfessionalRecord() {
		super.authenticate("handyWorker");
		ProfessionalRecord old, clon;
		old = this.professionalRecordService.findOne(super.getEntityId("professionalRecord1"));
		clon = (ProfessionalRecord) old.clone();
		clon.setProEndDate(new Date(System.currentTimeMillis() - 1000));
		old = clon;
		this.professionalRecordService.save(old);
		final Curriculum cur = this.curriculumService.findCurriculumByProRecordId(old.getId());
		super.unauthenticate();
	}
	//Testing saving a professional record expecting a fail
	@Test
	public void testSaveProfessionalRecordFail() {
		super.authenticate("customer");
		ProfessionalRecord old, nuevo;
		old = this.professionalRecordService.findOne(super.getEntityId("professionalRecord1"));
		Assert.notNull(old);
		old.setCompanyName("Marjane");
		this.exception.expect(IllegalArgumentException.class);
		nuevo = this.professionalRecordService.save(old);
		super.unauthenticate();
		Assert.isNull(nuevo);

	}
	//Testing deleting a professional record succesfully
	@Test
	public void testDeleteProfessionalRecord() {
		super.authenticate("handyWorker");
		final int id = super.getEntityId("professionalRecord1");
		final ProfessionalRecord borrado = this.professionalRecordService.findOne(id);
		Assert.notNull(borrado);
		this.professionalRecordService.delete(borrado);
		this.professionalRecordService.flush();
		final Collection<ProfessionalRecord> proRecords = this.professionalRecordService.findAll();
		Assert.isTrue(!proRecords.contains(borrado));
		super.unauthenticate();
	}

}
