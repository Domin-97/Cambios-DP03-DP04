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

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional


public class NoteServiceTest extends AbstractTest{

	@Autowired
	public NoteService noteService;
	
	@Rule
	public final ExpectedException exception = ExpectedException.none();
	@Test
	public void testFindAll() {
		
		Assert.isTrue(this.noteService.findAll().size()>=1);
	}
	@Test
	public void testSaveNote(){
		Note old, nuevo;
		old = this.noteService.findOne(6207);
		super.authenticate("handyworker");
		String cusComent = old.getCusComment();
		old.setCusComment("Acme-Corchutequiero");
		nuevo = this.noteService.save(old);
		
		Assert.isTrue(!cusComent.equals(nuevo.getCusComment()));
		
		super.unauthenticate();

	}
	@Test
	public void testCreateNote(){
		super.authenticate("handyworker");
		Note w, saved; 
		Collection<Note> notes;
		Date mom = new Date(452018);
		w = this.noteService.create();
		w.setCusComment("nnnnn");
		w.setHwComment("mmmmmm");
		w.setMoment(mom);
		saved = this.noteService.save(w);
		notes = this.noteService.findAll();
		Assert.isTrue(notes.contains(saved));
		super.unauthenticate();
	}
}

