package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import repositories.TutorialRepository;
import utilities.AbstractTest;
import domain.Tutorial;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class TutorialServiceTest extends AbstractTest {

	@Autowired
	public TutorialService			tutorialService;

	@Autowired
	public SectionService			sectionService;

	@Autowired
	public TutorialRepository		tutorialRepository;

	@Rule
	public final ExpectedException	exception	= ExpectedException.none();


	@Test
	public void testFindAll() {
		Assert.isTrue(this.tutorialService.findAll().size() >= 1);

	}

	@Test
	public void testFindOne() {

		Assert.notNull(this.tutorialService.findOne(6272));
	}

	@Test
	public void testSaveTutorial() {
		Tutorial old, nuevo;
		super.authenticate("handyworker");
		old = this.tutorialService.findOne(6272);

		final String name = old.getTitle();
		old.setTitle("Acme-Corchutequiero");
		nuevo = this.tutorialService.save(old);

		Assert.isTrue(!name.equals(nuevo.getTitle()));

		super.unauthenticate();

	}

	@Test
	public void testSaveFallaRefeere() {
		Tutorial old, nuevo;
		old = this.tutorialService.findOne(6272);
		super.authenticate("referee2");
		old.setTitle("Acme-Corchutequiero");
		this.exception.expect(IllegalArgumentException.class);
		nuevo = this.tutorialService.save(old);
		super.unauthenticate();
		Assert.isNull(nuevo);

	}

	@Test
	public void createTutorial() {
		super.authenticate("handyworker2");
		Tutorial tut;
		Tutorial saved;
		Date fecha = new Date();
		Collection<Tutorial> tutorials = new ArrayList<>();
		List<String> pic = new ArrayList<>();
		pic.add("foto1.com");
		tut = this.tutorialService.create();
		tut.setTitle("Tutorial 23");
		tut.setLastUpdated(fecha);
		tut.setPictures(pic);
		tut.setSummary("Summary 2");
		saved = this.tutorialService.save(tut);
		tutorials = this.tutorialService.findAll();
		Assert.isTrue(tutorials.contains(saved));
		super.unauthenticate();

	}

	@Test
	public void createTutorialMalAutentificado() {
		super.authenticate("referee2");

		this.exception.expect(IllegalArgumentException.class);
		this.tutorialService.create();

		super.unauthenticate();

	}

	@Test
	public void deleteTutorial() {
		super.authenticate("handyworker");
		final Tutorial borrado = this.tutorialService.findOne(6272);
		Assert.notNull(borrado);

		this.tutorialService.delete(borrado);
		final Collection<Tutorial> tutorials = this.tutorialService.findAll();
		Assert.isTrue(!tutorials.contains(borrado));

		super.unauthenticate();

	}

	//Un handyWorker NO propietario intenta borrar un tutorial
	//	@Test
	//	public void deleteTutorialMal() {
	//		super.authenticate("handyworker2");
	//		Assert.notNull(this.tutorialService.findOne(6216));
	//		final Tutorial borrado = this.tutorialService.findOne(6216);
	//
	//		this.exception.expect(IllegalArgumentException.class);
	//		this.tutorialService.delete(borrado);
	//
	//		super.unauthenticate();
	//
	//	}

}
