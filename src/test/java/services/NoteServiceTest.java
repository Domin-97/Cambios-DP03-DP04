
package services;

import java.sql.Date;
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

import domain.Note;
import domain.Warranty;

import utilities.AbstractTest;
import utilities.internal.SchemaPrinter;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class NoteServiceTest extends AbstractTest {

	@Autowired
	public NoteService				noteService;

	@Rule
	public final ExpectedException	exception	= ExpectedException.none();


	@Test
	public void testFindAll() {

		Assert.isTrue(this.noteService.findAll().size() >= 1);
		this.noteService.findAll();
	}

	@Test
	public void testCreateSaveNote() {
		super.authenticate("customer");

		Note note = this.noteService.create();
		note.setCusComment("sssssss");
		//esto peta
		this.noteService.save(note);

		super.unauthenticate();
	}
	// un customer poniendo una nota con un comentario de handyWorker
	@Test
	public void testCreateSaveMalNote() {
		super.authenticate("customer");
		Note note = new Note();
		note.setHwComment("ssssss");
		this.noteService.create();

		
		this.exception.expect(IllegalArgumentException.class);
		this.noteService.save(note);
		super.unauthenticate();
	}
	// un sponsor no puede crear una nota 
	@Test
	public void testCreateSaveMalNote1() {
		super.authenticate("sponsor");
		
		Note note = new Note();
		note.setHwComment("ssssss");
		this.exception.expect(IllegalArgumentException.class);
		this.noteService.create();

		
		
		this.noteService.save(note);
		super.unauthenticate();
	}
}
