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


	@Test
	public void testFindAll() {
		Assert.isTrue(this.sectionService.findAll().size() >= 1);

	}

	@Test
	public void testFindOne() {

		Assert.notNull(this.sectionService.findOne(6216));
	}

	@Test
	public void testSaveSection() {
		Section old, nuevo;
		super.authenticate("handyworker2");
		old = this.sectionService.findOne(6216);

		final String name = old.getTitle();
		old.setTitle("Acme-Corchutequiero");
		nuevo = this.sectionService.save(old);

		Assert.isTrue(!name.equals(nuevo.getTitle()));

		super.unauthenticate();

	}

	@Test
	public void testSaveFallaRefeere() {
		Section old, nuevo;
		old = this.sectionService.findOne(6216);
		super.authenticate("referee2");
		old.setTitle("Acme-Corchutequiero");
		this.exception.expect(IllegalArgumentException.class);
		nuevo = this.sectionService.save(old);
		super.unauthenticate();
		Assert.isNull(nuevo);

	}

	@Test
	public void createSection() {
		super.authenticate("handyworker2");
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

	@Test
	public void createSectionMalAutentificado() {
		super.authenticate("referee2");

		this.exception.expect(IllegalArgumentException.class);
		this.sectionService.create();

		super.unauthenticate();

	}

	@Test
	public void deleteSection() {
		super.authenticate("handyworker");
		final Section borrado = this.sectionService.findOne(6216);
		Assert.notNull(borrado);

		this.sectionService.delete(borrado);
		this.sectionService.flush();
		final Collection<Section> sectionss = this.sectionService.findAll();
		Assert.isTrue(!sectionss.contains(borrado));
		SchemaPrinter.print(this.sectionService.findOne(6216));

		super.unauthenticate();

	}
	//Un handyWorker NO propietario intenta borrar una section
	@Test
	public void deleteSectionMal() {
		super.authenticate("handyworker2");
		Assert.notNull(this.sectionService.findOne(6216));
		final Section borrado = this.sectionService.findOne(6216);

		this.exception.expect(IllegalArgumentException.class);
		this.sectionService.delete(borrado);

		super.unauthenticate();

	}
}
