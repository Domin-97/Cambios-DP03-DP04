
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
import domain.Customer;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class CustomerServiceTest extends AbstractTest {

	@Autowired
	public CustomerService		customerService;

	@Rule
	public ExpectedException	exception	= ExpectedException.none();


	//Getting all the customers
	@Test
	public void testFindAll() {
		Collection<Customer> customers = this.customerService.findAll();
		Assert.notNull(customers);
		//SchemaPrinter.print(customers);
	}

	//Getting one customer
	@Test
	public void testFindOne() {
		Customer cus = this.customerService.findOne(super.getEntityId("customer"));
		Assert.notNull(cus);
		//SchemaPrinter.print(cus);
	}

	//Creating one customer
	@Test
	public void testCreate() {
		super.unauthenticate();
		Customer cus;
		cus = this.customerService.create();
		Assert.notNull(cus);
	}

	//Trying to create a customer while being logged in
	@Test
	public void testMalCreateLogeado() {
		super.authenticate("customer");
		Customer cus;
		this.exception.expect(IllegalArgumentException.class);
		cus = this.customerService.create();
		Assert.notNull(cus);
		super.unauthenticate();
	}

	//Creating and saving a CUSTOMER
	@Test
	public void testCreateAndSave() {
		Customer cus;
		cus = this.customerService.create();

		Customer clon = (Customer) cus.clone();

		clon.setName("Prueba");
		clon.setMiddleName("pinpun");
		clon.setSurname("Mohamed");
		clon.setPhoto("http:www.fotitasa.es");
		clon.setEmail("maildeprueba@gmail.com");
		clon.setPhoneNumber("666666666");
		clon.setAddress("Ceuta");

		clon.getUserAccount().setUsername("prueba");
		clon.getUserAccount().setPassword("3asd1as35d4as5");

		cus = clon;

		//		SchemaPrinter.print(hw);
		//		SchemaPrinter.print(hw.getUserAccount());

		Customer saved = this.customerService.save(cus);
		Assert.notNull(saved);
		//		SchemaPrinter.print(saved);
	}

	//Editing customer information
	@Test
	public void testUpdateBasicInformation() {
		super.authenticate("customer");
		Customer old = this.customerService.findOne(super.getEntityId("customer"));
		Customer clon = (Customer) old.clone();

		clon.setName("Nombre nuevo");
		clon.setMiddleName("xd");
		clon.setSurname("lol");
		clon.setPhoto("http:www.foto.es");
		clon.setEmail("maildepruebaaa@gmail.com");
		clon.setPhoneNumber("621676666");
		clon.setAddress("marte");

		old = clon;
		Customer saved = this.customerService.save(old);
		Assert.notNull(saved);
		this.customerService.flush();
		//SchemaPrinter.print(saved);
		super.unauthenticate();
	}

	//Trying to edit a customer without having permission
	@Test
	public void testUpdateBasicInformationUsuarioMal() {
		super.authenticate("handyworker2"); //Usuario sin permisos
		Customer old = this.customerService.findOne(super.getEntityId("customer"));
		Customer clon = (Customer) old.clone();
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
		Customer saved = this.customerService.save(old);
		Assert.isNull(saved);

		super.unauthenticate();
	}

	//Customer trying to set ban and suspicious attributes (Not allowed)
	@Test
	public void testUpdateBooleanAttributes() {
		super.authenticate("customer");
		Customer old = this.customerService.findOne(super.getEntityId("customer"));
		Customer clon = (Customer) old.clone();

		clon.setName("Nombre nuevo");
		clon.setMiddleName("Nxd");
		clon.setSurname("camio");
		clon.setPhoto("http:www.fotoo.es");
		clon.setEmail("maildeprueba22@gmail.com");
		clon.setPhoneNumber("661176666");
		clon.setAddress("marte");
		clon.setBanned(true);
		clon.setSuspicious(true);

		old = clon;
		this.exception.expect(IllegalArgumentException.class);
		Customer saved = this.customerService.save(old);
		Assert.isNull(saved);

		Assert.isTrue(this.customerService.findOne(super.getEntityId("customer")).isSuspicious() == false);
		Assert.isTrue(this.customerService.findOne(super.getEntityId("customer")).isBanned() == false);

		this.customerService.flush(); //Para que cambie la versión

		super.unauthenticate();
	}
	//admin setting ban and suspicius atributes
	@Test
	public void testAdminUpdate() {
		super.authenticate("admin");
		Customer old = this.customerService.findOne(super.getEntityId("customer"));
		Customer clon = (Customer) old.clone();
		//		SchemaPrinter.print(old);

		clon.setSuspicious(true);
		clon.setBanned(true);

		old = clon;
		Customer saved = this.customerService.save(old);
		Assert.notNull(saved);

		Assert.isTrue(this.customerService.findOne(super.getEntityId("customer")).isSuspicious() == true);
		Assert.isTrue(this.customerService.findOne(super.getEntityId("customer")).isBanned() == true);

		this.customerService.flush(); //Para que cambie la versión
		//SchemaPrinter.print(this.customerService.findOne(super.getEntityId("customer")));

		super.unauthenticate();
	}

	//Admin trying to edit basic information (Not allowed)
	@Test
	public void testAdminUpdateMal() {
		super.authenticate("admin");
		Customer old = this.customerService.findOne(super.getEntityId("customer"));
		Customer clon = (Customer) old.clone();

		clon.setName("Juan");

		old = clon;
		this.exception.expect(IllegalArgumentException.class);
		Customer saved = this.customerService.save(old);
		Assert.notNull(saved);

		Assert.isTrue(this.customerService.findOne(super.getEntityId("customer")).getName() != "Juan");

		this.customerService.flush(); //Para que cambie la versión

		super.unauthenticate();
	}

	//Cant delete CUSTOMER with fix Uptask published
	@Test
	public void deleteMalPorFixUptaskCustomer() {
		super.authenticate("customer");
		int id = super.getEntityId("customer");
		// int id2 = super.getEntityId("section1");
		Customer borrado = this.customerService.findOne(id);
		Assert.notNull(borrado);

		this.exception.expect(IllegalArgumentException.class);
		this.customerService.delete(borrado);

		//Collection<Tutorial> tutorials = this.tutorialService.findAll();
		Assert.isNull(this.customerService.findOne(id));
		//SchemaPrinter.print(this.sectionService.findOne(id2));

		super.unauthenticate();

	}
	//Trying to delete one tutorial without being owner (Not allowed)
	@Test
	public void deleteTutorialMal() {
		super.authenticate("referee2");
		int id = super.getEntityId("customer");
		Assert.notNull(this.customerService.findOne(id));
		Customer borrado = this.customerService.findOne(id);

		this.exception.expect(IllegalArgumentException.class);
		this.customerService.delete(borrado);

		super.unauthenticate();

	}

}
