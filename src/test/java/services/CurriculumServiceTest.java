
package services;

import java.util.ArrayList;

import javax.transaction.Transactional;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import security.LoginService;
import utilities.AbstractTest;
import utilities.Tickers;
import domain.Curriculum;
import domain.EducationRecord;
import domain.EndorserRecord;
import domain.HandyWorker;
import domain.MiscellaneousRecord;
import domain.ProfessionalRecord;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class CurriculumServiceTest extends AbstractTest {

	@Autowired
	private CurriculumService		curriculumService;
	@Autowired
	private ActorService			actorService;

	@Rule
	public final ExpectedException	exception	= ExpectedException.none();


	//Testing findAll on curricula
	@Test
	public void testFindAll() {
		Assert.isTrue(this.curriculumService.findAll().size() >= 1);
	}
	//Testing findOne on curriculum1
	@Test
	public void testFindOne() {
		Assert.notNull(this.curriculumService.findOne(super.getEntityId("curriculum1")));
	}
	//Testing creating a saving a curriculum succesfully
	@Test
	public void testCreateAndSaveCurriculum() {
		super.authenticate("handyworker");
		Curriculum cur, clon;
		cur = this.curriculumService.create();
		clon = (Curriculum) cur.clone();
		clon.setTicker(Tickers.generateTicker());
		clon.setEmail("");
		clon.setFullname("");
		clon.setPhoneNumber("");
		clon.setlinkedInProfile("");
		clon.setPhoto("");
		clon.setMisRecord(new ArrayList<MiscellaneousRecord>());
		clon.setEduRecord(new ArrayList<EducationRecord>());
		clon.setProRecord(new ArrayList<ProfessionalRecord>());
		clon.setEndRecord(new ArrayList<EndorserRecord>());
		cur = this.curriculumService.save(cur);
		Assert.notNull(cur);
		super.unauthenticate();

	}
	//Testing updating a curriculum succesfully
	@Test
	public void testSaveCurriculum() {
		Curriculum old, clon;
		super.authenticate("handyWorker");
		old = this.curriculumService.findOne(super.getEntityId("curriculum1"));
		clon = (Curriculum) old.clone();
		clon.setFullname("Curriculum DP ");
		clon.setEmail("Patron@us.es");
		old = clon;
		final Curriculum saved = this.curriculumService.save(old);
		Assert.notNull(saved);
		super.unauthenticate();

	}
	//Testing customer saving a handy worker curriculum expecting a fail
	@Test
	public void testSaveHandyWorkerCurricula() {
		super.authenticate("customer");
		Curriculum old, nuevo;
		old = new ArrayList<Curriculum>(this.curriculumService.findAll()).get(0);
		old.setFullname("Curriculum Nigeria");
		this.exception.expect(IllegalArgumentException.class);
		nuevo = this.curriculumService.save(old);
		super.unauthenticate();
		Assert.isNull(nuevo);
	}
	//Testing updating a record and his corresponding curriculum succesfully 
	@Test
	public void testSaveCurriculumRecord() {
		Curriculum old;
		final Curriculum clon;
		super.authenticate("handyWorker");
		old = this.curriculumService.findOne(super.getEntityId("curriculum1"));
		clon = (Curriculum) old.clone();
		clon.setEduRecord(new ArrayList<EducationRecord>());
		old = clon;
		this.curriculumService.save(old);
		super.unauthenticate();
	}
	//Testing deleting a curriculum succesfully
	@Test
	public void testDeleteCurriculum() {
		super.authenticate("handyworker");
		final HandyWorker hw = (HandyWorker) this.actorService.findByUserAccountId(LoginService.getPrincipal().getId());
		final int id = super.getEntityId("curriculum1");
		Assert.notNull(this.curriculumService.findOne(id));
		final Curriculum borrado = this.curriculumService.findOne(id);
		this.curriculumService.delete(borrado);
		Assert.isTrue(!hw.getCurricula().contains(borrado));
		super.unauthenticate();

	}
	//IN THIS TESTS WE CHECK FINDING A CURRICULUM BY A GIVEN RECORD ID 
	@Test
	public void testFindCurriculumByEduRecordId() {
		Assert.notNull(this.curriculumService.findCurriculumByEduRecordId(super.getEntityId("educationRecord1")));
	}
	@Test
	public void testFindCurriculumByEndRecordId() {
		Assert.notNull(this.curriculumService.findCurriculumByEndRecordId(super.getEntityId("endorserRecord1")));
	}
	@Test
	public void testFindCurriculumByMisRecordId() {
		Assert.notNull(this.curriculumService.findCurriculumByMisRecordId(super.getEntityId("miscellaneousRecord1")));
	}
	@Test
	public void testFindCurriculumByProRecordId() {
		Assert.notNull(this.curriculumService.findCurriculumByProRecordId(super.getEntityId("professionalRecord1")));
	}
}
