
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
import domain.Section;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class SectionServiceTest extends AbstractTest {

	@Autowired
	public SectionService			sectionService;

	@Rule
	public final ExpectedException	exception	= ExpectedException.none();


	//Getting all sections
	@Test
	public void testFindAll() {
		Assert.isTrue(this.sectionService.findAll().size() >= 1);
		//SchemaPrinter.print(this.sectionService.findAll());
	}

	//Getting one section
	@Test
	public void testFindOne() {
		final int id = super.getEntityId("section1");
		Assert.notNull(this.sectionService.findOne(id));
		//SchemaPrinter.print(this.sectionService.findOne(id));

	}

	//Saving one section
	@Test
	public void testSaveSection() {
		Section old, clone;
		final int id = super.getEntityId("section1");
		super.authenticate("handyworker");
		old = this.sectionService.findOne(id);
		clone = (Section) old.clone();

		clone.setTitle("Prueba");
		Assert.isTrue(!old.getTitle().equals(clone.getTitle()));
		old = clone;
		final Section saved = this.sectionService.save(old);
		this.sectionService.flush();
		//SchemaPrinter.print(saved);
		super.unauthenticate();

	}

	//Refeere trying to save section (only handyworkers can)
	@Test
	public void testSaveFallaRefeere() {
		Section old, clone;
		final int id = super.getEntityId("section1");
		old = this.sectionService.findOne(id);
		super.authenticate("referee2");
		clone = (Section) old.clone();
		clone.setTitle("Prueba");
		old = clone;
		this.exception.expect(IllegalArgumentException.class);
		final Section xd = this.sectionService.save(old);
		this.sectionService.flush();
		super.unauthenticate();
		Assert.isNull(xd);

	}
	//Creating one section
	@Test
	public void createSection() {
		super.authenticate("handyworker");
		Section sect;
		Section saved;
		Collection<Section> sectionss = new ArrayList<>();
		final Collection<String> pic = new ArrayList<>();
		pic.add("foto1.com");
		sect = this.sectionService.create();
		sect.setNumber(1);
		sect.setTitle("Section COOL");
		sect.setText("texto para section");
		sect.setPictures(pic);
		saved = this.sectionService.save(sect);
		sectionss = this.sectionService.findAll();
		Assert.isTrue(sectionss.contains(saved));
		super.unauthenticate();

	}
	//Refeere trying to create section (only handyworkers can)
	@Test
	public void createSectionMalAutentificado() {
		super.authenticate("referee2");

		this.exception.expect(IllegalArgumentException.class);
		this.sectionService.create();

		super.unauthenticate();

	}
	//Deleting one section
	@Test
	public void deleteSection() {
		super.authenticate("handyworker");
		final int id = super.getEntityId("section1");
		final Section borrado = this.sectionService.findOne(id);
		Assert.notNull(borrado);

		this.sectionService.delete(borrado);
		this.sectionService.flush();
		final Collection<Section> sectionss = this.sectionService.findAll();
		Assert.isTrue(!sectionss.contains(borrado));
		//SchemaPrinter.print(this.sectionService.findOne(id));

		super.unauthenticate();

	}
	//Trying to delete one tutorial without being owner (Not allowed)
	@Test
	public void deleteSectionMal() {
		super.authenticate("handyworker2");
		final int id = super.getEntityId("section1");
		Assert.notNull(this.sectionService.findOne(id));
		final Section borrado = this.sectionService.findOne(id);

		this.exception.expect(IllegalArgumentException.class);
		this.sectionService.delete(borrado);

		super.unauthenticate();

	}
}
