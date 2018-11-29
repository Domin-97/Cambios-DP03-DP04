
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
import domain.MiscellaneousRecord;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class MiscellaneousRecordServiceTest extends AbstractTest {

	@Autowired
	private MiscellaneousRecordService	miscellaneousRecordService;
	@Autowired
	private CurriculumService			curriculumService;
	@Rule
	public final ExpectedException		exception	= ExpectedException.none();


	//Testing findAll on miscellaneous record
	@Test
	public void testFindAll() {
		Assert.isTrue(this.miscellaneousRecordService.findAll().size() >= 1);

	}
	//Testing findOne on miscellaneousRecord1
	@Test
	public void testFindOne() {
		Assert.notNull(this.miscellaneousRecordService.findOne(super.getEntityId("miscellaneousRecord1")));
	}
	//Testing creating a miscellaneous record succesfully
	@Test
	public void testCreateMiscellaneousRecord() {
		super.authenticate("handyWorker");
		MiscellaneousRecord mis, saved;
		Collection<MiscellaneousRecord> miscellaneous;
		final String x = "He viajado a Marruecos";
		final Collection<String> xd = new ArrayList<String>();
		xd.add(x);

		mis = this.miscellaneousRecordService.create();
		mis.setMisTitle("Se hablar arabe");
		mis.setAttachment(null);
		mis.setComments(xd);
		saved = this.miscellaneousRecordService.save(mis);
		miscellaneous = this.miscellaneousRecordService.findAll();
		Assert.isTrue(miscellaneous.contains(saved));
		super.unauthenticate();

	}
	//Testing saving a miscellaneous record succesfully
	@Test
	public void testSaveMiscellaneousRecord() {
		MiscellaneousRecord old, clon;
		super.authenticate("handyWorker");
		old = this.miscellaneousRecordService.findOne(super.getEntityId("miscellaneousRecord1"));
		clon = (MiscellaneousRecord) old.clone();
		clon.setMisTitle("Se hablar arameo");
		old = clon;
		this.miscellaneousRecordService.save(old);
		final Curriculum cur = this.curriculumService.findCurriculumByMisRecordId(old.getId());
		super.unauthenticate();

	}
	//Testing saving a miscellaneous record expecting a fail
	@Test
	public void testSaveMiscellaneousRecordFail() {
		super.authenticate("customer");
		MiscellaneousRecord old, nuevo;
		old = this.miscellaneousRecordService.findOne(super.getEntityId("miscellaneousRecord1"));
		Assert.notNull(old);
		old.setAttachment("https://www.cambridgeenglish.org/es/");
		this.exception.expect(IllegalArgumentException.class);
		nuevo = this.miscellaneousRecordService.save(old);
		super.unauthenticate();
		Assert.isNull(nuevo);

	}
	//Testing deleting a miscellaneous record succesfully
	@Test
	public void testDeleteMiscellaneousRecord() {
		super.authenticate("handyWorker");
		final int id = super.getEntityId("miscellaneousRecord1");
		final MiscellaneousRecord borrado = this.miscellaneousRecordService.findOne(id);
		Assert.notNull(borrado);

		this.miscellaneousRecordService.delete(borrado);
		this.miscellaneousRecordService.flush();
		final Collection<MiscellaneousRecord> misRecord = this.miscellaneousRecordService.findAll();
		Assert.isTrue(!misRecord.contains(borrado));
		super.unauthenticate();

	}
}
