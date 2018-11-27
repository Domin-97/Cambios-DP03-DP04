package services;

import java.util.ArrayList;
import java.util.Collection;

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
import domain.Curriculum;
import domain.EducationRecord;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class CurriculumServiceTest extends AbstractTest {

	@Autowired
	public CurriculumService		curriculumService;
	@Autowired
	public ActorService				actorService;
	@Autowired
	public HandyWorkerService		handyWorkerService;

	@Rule
	public final ExpectedException	exception	= ExpectedException.none();


	@Test
	public void testFindAll() {
		Assert.isTrue(this.curriculumService.findAll().size() >= 1);
	}

	@Test
	public void testFindOne() {
		Assert.notNull(this.curriculumService.findOne(6098));
	}
	@Test
	public void testCreateCurriculum() {
		super.authenticate("handyworker");
		final Curriculum result = new Curriculum();

		result.setEmail("");
		result.setEduRecord(new ArrayList<EducationRecord>());
	}
	//LOGRAR MODIFICAR UN CURRICULUM
	@Test
	public void testSaveCurriculum() {
		Curriculum old, clon;

		old = this.curriculumService.findOne(6098);
		clon = (Curriculum) old.clone();
		SchemaPrinter.print(clon);
		super.authenticate("handyWorker");
		clon.setFullname("Curriculum DP ");
		old = clon;
		SchemaPrinter.print(clon);
		this.curriculumService.save(old);
		super.unauthenticate();

	}
	//UN CUSTOMER NO PUEDE GUARDAR UN CURRICULUM
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
	//SE MODIFICA SI CAMBIAMOS EL RECORD 
	@Test
	public void testSaveCurriculumRecord() {
		Curriculum old, nuevo;

		old = this.curriculumService.findOne(6098);
		SchemaPrinter.print(old);
		super.authenticate("handyWorker");
		final Collection<EducationRecord> eduRecord = old.getEduRecord();
		old.setEduRecord(new ArrayList<EducationRecord>());
		nuevo = this.curriculumService.save(old);
		SchemaPrinter.print(nuevo);
		Assert.isTrue(!eduRecord.equals(nuevo.getEduRecord()));
		super.unauthenticate();
	}
	@Test
	public void testDeleteCurriculum() {
		super.authenticate("handyworker");
		Assert.notNull(this.curriculumService.findOne(6098));
		final Curriculum borrado = this.curriculumService.findOne(6098);
		this.curriculumService.delete(borrado);
		final Collection<Curriculum> curricula = this.curriculumService.findAll();
		Assert.isTrue(!curricula.contains(borrado));
		Assert.isTrue(this.curriculumService.findAll().size() >= 1);
		super.unauthenticate();

	}
}
