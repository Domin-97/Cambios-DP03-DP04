
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

import domain.Application;
import domain.CreditCard;
import domain.Customer;
import domain.FixUpTask;
import domain.HandyWorker;
import domain.MessageBox;
import domain.Money;

import utilities.AbstractTest;
import utilities.internal.SchemaPrinter;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class ApplicationServiceTest extends AbstractTest {

	@Autowired
	public ApplicationService		applicationService;

	@Autowired
	public CustomerService			customerService;

	@Autowired
	public HandyWorkerService		handyWorkerService;

	@Autowired
	public FixUpTaskService			fixUpTaskService;

	@Autowired
	private MessageBoxService		messageBoxService;
	
	@Rule
	public final ExpectedException	exception	= ExpectedException.none();


	@Test
	public void createApplicationTest() {
		super.authenticate("handyworker2");
		Application application = this.applicationService.create();
		Assert.notNull(application);
		//		SchemaPrinter.print(application);
		super.unauthenticate();
	}

	@Test
	public void createApplicationTestFail() {
		//Un customer intenta crear una application
		super.authenticate("customer2");
		this.exception.expect(IllegalArgumentException.class);
		Application application = this.applicationService.create();
		Assert.isNull(application);
		super.unauthenticate();
	}

	@Test
	public void createApplicationTestFail2() {
		//Intento de crear una application sin login
		super.unauthenticate();
		this.exception.expect(IllegalArgumentException.class);
		Application application = this.applicationService.create();
		Assert.isNull(application);
		super.unauthenticate();
	}

	@Test
	public void findAllApplications() {
		super.unauthenticate();
		Collection<Application> result;
		result = this.applicationService.findAll();
		Assert.notNull(result);
		//		SchemaPrinter.print(result);
	}

	@Test
	public void findFixUpTaskApplicationsTest() {
		super.authenticate("customer");

		Customer cus = this.customerService.findOne(super.getEntityId("customer"));

		//FixUpTask del customer
		List<FixUpTask> fixUpTasks = (List<FixUpTask>) this.customerService.findOne(super.getEntityId("customer")).getFixUpTasks();

		if (fixUpTasks.size() > 0) {
			List<Application> applications = (List<Application>) this.applicationService.findFixUpTaskApplications(fixUpTasks.get(0));
			Assert.notNull(applications);
			if (applications.size() > 0) {
				Assert.isTrue(applications.get(0).getFixUpTask().getCustomer().equals(cus));
			}
		}
		super.unauthenticate();
	}

	@Test
	public void findFixUpTaskApplicationsTestFail() {
		super.authenticate("customer2");

		Customer cus = this.customerService.findOne(super.getEntityId("customer"));

		//FixUpTask del customer
		List<FixUpTask> fixUpTasks = (List<FixUpTask>) this.customerService.findOne(super.getEntityId("customer")).getFixUpTasks();

		if (fixUpTasks.size() > 0) {
			//Un customer intenta ver las applications de una fixuptask de otro customer
			this.exception.expect(IllegalArgumentException.class);
			List<Application> applications = (List<Application>) this.applicationService.findFixUpTaskApplications(fixUpTasks.get(0));
			Assert.isNull(applications);
			if (applications != null && applications.size() > 0) {
				Assert.isTrue(applications.get(0).getFixUpTask().getCustomer().equals(cus));
			}
		}
		super.unauthenticate();
	}

	@Test
	public void findHandyWorkerApplicationsTest() {
		super.authenticate("handyworker2");
		HandyWorker handy = this.handyWorkerService.findOne(super.getEntityId("handyworker2"));

		Collection<Application> result = this.applicationService.findHandyWorkerApplications(handy);
		Assert.notNull(result);
		Assert.isTrue(result.equals(handy.getApplications()));

		super.unauthenticate();
	}

	@Test
	public void findHandyWorkerApplicationsTestFail() {
		super.authenticate("handyworker2");
		HandyWorker handy = this.handyWorkerService.findOne(super.getEntityId("handyworker3"));

		//Un handyworker intenta ver las aplicaciones de otro handyworker
		this.exception.expect(IllegalArgumentException.class);
		Collection<Application> result = this.applicationService.findHandyWorkerApplications(handy);
		Assert.isNull(result);

		super.unauthenticate();
	}

	@Test
	public void createAndSave() {
		super.authenticate("handyworker2");
		Application application = this.applicationService.create();
		Assert.notNull(application);
		//		SchemaPrinter.print(application);

		Application clon = (Application) application.clone();

		Money money = new Money();
		money.setAmount(89.95);
		money.setCurrency("Euros");
		money.setVatTax(21);
		clon.setOfferedPrice(money);

		FixUpTask fut = this.fixUpTaskService.findOne(super.getEntityId("fixUpTask8"));

		clon.setFixUpTask(fut);

		application = clon;

		Application saved = this.applicationService.save(application);
		Assert.notNull(saved);
		//		SchemaPrinter.print(saved);

		this.applicationService.flush();

		//		SchemaPrinter.print(this.fixUpTaskService.findOne(super.getEntityId("fixUpTask8")));
		//		SchemaPrinter.print(this.handyWorkerService.findOne(super.getEntityId("handyworker2")));
		super.unauthenticate();
	}

	@Test
	public void createAndSaveFail() {
		super.authenticate("handyworker2");
		Application application = this.applicationService.create();
		Assert.notNull(application);
		//		SchemaPrinter.print(application);

		Application clon = (Application) application.clone();

		Money money = new Money();
		money.setAmount(89.95);
		money.setCurrency("Euros");
		money.setVatTax(21);
		clon.setOfferedPrice(money);

		//Intenta aplicar a una fixUpTask con endDate ya pasada o con una application accepted
		FixUpTask fut = this.fixUpTaskService.findOne(super.getEntityId("fixUpTask1"));

		clon.setFixUpTask(fut);

		application = clon;

		this.exception.expect(IllegalArgumentException.class);
		Application saved = this.applicationService.save(application);
		Assert.isNull(saved);

		super.unauthenticate();
	}

	@Test
	public void createAndSaveFail2() {
		super.authenticate("handyworker2");
		Application application = this.applicationService.create();
		Assert.notNull(application);
		//		SchemaPrinter.print(application);

		Application clon = (Application) application.clone();

		Money money = new Money();
		money.setAmount(89.95);
		money.setCurrency("Euros");
		money.setVatTax(21);
		clon.setOfferedPrice(money);

		//El handyworker ya tiene una application pending en esta fixuptask
		FixUpTask fut = this.fixUpTaskService.findOne(super.getEntityId("fixUpTask7"));

		clon.setFixUpTask(fut);

		application = clon;

		this.exception.expect(IllegalArgumentException.class);
		Application saved = this.applicationService.save(application);
		Assert.isNull(saved);
		//		SchemaPrinter.print(saved);

		super.unauthenticate();
	}

	@Test
	public void findOneTest() {
		super.authenticate("handyworker2");
		Application a = this.applicationService.findOne(super.getEntityId("application8"));
		Assert.notNull(a);

		super.unauthenticate();
	}

	@Test
	public void findOneTest2() {
		super.authenticate("customer");
		Application a = this.applicationService.findOne(super.getEntityId("application8"));
		Assert.notNull(a);

		super.unauthenticate();
	}

	@Test
	public void findOneTestFail() {
		//No es el dueño de la application
		super.authenticate("handyworker3");
		this.exception.expect(IllegalArgumentException.class);
		Application a = this.applicationService.findOne(super.getEntityId("application8"));
		Assert.isNull(a);

		super.unauthenticate();
	}

	@Test
	public void findOneTestFail2() {
		//No es el dueño de la fixUpTask de la application
		super.authenticate("customer3");
		this.exception.expect(IllegalArgumentException.class);
		Application a = this.applicationService.findOne(super.getEntityId("application8"));
		Assert.isNull(a);

		super.unauthenticate();
	}

	@Test
	public void saveApplicationHandyWorker() {
		super.authenticate("handyworker2");

		Application old = this.applicationService.findOne(super.getEntityId("application8"));

		Application clon = (Application) old.clone();

		Money money = new Money();
		money.setAmount(57.25);
		money.setCurrency("Euros");
		money.setVatTax(21.);
		clon.setOfferedPrice(money);

		clon.setComments(old.getComments());
		String comment = "Comentario nuevo";
		clon.getComments().add(comment);

		old = clon;
		Application saved = this.applicationService.save(old);
		Assert.notNull(saved);
		Assert.isTrue(money.getAmount() == this.applicationService.findOne(super.getEntityId("application8")).getOfferedPrice().getAmount());
		Assert.isTrue(this.applicationService.findOne(super.getEntityId("application8")).getComments().contains(comment));
		super.unauthenticate();
	}

	@Test
	public void saveApplicationHandyWorkerFail() {
		super.authenticate("handyworker2");

		Application old = this.applicationService.findOne(super.getEntityId("application2"));

		Application clon = (Application) old.clone();

		Money money = new Money();
		money.setAmount(57.25);
		money.setCurrency("Euros");
		money.setVatTax(21.);
		clon.setOfferedPrice(money);

		clon.setComments(old.getComments());
		String comment = "Comentario nuevo";
		clon.getComments().add(comment);

		//Tan solo puede cambiar el offeredprice (en caso de que el estado 
		//sea "PENDING") y añadir comentarios

		clon.setStatus("ACCEPTED");
		
		old = clon;
		this.exception.expect(IllegalArgumentException.class);
		Application saved = this.applicationService.save(old);
		Assert.isNull(saved);
		Assert.isTrue(!(money.getAmount() == this.applicationService.findOne(super.getEntityId("application8")).getOfferedPrice().getAmount()));
		Assert.isTrue(!(this.applicationService.findOne(super.getEntityId("application8")).getComments().contains(comment)));
		super.unauthenticate();
	}

	@Test
	public void saveApplicationHandyWorkerFail2() {
		super.authenticate("handyworker2");

		Application old = this.applicationService.findOne(super.getEntityId("application2"));

		Application clon = (Application) old.clone();

		Money money = new Money();
		money.setAmount(57.25);
		money.setCurrency("Euros");
		money.setVatTax(21.);

		//Tan solo puede cambiar el offeredprice (en caso de que el estado 
		//sea "PENDING") y añadir comentarios
		clon.setOfferedPrice(money);

		old = clon;
		this.exception.expect(IllegalArgumentException.class);
		Application saved = this.applicationService.save(old);
		Assert.isNull(saved);
		Assert.isTrue(!(money.getAmount() == this.applicationService.findOne(super.getEntityId("application8")).getOfferedPrice().getAmount()));
		super.unauthenticate();
	}

	@Test
	public void saveApplicationCustomer() {
		super.authenticate("handyworker3");
		Application rejApp = this.applicationService.findOne(super.getEntityId("application9"));
		HandyWorker hw = rejApp.getHandyWorker();
		MessageBox inhw3 = this.messageBoxService.findMessageBoxByNameAndActorId("in box", hw.getId());
		int sizeInhw3 = inhw3.getMessages().size();
		super.unauthenticate();
		
		super.authenticate("customer");
		Application old = this.applicationService.findOne(super.getEntityId("application8"));
		Application clon = (Application) old.clone();

		MessageBox in = this.messageBoxService.findMessageBoxByNameAndActorId("in box", super.getEntityId("customer"));
		int size = in.getMessages().size();
		
		//Modificamos el estado, los comentarios y añadimos una CreditCard
		clon.setStatus("ACCEPTED");
		clon.setComments(old.getComments());
		String comment = "Comentario más nuevo";
		clon.getComments().add(comment);

		CreditCard creditCard = new CreditCard();
		creditCard.setBrandName("VISA");
		creditCard.setCvv(450);
		creditCard.setHolderName("Felipe González");
		creditCard.setExpirationMonth(8);
		creditCard.setExpirationYear(20);
		creditCard.setNumber("4564789");
		clon.setCreditCard(creditCard);

		old = clon;

		Application saved = this.applicationService.save(old);
		Assert.notNull(saved);
		this.applicationService.flush();
		
		Application newAppli = this.applicationService.findOne(super.getEntityId("application8"));
		
		MessageBox inmod = this.messageBoxService.findMessageBoxByNameAndActorId("in box", super.getEntityId("customer"));
		Assert.isTrue(size< inmod.getMessages().size());
		
		
		Assert.isTrue(newAppli.getComments().contains(comment));
		Assert.isTrue(newAppli.getStatus().equals("ACCEPTED"));
		Assert.isTrue(newAppli.getCreditCard().equals(creditCard));
		
		super.unauthenticate();
		
		super.authenticate("handyworker3");
		Application newRej = this.applicationService.findOne(super.getEntityId("application9"));
		SchemaPrinter.print(newRej);
		MessageBox inhw3Mod = this.messageBoxService.findMessageBoxByNameAndActorId("in box", hw.getId());
		SchemaPrinter.print(inhw3Mod);
		Assert.isTrue(sizeInhw3<inhw3Mod.getMessages().size());
		super.unauthenticate();
		
		
		
	}

	@Test
	public void saveApplicationCustomer2() {
		super.authenticate("customer");
		Application old = this.applicationService.findOne(super.getEntityId("application8"));

		Application clon = (Application) old.clone();

		//Modificamos el estado y añadimos algún comentario	
		clon.setStatus("REJECTED");
		clon.setComments(old.getComments());
		String comment = "No me interesa";
		clon.getComments().add(comment);

		old = clon;

		Application saved = this.applicationService.save(old);
		Assert.notNull(saved);

		Application newAppli = this.applicationService.findOne(super.getEntityId("application8"));

		Assert.isTrue(newAppli.getComments().contains(comment));
		Assert.isTrue(newAppli.getStatus().equals("REJECTED"));

		super.unauthenticate();
	}

	@Test
	public void saveApplicationCustomerFail() {
		super.authenticate("customer");

		Application old = this.applicationService.findOne(super.getEntityId("application8"));
		Application clon = (Application) old.clone();

		//Modificamos el estado, los comentarios y añadimos una CreditCard (Aunque el estado sea "REJECTED")
		clon.setStatus("REJECTED");
		clon.setComments(old.getComments());
		String comment = "Comentario más nuevo";
		clon.getComments().add(comment);

		CreditCard creditCard = new CreditCard();
		creditCard.setBrandName("VISA");
		creditCard.setCvv(450);
		creditCard.setHolderName("Felipe González");
		creditCard.setExpirationMonth(8);
		creditCard.setExpirationYear(20);
		creditCard.setNumber("4564789");
		clon.setCreditCard(creditCard);

		old = clon;

		this.exception.expect(IllegalArgumentException.class);
		Application saved = this.applicationService.save(old);
		Assert.notNull(saved);

		Application newAppli = this.applicationService.findOne(super.getEntityId("application8"));

		Assert.isTrue(!newAppli.getComments().contains(comment));
		Assert.isTrue(!newAppli.getStatus().equals("REJECTED"));
		Assert.isTrue(newAppli.getCreditCard() == null);

		super.unauthenticate();
	}

	@Test
	public void deleteApplicationHandyWorker() {
		super.authenticate("handyworker2");
		Application old = this.applicationService.findOne(super.getEntityId("application8"));
		Assert.notNull(old);

		this.applicationService.delete(old);
		this.applicationService.flush();

		HandyWorker hw = this.handyWorkerService.findOne(super.getEntityId("handyworker2"));
		Assert.isTrue(!hw.getApplications().contains(old));
		//		SchemaPrinter.print(this.handyWorkerService.findOne(super.getEntityId("handyworker2")));

		super.unauthenticate();
	}

	@Test
	public void deleteApplicationHandyWorkerFail() {
		super.authenticate("handyworker2");
		Application old = this.applicationService.findOne(super.getEntityId("application8"));
		Assert.notNull(old);
		super.unauthenticate();
		super.authenticate("handyworker3");
		//Una handyworker que no es el dueño intenta borrar la application
		this.exception.expect(IllegalArgumentException.class);
		this.applicationService.delete(old);
		this.applicationService.flush();

		HandyWorker hw = this.handyWorkerService.findOne(super.getEntityId("handyworker2"));
		Assert.isTrue(hw.getApplications().contains(old));
		super.unauthenticate();
	}

	@Test
	public void deleteFixUpTasksApplications() {
		super.authenticate("customer");
		FixUpTask fut = this.fixUpTaskService.findOne(super.getEntityId("fixUpTask7"));
		Assert.notNull(fut);

		this.applicationService.deleteFixUpTaskApplications(fut);
		this.applicationService.flush();
		//		SchemaPrinter.print(this.fixUpTaskService.findOne(super.getEntityId("fixUpTask7")));
		Assert.isTrue(this.fixUpTaskService.findOne(super.getEntityId("fixUpTask7")).getApplications().isEmpty());
		//		SchemaPrinter.print(this.fixUpTaskService.findOne(super.getEntityId("fixUpTask7")).getApplications());
		super.unauthenticate();
	}

}
