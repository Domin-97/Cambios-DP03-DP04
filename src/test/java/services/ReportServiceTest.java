
package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.naming.spi.DirStateFactory.Result;
import javax.transaction.Transactional;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import repositories.ReportRepository;
import utilities.AbstractTest;
import utilities.internal.SchemaPrinter;
import domain.Complaint;
import domain.Configuration;
import domain.Note;
import domain.Report;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class ReportServiceTest extends AbstractTest {

	@Autowired
	public ReportService		reportService;

	@Autowired
	private NoteService			noteService;

	@Rule
	public ExpectedException	exception	= ExpectedException.none();


	@Test
	public void testFindAll() {

		Assert.isTrue(this.reportService.findAll() != null);
		this.reportService.findAll();
	}
	@Test
	public void TestCreate() {
		super.authenticate("referee2");
		Report report = new Report();
		Collection<String> attachments = new ArrayList<>();
		Complaint complaint = new Complaint();
		final Collection<Note> notes = new ArrayList<>();
		report.setAttachments(attachments);
		report.setComplaint(complaint);
		report.setDescription("descrip");
		report.setIsFinal(true);
		report.setMoment(new Date(System.currentTimeMillis() - 1000));
		notes.add(noteService.create());
		report.setNotes(notes);

		this.reportService.create();

		super.unauthenticate();

	}
	@Test
	public void TestCreateSave() {
		super.authenticate("referee2");
		Report old = reportService.create();
		this.reportService.save(old);
		super.unauthenticate();
	}
	//compribar que no puede crear un report un actor que no sea referee
	@Test
	public void TestCreateSaveMal() {
		super.authenticate("customer2 ");
		this.exception.expect(IllegalArgumentException.class);
		Report old = reportService.create();
		Report clon;
		clon = (Report) old.clone();

		old = clon;

		this.reportService.save(old);
		super.unauthenticate();
	}
	@Test
	public void TestUpdateSave2() {
		super.authenticate("referee2");
		Report old, clon;
		old = this.reportService.findOne(super.getEntityId("report2"));
		clon = (Report) old.clone();
		Collection<Note> notes = new ArrayList<Note>();
		Note note = this.noteService.create();
		notes.add(note);
		clon.setNotes(notes);

		old = clon;
		this.exception.expect(IllegalArgumentException.class);
		this.reportService.save(old);
		super.unauthenticate();
	}
	//intenta modificar algo del report que no sean las notas 

	@Test
	public void TestUpdateSaveMal() {
		super.authenticate("referee2");
		Report old, clon;
		old = this.reportService.findOne(super.getEntityId("report2"));
		clon = (Report) old.clone();

		clon.setDescription("prueba1");

		old = clon;

		this.exception.expect(IllegalArgumentException.class);
		this.reportService.save(old);
		super.unauthenticate();
	}
	//probar que un referee que no es dueño del report no puede guardar una nota
	@Test
	public void TestUpdateSaveMal2() {
		super.authenticate("referee2");
		Report old, clon;
		old = this.reportService.findOne(super.getEntityId("report1"));
		clon = (Report) old.clone();
		Collection<Note> notes = new ArrayList<Note>();
		Note note = this.noteService.create();
		notes.add(note);
		clon.setNotes(notes);

		old = clon;

		this.exception.expect(IllegalArgumentException.class);
		this.reportService.save(old);
		super.unauthenticate();
	}
	@Test
	public void TestUpdateSave3() {
		super.authenticate("customer");
		Report old, clon;
		old = this.reportService.findOne(super.getEntityId("report1"));
		clon = (Report) old.clone();
		Collection<Note> notes = new ArrayList<Note>();
		Note note = this.noteService.create();
		notes.add(note);
		clon.setNotes(notes);

		old = clon;

		this.reportService.save(old);
		super.unauthenticate();
	}
	//este customer no tiene fixUpTask osea no puede crear ninguna nota 
	@Test
	public void TestUpdateSaveMal4() {
		super.authenticate("customer5");
		Report old, clon;
		old = this.reportService.findOne(super.getEntityId("report1"));
		clon = (Report) old.clone();
		Collection<Note> notes = new ArrayList<Note>();
		Note note = this.noteService.create();
		notes.add(note);
		clon.setNotes(notes);

		old = clon;

		this.exception.expect(IllegalArgumentException.class);
		this.reportService.save(old);
		super.unauthenticate();
	}
	@Test
	public void TestUpdateSave5() {
		super.authenticate("handyWorker");
		Report old, clon;
		old = this.reportService.findOne(super.getEntityId("report1"));
		clon = (Report) old.clone();
		Collection<Note> notes = new ArrayList<Note>();
		Note note = this.noteService.create();
		notes.add(note);
		clon.setNotes(notes);

		old = clon;

		this.reportService.save(old);
		super.unauthenticate();
	}
	@Test
	public void TestUpdateSaveMal5() {
		super.authenticate("handyWorker3");
		Report old, clon;
		old = this.reportService.findOne(super.getEntityId("report1"));
		clon = (Report) old.clone();
		Collection<Note> notes = new ArrayList<Note>();
		Note note = this.noteService.create();
		notes.add(note);
		clon.setNotes(notes);

		old = clon;

		this.exception.expect(IllegalArgumentException.class);
		this.reportService.save(old);
		super.unauthenticate();
	}

}
