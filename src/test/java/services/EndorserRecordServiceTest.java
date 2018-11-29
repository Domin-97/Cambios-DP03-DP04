
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
import domain.Curriculum;
import domain.EndorserRecord;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class EndorserRecordServiceTest extends AbstractTest {

	@Autowired
	private EndorserRecordService	endorserRecordService;
	@Autowired
	private CurriculumService		curriculumService;
	@Rule
	public final ExpectedException	exception	= ExpectedException.none();


	//Testing findAll on endorser record
	@Test
	public void testFindAll() {
		Assert.isTrue(this.endorserRecordService.findAll().size() >= 1);
	}
	//Testing findOne on educationRecord1
	@Test
	public void testFindOne() {
		Assert.notNull(this.endorserRecordService.findOne(super.getEntityId("endorserRecord1")));
	}
	//Testing creating an education record succesfully
	@Test
	public void testCreateEndorserRecord() {
		super.authenticate("handyWorker");
		EndorserRecord end, saved;
		Collection<EndorserRecord> ends;

		end = this.endorserRecordService.create();
		end.setFullname("CEO de Kebab");
		end.setLinkedInProfile("https://ec.linkedin.com/in/mauricio-intriago-824504137");
		end.setEmail("kebadMaster@us.es");
		end.setPhoneNumber("+34 52542525252");
		end.setEndComment(new ArrayList<String>());
		saved = this.endorserRecordService.save(end);
		ends = this.endorserRecordService.findAll();
		Assert.isTrue(ends.contains(saved));
	}
	//Testing saving an endorser record succesfully
	@Test
	public void testSaveEndorserRecord() {
		EndorserRecord old, clon;
		super.authenticate("handyWorker");
		old = this.endorserRecordService.findOne(super.getEntityId("endorserRecord1"));
		clon = (EndorserRecord) old.clone();
		clon.setFullname("CEO de fontanería de la US");
		old = clon;
		this.endorserRecordService.save(old);
		final Curriculum cur = this.curriculumService.findCurriculumByEndRecordId(old.getId());
		super.unauthenticate();

	}
	//Testing saving an endorser record expecting a fail
	@Test
	public void testSaveEndorserRecordFail() {
		super.authenticate("customer");
		EndorserRecord old, nuevo;
		old = this.endorserRecordService.findOne(super.getEntityId("endorserRecord2"));
		Assert.notNull(old);
		old.setLinkedInProfile("https://www.comuniazo.com/");
		this.exception.expect(IllegalArgumentException.class);
		nuevo = this.endorserRecordService.save(old);
		super.unauthenticate();
		Assert.isNull(nuevo);
	}
	//Testing deleting an endorser record succesfully
	@Test
	public void testDeleteEndorserRecord() {
		super.authenticate("handyWorker");
		final int id = super.getEntityId("endorserRecord1");
		final EndorserRecord borrado = this.endorserRecordService.findOne(id);
		Assert.notNull(borrado);
		this.endorserRecordService.delete(borrado);
		this.endorserRecordService.flush();
		final Collection<EndorserRecord> endRecords = this.endorserRecordService.findAll();
		Assert.isTrue(!endRecords.contains(borrado));
		super.unauthenticate();
	}
}
