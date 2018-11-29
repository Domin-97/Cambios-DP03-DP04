
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

import domain.Customer;
import domain.Endorsement;
import domain.Endorser;
import domain.HandyWorker;

import utilities.AbstractTest;
import utilities.internal.SchemaPrinter;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class EndorsementServiceTest extends AbstractTest {

	@Autowired
	public EndorsementService		endorsementService;

	@Autowired
	public CustomerService			customerService;

	@Autowired
	public ActorService				actorService;

	@Autowired
	public EndorserService			endorserService;

	@Autowired
	public HandyWorkerService		handyWorkerService;

	@Rule
	public final ExpectedException	exception	= ExpectedException.none();


	@Test
	public void findOneTest() {
		//Cualquiera puede solicitar un endorsement
		super.unauthenticate();
		Endorsement result = this.endorsementService.findOne(super.getEntityId("endorsement4"));
		Assert.notNull(result);
		//		SchemaPrinter.print(result);
		super.unauthenticate();
	}

	@Test
	public void findOneTestFail() {
		//Cualquiera puede solicitar un endorsement
		super.unauthenticate();
		//Se solicita un endorsement que no existe
		this.exception.expect(IllegalArgumentException.class);
		Endorsement result = this.endorsementService.findOne(super.getEntityId("endorsement84"));
		Assert.isNull(result);
		super.unauthenticate();
	}

	@Test
	public void findEndorsementsCreatedTest() {
		//Un usuario logeado solicita sus endorsements creados
		super.authenticate("customer4");
		Collection<Endorsement> result;
		result = this.endorsementService.findEndorsementsCreated(this.endorserService.findOne(this.customerService.findOne(super.getEntityId("customer4")).getId()).getId());
		Assert.notNull(result);
		for (Endorsement e : result) {
			Assert.isTrue(customerService.findOne(super.getEntityId("customer4")).getEndorsementsByMe().contains(e));
		}
		super.unauthenticate();
	}

	@Test
	public void findEndorsementsCreatedTest2() {
		//Un usuario cualquiera solicita los endorsements creados de otro usuario
		super.unauthenticate();
		Collection<Endorsement> result;
		Customer customer = this.customerService.findOne(super.getEntityId("customer4"));
		result = this.endorsementService.findEndorsementsCreated(customer.getId());
		Assert.notNull(result);
		for (Endorsement e : result) {
			Assert.isTrue(customerService.findOne(customer.getId()).getEndorsementsByMe().contains(e));
		}
		super.unauthenticate();
	}

	@Test
	public void findEndorsementsCreatedTestFail() {
		//Un usuario cualquiera solicita los endorsements creados de un usuario no endorser
		super.unauthenticate();
		Customer customer = this.customerService.findOne(super.getEntityId("customer4"));
		Collection<Endorsement> result;
		this.exception.expect(IllegalArgumentException.class);
		result = this.endorsementService.findEndorsementsCreated(customer.getId());
		Assert.isNull(result);
		super.unauthenticate();
	}

	@Test
	public void findEndorsementsObtainedTest() {
		//Un usuario logeado solicita sus endorsements obtenidos
		super.authenticate("customer4");
		Customer customer = this.customerService.findOne(super.getEntityId("customer4"));
		Collection<Endorsement> result;
		result = this.endorsementService.findEndorsementsObtained(customer.getId());
		Assert.notNull(result);
		for (Endorsement e : result) {
			Assert.isTrue(customerService.findOne(customer.getId()).getEndorsementsByOther().contains(e));
		}
		super.unauthenticate();
	}

	@Test
	public void findEndorsementsObtainedTest2() {
		//Un usuario cualquiera solicita los endorsements obtenidos de otro usuario
		super.unauthenticate();
		Collection<Endorsement> result;
		Customer customer = this.customerService.findOne(super.getEntityId("customer4"));
		result = this.endorsementService.findEndorsementsObtained(customer.getId());
		Assert.notNull(result);
		for (Endorsement e : result) {
			Assert.isTrue(customerService.findOne(customer.getId()).getEndorsementsByOther().contains(e));
		}
		super.unauthenticate();
	}

	@Test
	public void findEndorsementsObtainedTestFail() {
		//Un usuario cualquiera solicita los endorsements obtenidos de un usuario no endorser
		super.unauthenticate();
		Collection<Endorsement> result;
		Customer customer = this.customerService.findOne(super.getEntityId("customer4"));
		this.exception.expect(IllegalArgumentException.class);
		result = this.endorsementService.findEndorsementsObtained(this.endorserService.findOne(customer.getId()).getId());
		Assert.isNull(result);
		super.unauthenticate();
	}

	@Test
	public void createTest() {
		//Los usuarios customer pueden crear endorsements
		super.authenticate("customer4");
		Endorsement result = this.endorsementService.create();
		Assert.notNull(result);
		super.unauthenticate();
	}

	@Test
	public void createTestFail() {
		//Los usuarios sponsor no pueden crear endorsements
		super.authenticate("sponsor2");
		this.exception.expect(IllegalArgumentException.class);
		Endorsement result = this.endorsementService.create();
		Assert.isNull(result);
		super.unauthenticate();
	}

	@Test
	public void createAndSaveTest() {
		//Un handyworker puede crear un endorsement sobre un customer
		//si tienen alguna fixuptask en común
		super.authenticate("handyworker3");
		Endorsement result = this.endorsementService.create();
		Assert.notNull(result);
		HandyWorker handy = this.handyWorkerService.findOne(super.getEntityId("handyworker3"));
		Customer customer = this.customerService.findOne(super.getEntityId("customer3"));

		Endorsement clon = (Endorsement) result.clone();

		clon.setComment("good, fantastic, bad");

		Endorsement saved = this.endorsementService.save(clon, customer);
		Assert.notNull(saved);

		//Vemos que ambas colecciones contienen el nuevo endorsement
		Collection<Endorsement> handyEnd = this.handyWorkerService.findOne(handy.getId()).getEndorsementsByMe();
		Collection<Endorsement> customerEnd = this.customerService.findOne(customer.getId()).getEndorsementsByOther();
		Assert.isTrue(handyEnd.contains(saved) && customerEnd.contains(saved));

		super.unauthenticate();
	}

	@Test
	public void createAndSaveTest2() {
		//Un customer puede crear un endorsement sobre un handyworker
		//si tienen alguna fixuptask en común
		super.authenticate("customer3");
		Endorsement result = this.endorsementService.create();
		Assert.notNull(result);
		HandyWorker handy = this.handyWorkerService.findOne(super.getEntityId("handyworker3"));
		Customer customer = this.customerService.findOne(super.getEntityId("customer3"));

		Endorsement clon = (Endorsement) result.clone();

		clon.setComment("good, fantastic, great");

		Endorsement saved = this.endorsementService.save(clon, handy);
		Assert.notNull(saved);

		//Vemos que ambas colecciones contienen el nuevo endorsement
		Collection<Endorsement> handyEnd = this.handyWorkerService.findOne(handy.getId()).getEndorsementsByMe();
		Collection<Endorsement> customerEnd = this.customerService.findOne(customer.getId()).getEndorsementsByOther();
		Assert.isTrue(handyEnd.contains(saved) && customerEnd.contains(saved));

		super.unauthenticate();
	}

	@Test
	public void createAndSaveTestFail() {
		//Un handyworker no puede crear un endorsement sobre otro handyworker
		super.authenticate("handyworker3");
		Endorsement result = this.endorsementService.create();
		Assert.notNull(result);
		HandyWorker handy = this.handyWorkerService.findOne(super.getEntityId("handyworker3"));
		HandyWorker handy2 = this.handyWorkerService.findOne(super.getEntityId("handyworker2"));

		Endorsement clon = (Endorsement) result.clone();

		clon.setComment("good, fantastic, bad");

		this.exception.expect(IllegalArgumentException.class);
		Endorsement saved = this.endorsementService.save(clon, handy2);
		Assert.isNull(saved);

		super.unauthenticate();
	}

	@Test
	public void createAndSaveTestFail2() {
		//Un handyworker puede crear un endorsement sobre un customer
		//si tienen alguna fixuptask en común
		super.authenticate("handyworker3");
		Endorsement result = this.endorsementService.create();
		Assert.notNull(result);
		HandyWorker handy = this.handyWorkerService.findOne(super.getEntityId("handyworker3"));
		Customer customer = this.customerService.findOne(super.getEntityId("customer3"));

		Endorsement clon = (Endorsement) result.clone();

		//Falla por el comentario en blanco
		clon.setComment("");

		this.exception.expect(IllegalArgumentException.class);
		Endorsement saved = this.endorsementService.save(clon, customer);
		Assert.isNull(saved);

		super.unauthenticate();
	}

	@Test
	public void saveTest() {
		//Un endorser solo puede modificar los endorsements que se encuentran en su lista de creados
		super.authenticate("handyworker3");
		//Este endorsement pertenece al endorser
		Endorsement old = this.endorsementService.findOne(super.getEntityId("endorsement5"));
		Assert.notNull(old);

		String oldComment = old.getComment();
		String newComment = "Bad, horrible, disaster";
		Endorsement clon = (Endorsement) old.clone();

		clon.setComment(newComment);
		old = clon;
		//El receptor en este caso debe estar a null
		Endorsement saved = this.endorsementService.save(old, null);
		Assert.notNull(saved);
		//Comprobamos que se ha efectuado el cambio
		Assert.isTrue(this.endorsementService.findOne(old.getId()).getComment().equals(newComment));
		super.unauthenticate();
	}

	@Test
	public void saveTestFail() {
		//Un endorser solo puede modificar los endorsements que se encuentran en su lista de creados
		super.authenticate("handyworker3");
		//Este endorsement no pertenece al endorser
		Endorsement old = this.endorsementService.findOne(super.getEntityId("endorsement3"));
		Assert.notNull(old);

		String oldComment = old.getComment();
		String newComment = "Bad, horrible, disaster";
		Endorsement clon = (Endorsement) old.clone();

		clon.setComment(newComment);
		old = clon;
		//El receptor en este caso debe estar a null
		this.exception.expect(IllegalArgumentException.class);
		Endorsement saved = this.endorsementService.save(old, null);
		Assert.isNull(saved);
		super.unauthenticate();
	}

	@Test
	public void deleteTest() {
		//Un endorser solo puede borrar los endorsements que se encuentran en su lista de creados
		super.authenticate("customer4");
		//Este endorsement pertenece al endorser
		Endorsement old = this.endorsementService.findOne(super.getEntityId("endorsement4"));
		this.endorsementService.delete(old);
		this.endorsementService.flush();
		
		//Comprobamos que el usuario ya no tiene el endorsement
		Customer customer = this.customerService.findOne(super.getEntityId("customer4"));
		//SchemaPrinter.print(customer.getEndorsementsByMe());
		Assert.isTrue(!customer.getEndorsementsByMe().contains(old));
		
		super.unauthenticate();
	}
	
	@Test
	public void deleteTestFail() {
		//Un endorser solo puede borrar los endorsements que se encuentran en su lista de creados
		super.authenticate("customer4");
		//Este endorsement no pertenece al endorser
		Endorsement old = this.endorsementService.findOne(super.getEntityId("endorsement5"));
		this.exception.expect(IllegalArgumentException.class);
		this.endorsementService.delete(old);
		this.endorsementService.flush();
		
		//Comprobamos que el usuario ya no tiene el endorsement
		Customer customer = this.customerService.findOne(super.getEntityId("customer4"));
		SchemaPrinter.print(customer.getEndorsementsByMe());
		Assert.isTrue(customer.getEndorsementsByOther().contains(old));
		
		super.unauthenticate();
	}
}
