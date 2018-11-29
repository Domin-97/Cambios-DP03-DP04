
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

	@Autowired
	public SponsorshipService		sponsorshipService;

	@Rule
	public final ExpectedException	exception	= ExpectedException.none();


	//Getting all the tutorials
	@Test
	public void testFindAll() {
		final Collection<Tutorial> tutorials = this.tutorialService.findAll();
		Assert.notNull(tutorials);
		//SchemaPrinter.print(tutorials);
	}

	//Getting one tutorial
	@Test
	public void testFindOne() {
		final int id = super.getEntityId("tutorial1");
		Assert.notNull(this.tutorialService.findOne(id));
		//SchemaPrinter.print(this.tutorialService.findOne(id));
	}

	//saving one tutorial
	@Test
	public void testSaveTutorial() {
		Tutorial old, clone;
		super.authenticate("handyworker");
		final int id = super.getEntityId("tutorial1");
		old = this.tutorialService.findOne(id);
		clone = (Tutorial) old.clone();

		clone.setTitle("Prueba");
		Assert.isTrue(!old.getTitle().equals(clone.getTitle()));
		old = clone;
		final Tutorial saved = this.tutorialService.save(old);
		this.tutorialService.flush();
		//SchemaPrinter.print(saved);
		super.unauthenticate();

	}

	//Refeere trying to save a tutorial (Not allowed)
	@Test
	public void testSaveFallaRefeere() {
		Tutorial old, clone;
		final int id = super.getEntityId("tutorial1");
		old = this.tutorialService.findOne(id);
		super.authenticate("referee2");
		clone = (Tutorial) old.clone();
		clone.setTitle("AcmePrueba");
		this.exception.expect(IllegalArgumentException.class);
		final Tutorial falla = this.tutorialService.save(old);
		this.sectionService.flush();
		super.unauthenticate();
		Assert.isNull(falla);

	}

	//Creating a tutorial
	@Test
	public void createTutorial() {
		super.authenticate("handyworker");
		Tutorial tut;
		Tutorial saved;
		final Date fecha = new Date();
		Collection<Tutorial> tutorials = new ArrayList<>();
		final List<String> pic = new ArrayList<>();
		pic.add("foto1.com");
		tut = this.tutorialService.create();
		tut.setTitle("Tutorial 23");
		tut.setLastUpdated(fecha);
		tut.setPictures(pic);
		tut.setSummary("Summary 2");
		tut.setSponsorShips(this.sponsorshipService.findAll());
		saved = this.tutorialService.save(tut);
		tutorials = this.tutorialService.findAll();
		Assert.isTrue(tutorials.contains(saved));
		super.unauthenticate();

	}
	//Refeere trying to create a tutorial (Not allowed)
	@Test
	public void createTutorialMalAutentificado() {
		super.authenticate("referee2");

		this.exception.expect(IllegalArgumentException.class);
		this.tutorialService.create();

		super.unauthenticate();

	}

	//Deleting one tutorial
	@Test
	public void deleteTutorial() {
		super.authenticate("handyworker");
		final int id = super.getEntityId("tutorial1");
		//final int id2 = super.getEntityId("section1");
		final Tutorial borrado = this.tutorialService.findOne(id);
		Assert.notNull(borrado);

		this.tutorialService.delete(borrado);

		//Collection<Tutorial> tutorials = this.tutorialService.findAll();
		Assert.isNull(this.sectionService.findOne(id));
		//SchemaPrinter.print(this.sectionService.findOne(id2));

		super.unauthenticate();

	}
	//Trying to delete one tutorial without being owner (Not allowed)
	@Test
	public void deleteTutorialMal() {
		super.authenticate("handyworker2");
		final int id = super.getEntityId("tutorial1");
		Assert.notNull(this.tutorialService.findOne(id));
		final Tutorial borrado = this.tutorialService.findOne(id);

		this.exception.expect(IllegalArgumentException.class);
		this.tutorialService.delete(borrado);

		super.unauthenticate();

	}

}
