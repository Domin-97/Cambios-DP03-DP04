
package services;

import java.util.Collection;
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

import security.Authority;
import security.UserAccount;
import utilities.AbstractTest;
import utilities.internal.SchemaPrinter;

import domain.Application;
import domain.Curriculum;
import domain.Endorsement;
import domain.HandyWorker;
import domain.MessageBox;
import domain.SocialProfile;
import domain.Tutorial;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class HandyWorkerServiceTest extends AbstractTest {

	@Autowired
	public HandyWorkerService		handyWorkerService;

	@Rule
	public final ExpectedException	exception	= ExpectedException.none();


	@Test
	public void testFindAll() {
		Collection<HandyWorker> handyWorkers = this.handyWorkerService.findAll();
		Assert.notNull(handyWorkers);
	}

	@Test
	public void testFindOne() {
		HandyWorker hw = this.handyWorkerService.findOne(super.getEntityId("handyworker2"));
		Assert.notNull(hw);
	}

	@Test
	public void testCreate() {
		super.unauthenticate();
		HandyWorker hw;
		hw = this.handyWorkerService.create();
		Assert.notNull(hw);
	}
	
	@Test
	public void testCreateLogged() {
		super.authenticate("handyworker2");
		HandyWorker hw;
		this.exception.expect(IllegalArgumentException.class);
		hw = this.handyWorkerService.create();
		Assert.isNull(hw);
		super.unauthenticate();
	}

	@Test
	public void testCreateAndSave() {
		super.unauthenticate();
		HandyWorker hw;
		hw = this.handyWorkerService.create();

		HandyWorker clon = (HandyWorker) hw.clone();

		//COSAS DE ACTOR
		clon.setName("Mostaza");
		clon.setMiddleName("Taza");
		clon.setSurname("Mohamed");
		clon.setPhoto("http:www.fotito.es");
		clon.setEmail("maildeprueba@gmail.com");
		clon.setPhoneNumber("666666666");
		clon.setAddress("Ceuta");

		//CUENTA
		clon.getUserAccount().setUsername("mostaza");
		clon.getUserAccount().setPassword("3asd1as35d4as5");

		hw = clon;

		//		SchemaPrinter.print(hw);
		//		SchemaPrinter.print(hw.getUserAccount());

		HandyWorker saved = this.handyWorkerService.save(hw);
		Assert.notNull(saved);
		//		SchemaPrinter.print(saved);
	}
	
	@Test
	public void testCreateAndSaveFail() {
		super.unauthenticate();
		HandyWorker hw;
		hw = this.handyWorkerService.create();

		HandyWorker clon = (HandyWorker) hw.clone();

		//COSAS DE ACTOR
		clon.setName("Mostaza");
		clon.setMiddleName("Taza");
		clon.setSurname("Mohamed");
		clon.setPhoto("http:www.fotito.es");
		clon.setEmail("maildeprueba@gmail.com");
		clon.setPhoneNumber("666666666");
		clon.setAddress("Ceuta");

		//CUENTA
		clon.getUserAccount().setUsername("mostaza");
		clon.getUserAccount().setPassword("3asd1as35d4as5");

		hw = clon;

		//		SchemaPrinter.print(hw);
		//		SchemaPrinter.print(hw.getUserAccount());
		
		//Se logea en medio del proceso
		super.authenticate("handyworker2");
		
		this.exception.expect(IllegalArgumentException.class);
		HandyWorker saved = this.handyWorkerService.save(hw);
		Assert.isNull(saved);
		//		SchemaPrinter.print(saved);
		super.unauthenticate();
	}

	@Test
	public void testUpdateBasicInformation() {
		super.authenticate("handyworker2");
		HandyWorker old = this.handyWorkerService.findOne(super.getEntityId("handyworker2"));
		HandyWorker clon = (HandyWorker) old.clone();
		//		SchemaPrinter.print(old);

		clon.setName("Nombre cambiado");
		clon.setMiddleName("NC");
		clon.setSurname("Cambiadito");
		clon.setPhoto("http:www.suuuu.es");
		clon.setEmail("maildeprueba2@gmail.com");
		clon.setPhoneNumber("666676666");
		clon.setAddress("Melilla");

		old = clon;
		HandyWorker saved = this.handyWorkerService.save(old);
		Assert.notNull(saved);
		this.handyWorkerService.flush(); //Para que cambie la versión
		//		SchemaPrinter.print(this.handyWorkerService.findOne(super.getEntityId("handyworker2")));

		super.unauthenticate();
	}

	@Test
	public void testUpdateBasicInformationUsuarioMal() {
		super.authenticate("customer"); //Usuario sin permisos
		HandyWorker old = this.handyWorkerService.findOne(super.getEntityId("handyworker2"));
		HandyWorker clon = (HandyWorker) old.clone();
		//		SchemaPrinter.print(old);

		clon.setName("Nombre cambiado");
		clon.setMiddleName("NC");
		clon.setSurname("Cambiadito");
		clon.setPhoto("http:www.suuuu.es");
		clon.setEmail("maildeprueba2@gmail.com");
		clon.setPhoneNumber("666676666");
		clon.setAddress("Melilla");

		old = clon;
		this.exception.expect(IllegalArgumentException.class);
		HandyWorker saved = this.handyWorkerService.save(old);
		Assert.isNull(saved);

		super.unauthenticate();
	}

	@Test
	public void testUpdateBooleanAttributes() {
		super.authenticate("handyworker2");
		HandyWorker old = this.handyWorkerService.findOne(super.getEntityId("handyworker2"));
		HandyWorker clon = (HandyWorker) old.clone();
		//		SchemaPrinter.print(old);

		clon.setName("Nombre cambiado");
		clon.setMiddleName("NC");
		clon.setSurname("Cambiadito");
		clon.setPhoto("http:www.suuuu.es");
		clon.setEmail("maildeprueba2@gmail.com");
		clon.setPhoneNumber("666676666");
		clon.setAddress("Melilla");
		clon.setBanned(true);
		clon.setSuspicious(true);

		old = clon;
		this.exception.expect(IllegalArgumentException.class);
		HandyWorker saved = this.handyWorkerService.save(old);
		Assert.isNull(saved);

		Assert.isTrue(this.handyWorkerService.findOne(super.getEntityId("handyworker2")).isSuspicious() == false);
		Assert.isTrue(this.handyWorkerService.findOne(super.getEntityId("handyworker2")).isBanned() == false);

		this.handyWorkerService.flush(); //Para que cambie la versión
		//		SchemaPrinter.print(this.handyWorkerService.findOne(super.getEntityId("handyworker2")));

		super.unauthenticate();
	}

	@Test
	public void testAdminUpdate() {
		super.authenticate("admin");
		HandyWorker old = this.handyWorkerService.findOne(super.getEntityId("handyworker2"));
		HandyWorker clon = (HandyWorker) old.clone();
		//		SchemaPrinter.print(old);

		clon.setSuspicious(true);
		clon.setBanned(true);

		old = clon;
		HandyWorker saved = this.handyWorkerService.save(old);
		Assert.notNull(saved);

		Assert.isTrue(this.handyWorkerService.findOne(super.getEntityId("handyworker2")).isSuspicious() == true);
		Assert.isTrue(this.handyWorkerService.findOne(super.getEntityId("handyworker2")).isBanned() == true);

		this.handyWorkerService.flush(); //Para que cambie la versión
		//SchemaPrinter.print(this.handyWorkerService.findOne(super.getEntityId("handyworker2")));

		super.unauthenticate();
	}

	@Test
	public void testAdminUpdateMal() {
		super.authenticate("admin");
		HandyWorker old = this.handyWorkerService.findOne(super.getEntityId("handyworker2"));
		HandyWorker clon = (HandyWorker) old.clone();
		//		SchemaPrinter.print(old);

		clon.setName("Antonio");

		old = clon;
		this.exception.expect(IllegalArgumentException.class);
		HandyWorker saved = this.handyWorkerService.save(old);
		Assert.notNull(saved);

		Assert.isTrue(this.handyWorkerService.findOne(super.getEntityId("handyworker2")).getName() != "Antonio");

		this.handyWorkerService.flush(); //Para que cambie la versión
		//		SchemaPrinter.print(this.handyWorkerService.findOne(super.getEntityId("handyworker2")));

		super.unauthenticate();
	}

}

