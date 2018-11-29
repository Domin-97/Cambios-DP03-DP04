
package services;

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
import domain.Administrator;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class AdministratorServiceTest extends AbstractTest {

	@Autowired
	public AdministratorService	administratorService;

	@Rule
	public ExpectedException	exception	= ExpectedException.none();


	//Findind all administrators
	@Test
	public void testFindAll() {
		Collection<Administrator> administrators = this.administratorService.findAll();
		Assert.notNull(administrators);
		//SchemaPrinter.print(administrators);
	}
	//Findind one administrator
	@Test
	public void testFindOne() {
		Administrator admin = this.administratorService.findOne(super.getEntityId("admin1"));
		Assert.notNull(admin);
		//SchemaPrinter.print(admin);
	}

	//Creating one administrator
	@Test
	public void testCreate() {
		super.authenticate("admin");
		Administrator admin;
		admin = this.administratorService.create();
		Assert.notNull(admin);
		super.unauthenticate();
	}

	//Creating and saving one administrator
	@Test
	public void testCreateAndSave() {
		super.authenticate("admin");
		Administrator admin;
		admin = this.administratorService.create();

		Administrator clon = (Administrator) admin.clone();

		clon.setName("Prueba23");
		clon.setMiddleName("pinpun");
		clon.setSurname("Mohamed");
		clon.setPhoto("http:www.fotitasa.es");
		clon.setEmail("maildeprueba@gmail.com");
		clon.setPhoneNumber("666666666");
		clon.setAddress("Ceuta");

		clon.getUserAccount().setUsername("prueba234");
		clon.getUserAccount().setPassword("3asd1as35d4as5");

		admin = clon;

		Administrator saved = this.administratorService.save(admin);
		Assert.notNull(saved);
		//SchemaPrinter.print(saved);
		super.unauthenticate();
	}

	//Editing admin information
	@Test
	public void testUpdateBasicInformation() {
		super.authenticate("admin");
		Administrator old = this.administratorService.findOne(super.getEntityId("admin1"));
		Administrator clon = (Administrator) old.clone();

		clon.setName("Nombre nuevo");
		clon.setMiddleName("xd");
		clon.setSurname("lol");
		clon.setPhoto("http:www.foto.es");
		clon.setEmail("maildepruebaaa@gmail.com");
		clon.setPhoneNumber("621676666");
		clon.setAddress("marte");

		old = clon;
		Administrator saved = this.administratorService.save(old);
		Assert.notNull(saved);
		this.administratorService.flush(); //Para que cambie la versión

		super.unauthenticate();
	}

	//Trying to edit an admin without having permission
	@Test
	public void testUpdateBasicInformationUsuarioMal() {

		super.authenticate("handyworker2"); //Usuario sin permisos
		Administrator old = this.administratorService.findOne(super.getEntityId("admin1"));
		Administrator clon = (Administrator) old.clone();
		//		SchemaPrinter.print(old);

		clon.setName("Nombre nuevo");
		clon.setMiddleName("Nxd");
		clon.setSurname("camio");
		clon.setPhoto("http:www.fotoo.es");
		clon.setEmail("maildeprueba22@gmail.com");
		clon.setPhoneNumber("661176666");
		clon.setAddress("marte");

		old = clon;
		this.exception.expect(IllegalArgumentException.class);
		Administrator saved = this.administratorService.save(old);
		Assert.isNull(saved);

		super.unauthenticate();
	}

}
