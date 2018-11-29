package services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.joda.time.DateTime;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import security.LoginService;
import security.UserAccount;
import utilities.AbstractTest;
import utilities.internal.SchemaPrinter;
import domain.Application;
import domain.Category;
import domain.Complaint;
import domain.Customer;
import domain.FixUpTask;
import domain.Money;
import domain.Phase;
import domain.Warranty;

@ContextConfiguration(locations = { "classpath:spring/junit.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class FixUpTaskServiceTest extends AbstractTest {

	@Autowired
	private FixUpTaskService fixUpTaskService;

	@Autowired
	private ApplicationService applicationService;

	@Autowired
	private ActorService actorService;

	@Autowired
	private WarrantyService warrantyService;

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private HandyWorkerService handyWorkerService;

	@Autowired
	private PhaseService phaseService;

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	// Test findAll()
	// Expected all the fixUpTasks returned.
	@Test
	public void testFindAllFixUpTask() {
		Assert.isTrue(this.fixUpTaskService.findAll().size() >= 1);
	}

	// Test FindOne()
	// Expected the return of the entity
	@Test
	public void testFindOneFixUpTask() {
		final FixUpTask fut;
		final int id = super.getEntityId("fixUpTask1");
		fut = this.fixUpTaskService.findOne(id);
		Assert.notNull(fut);
	}

	// Test findOne()2
	// Expected the return of null because no entity has this id
	@Test
	public void testFindOneFixUpTask2() {
		FixUpTask fut;
		final int id = -1;
		fut = this.fixUpTaskService.findOne(id);
		Assert.isNull(fut);
	}

	// Test createFixUpTask()
	// Expected the correct creation of the entity
	@Test
	public void testCreateFixUpTask() {
		super.authenticate("customer2");
		final FixUpTask p = this.fixUpTaskService.create();
		Assert.notNull(p);
		Assert.isTrue(p.getComplaints().equals(new ArrayList<Complaint>()));
		Assert.isTrue(p.getApplications().equals(new ArrayList<Application>()));
		Assert.isTrue(p.getWorkPlan().equals(new ArrayList<Phase>()));
		final UserAccount userAccount = LoginService.getPrincipal();
		final Customer c = (Customer) this.actorService
				.findByUserAccountId(userAccount.getId());
		Assert.isTrue(p.getCustomer().equals(c));
		Assert.notNull(p.getmaxPrice());
		super.unauthenticate();
	}

	// Creating a new FixUpTask and saving it
	// Expected the creation of the entity and that is saved in the database
	@Test
	public void testSaveFixUpTask() {
		super.authenticate("customer");
		final FixUpTask fut = this.fixUpTaskService.create();
		fut.setAddress("Address");
		fut.setDescription("Description");
		fut.setEndDate(DateTime.now().plusDays(15).toDate());
		fut.setStartDate(DateTime.now().plusDays(5).toDate());
		final Money m = new Money();
		m.setAmount(50);
		m.setCurrency("euro");
		m.setVatTax(21);
		fut.setmaxPrice(m);
		final int idCat = super.getEntityId("category2");
		final int idWarr = super.getEntityId("warranty1");
		final Category cat = this.categoryService.findOne(idCat);
		final Warranty war = this.warrantyService.findOne(idWarr);
		fut.setWarranty(war);
		fut.setCategory(cat);
		this.fixUpTaskService.save(fut);
		this.unauthenticate();
	}

	// //Creating a new FixUpTask and saving it
	// //Expected the IAE, no user auth
	@Test
	public void testSaveFixUpTask2() {
		this.exception.expect(IllegalArgumentException.class);
		final FixUpTask fut = this.fixUpTaskService.create();
		fut.setAddress("Address");
		fut.setDescription("Description");
		fut.setEndDate(DateTime.now().plusDays(15).toDate());
		fut.setStartDate(DateTime.now().plusDays(5).toDate());
		final Money m = new Money();
		m.setAmount(50);
		m.setCurrency("euro");
		m.setVatTax(21);
		fut.setmaxPrice(m);
		final int idCat = super.getEntityId("category2");
		final int idWarr = super.getEntityId("warranty1");
		final Category cat = this.categoryService.findOne(idCat);
		final Warranty war = this.warrantyService.findOne(idWarr);
		fut.setWarranty(war);
		fut.setCategory(cat);
		this.fixUpTaskService.save(fut);
	}

	// Creating a new FixUpTask and saving it
	// Expected the IAE, handy Worker authenticated
	@Test
	public void testSaveFixUpTask3() {
		super.authenticate("customer");
		final FixUpTask fut = this.fixUpTaskService.create();
		super.unauthenticate();
		super.authenticate("handyworker");
		fut.setAddress("Address");
		fut.setDescription("Description");
		fut.setEndDate(DateTime.now().plusDays(15).toDate());
		fut.setStartDate(DateTime.now().plusDays(5).toDate());
		final Money m = new Money();
		m.setAmount(50);
		m.setCurrency("euro");
		m.setVatTax(21);
		fut.setmaxPrice(m);
		final int idCat = super.getEntityId("category2");
		final int idWarr = super.getEntityId("warranty1");
		final Category cat = this.categoryService.findOne(idCat);
		final Warranty war = this.warrantyService.findOne(idWarr);
		fut.setWarranty(war);
		fut.setCategory(cat);
		this.exception.expect(IllegalArgumentException.class);
		this.fixUpTaskService.save(fut);
		super.unauthenticate();
	}

	// Updating a FixUpTask
	// Expected the IAE, handy Worker trying to change attributes that only can
	// change the customer
	@Test
	public void testSaveFixUpTask4() {
		super.authenticate("handyWorker");
		final int idFuT = super.getEntityId("fixUpTask1");
		final FixUpTask fut = this.fixUpTaskService.findOne(idFuT);
		final FixUpTask clon = (FixUpTask) fut.clone();
		clon.setAddress("Address");
		clon.setDescription("Description");
		clon.setEndDate(DateTime.now().plusDays(15).toDate());
		clon.setStartDate(DateTime.now().plusDays(5).toDate());
		final Money m = new Money();
		m.setAmount(50);
		m.setCurrency("euro");
		m.setVatTax(21);
		clon.setmaxPrice(m);
		final int idCat = super.getEntityId("category2");
		final int idWarr = super.getEntityId("warranty1");
		final Category cat = this.categoryService.findOne(idCat);
		final Warranty war = this.warrantyService.findOne(idWarr);
		clon.setWarranty(war);
		clon.setCategory(cat);
		this.exception.expect(IllegalArgumentException.class);
		this.fixUpTaskService.save(clon);
		super.unauthenticate();
	}

	// //Updating a FixUpTask
	// //Expected the save of the entity
	@Test
	public void testSaveFixUpTask5() {
		super.authenticate("handyWorker3");
		final int idFuT = super.getEntityId("fixUpTask3");
		final FixUpTask fut = this.fixUpTaskService.findOne(idFuT);
		final FixUpTask clon = (FixUpTask) fut.clone();
		Application app = this.applicationService.create();
		app.setMoment(fut.getStartDate());
		app.setFixUpTask(fut);
		app.setHandyWorker(this.handyWorkerService.findOne(super
				.getEntityId("handyworker3")));
		app = this.applicationService.save(app);
		final List<Application> apps = new ArrayList<Application>(
				fut.getApplications());
		apps.add(app);
		clon.setApplications(apps);
		super.unauthenticate();
	}

	// Updating a FixUpTask changing the workPlan
	// Expected the save of the entity
	@Test
	public void testSaveFixUpTask6() {
		super.authenticate("handyWorker2");
		final int idFuT = super.getEntityId("fixUpTask1");
		final FixUpTask fut = this.fixUpTaskService.findOne(idFuT);
		final FixUpTask clon = (FixUpTask) fut.clone();
		Phase p = this.phaseService.create();
		final Date start = DateTime.parse(fut.getStartDate().toString())
				.plusDays(1).toDate();
		final Date end = DateTime.parse(fut.getStartDate().toString())
				.plusDays(2).toDate();
		p.setStartMoment(start);
		p.setEndTime(end);
		p.setNumber(5);
		p.setDescription("Description");
		p.setTitle("Title");
		p = this.phaseService.save(p);
		final List<Phase> wp = new ArrayList<Phase>(fut.getWorkPlan());
		wp.add(p);
		clon.setWorkPlan(wp);
		this.fixUpTaskService.save(clon);
		Assert.isTrue(this.fixUpTaskService.findOne(idFuT).equals(clon));
		super.unauthenticate();
	}

	// Updating a FixUpTask changing the workPlan
	// Expected IAE, the FuT has an accepted application from another HW
	@Test
	public void testSaveFixUpTask7() {
		super.authenticate("handyWorker");
		final int idFuT = super.getEntityId("fixUpTask1");
		final FixUpTask fut = this.fixUpTaskService.findOne(idFuT);
		final FixUpTask clon = (FixUpTask) fut.clone();
		Phase p = this.phaseService.create();
		final Date start = DateTime.parse(fut.getStartDate().toString())
				.plusDays(1).toDate();
		final Date end = DateTime.parse(fut.getStartDate().toString())
				.plusDays(2).toDate();
		p.setStartMoment(start);
		p.setEndTime(end);
		p.setNumber(5);
		p.setDescription("Description");
		p.setTitle("Title");
		p = this.phaseService.save(p);
		final List<Phase> wp = new ArrayList<Phase>(fut.getWorkPlan());
		wp.add(p);
		clon.setWorkPlan(wp);
		this.exception.expect(IllegalArgumentException.class);
		this.fixUpTaskService.save(clon);
		super.unauthenticate();
	}

	// Updating a FixUpTask changing the workPlan
	// Expected IAE, the FuT hasn´t an accepted application
	@Test
	public void testSaveFixUpTask8() {
		super.authenticate("handyWorker");
		final int idFuT = super.getEntityId("fixUpTask2");
		final FixUpTask fut = this.fixUpTaskService.findOne(idFuT);
		final FixUpTask clon = (FixUpTask) fut.clone();
		Phase p = this.phaseService.create();
		final Date start = DateTime.parse(fut.getStartDate().toString())
				.plusDays(1).toDate();
		final Date end = DateTime.parse(fut.getStartDate().toString())
				.plusDays(2).toDate();
		p.setStartMoment(start);
		p.setEndTime(end);
		p.setNumber(5);
		p.setDescription("Description");
		p.setTitle("Title");
		p = this.phaseService.save(p);
		final List<Phase> wp = new ArrayList<Phase>(fut.getWorkPlan());
		wp.add(p);
		clon.setWorkPlan(wp);
		this.exception.expect(IllegalArgumentException.class);
		this.fixUpTaskService.save(clon);
		super.unauthenticate();
	}

	// Updating a FixUpTask
	// Expected the save of the entity
	@Test
	public void testSaveFixUpTask9() {
		super.authenticate("customer");
		final int idFuT = super.getEntityId("fixUpTask1");
		final FixUpTask fut = this.fixUpTaskService.findOne(idFuT);
		final FixUpTask clon = (FixUpTask) fut.clone();
		clon.setAddress("Address");
		clon.setDescription("Description");
		clon.setEndDate(DateTime.now().plusDays(15).toDate());
		clon.setStartDate(DateTime.now().plusDays(5).toDate());
		final Money m = new Money();
		m.setAmount(50);
		m.setCurrency("euro");
		m.setVatTax(21);
		clon.setmaxPrice(m);
		final int idCat = super.getEntityId("category2");
		final int idWarr = super.getEntityId("warranty1");
		final Category cat = this.categoryService.findOne(idCat);
		final Warranty war = this.warrantyService.findOne(idWarr);
		clon.setWarranty(war);
		clon.setCategory(cat);
		this.fixUpTaskService.save(clon);
		Assert.isTrue(this.fixUpTaskService.findOne(idFuT).equals(clon));
		super.unauthenticate();
	}

	// Updating a FixUpTask
	// Expected IAE, the customer is not the owner of the FuT
	@Test
	public void testSaveFixUpTask10() {
		super.authenticate("customer2");
		final int idFuT = super.getEntityId("fixUpTask1");
		final FixUpTask fut = this.fixUpTaskService.findOne(idFuT);
		final FixUpTask clon = (FixUpTask) fut.clone();
		clon.setAddress("Address");
		clon.setDescription("Description");
		clon.setEndDate(DateTime.now().plusDays(15).toDate());
		clon.setStartDate(DateTime.now().plusDays(5).toDate());
		final Money m = new Money();
		m.setAmount(50);
		m.setCurrency("euro");
		m.setVatTax(21);
		clon.setmaxPrice(m);
		final int idCat = super.getEntityId("category2");
		final int idWarr = super.getEntityId("warranty1");
		final Category cat = this.categoryService.findOne(idCat);
		final Warranty war = this.warrantyService.findOne(idWarr);
		clon.setWarranty(war);
		clon.setCategory(cat);
		this.exception.expect(IllegalArgumentException.class);
		this.fixUpTaskService.save(clon);
		Assert.isTrue(this.fixUpTaskService.findOne(idFuT).equals(clon));
		super.unauthenticate();
	}

	// Updating a FixUpTask changing the workPlan
	// Expected IAE, customer cant change the workplan
	@Test
	public void testSaveFixUpTask11() {
		super.authenticate("handyWorker");
		final int idFuT = super.getEntityId("fixUpTask1");
		final FixUpTask fut = this.fixUpTaskService.findOne(idFuT);
		final FixUpTask clon = (FixUpTask) fut.clone();
		Phase p = this.phaseService.create();
		final Date start = DateTime.parse(fut.getStartDate().toString())
				.plusDays(1).toDate();
		final Date end = DateTime.parse(fut.getStartDate().toString())
				.plusDays(2).toDate();
		p.setStartMoment(start);
		p.setEndTime(end);
		p.setNumber(5);
		p.setDescription("Description");
		p.setTitle("Title");
		p = this.phaseService.save(p);
		super.unauthenticate();
		super.authenticate("customer");
		final List<Phase> wp = new ArrayList<Phase>(fut.getWorkPlan());
		wp.add(p);
		clon.setWorkPlan(wp);
		this.exception.expect(IllegalArgumentException.class);
		this.fixUpTaskService.save(clon);
		Assert.isTrue(this.fixUpTaskService.findOne(idFuT).equals(clon));
		super.unauthenticate();
	}

	// Updating a FixUpTask
	// Expected IAE, dates in wrong range
	@Test
	public void testSaveFixUpTask12() {
		super.authenticate("customer");
		final int idFuT = super.getEntityId("fixUpTask1");
		final FixUpTask fut = this.fixUpTaskService.findOne(idFuT);
		final FixUpTask clon = (FixUpTask) fut.clone();
		clon.setAddress("Address");
		clon.setDescription("Description");
		clon.setEndDate(DateTime.now().plusDays(15).toDate());
		clon.setStartDate(DateTime.now().plusDays(20).toDate());
		final Money m = new Money();
		m.setAmount(50);
		m.setCurrency("euro");
		m.setVatTax(21);
		clon.setmaxPrice(m);
		final int idCat = super.getEntityId("category2");
		final int idWarr = super.getEntityId("warranty1");
		final Category cat = this.categoryService.findOne(idCat);
		final Warranty war = this.warrantyService.findOne(idWarr);
		clon.setWarranty(war);
		clon.setCategory(cat);
		this.exception.expect(IllegalArgumentException.class);
		this.fixUpTaskService.save(clon);
		Assert.isTrue(this.fixUpTaskService.findOne(idFuT).equals(clon));
		super.unauthenticate();
	}

	// Updating a FixUpTask
	// Expected IAE, Warranty in draft mode
	@Test
	public void testSaveFixUpTask13() {
		super.authenticate("customer");
		final int idFuT = super.getEntityId("fixUpTask1");
		final FixUpTask fut = this.fixUpTaskService.findOne(idFuT);
		final FixUpTask clon = (FixUpTask) fut.clone();
		clon.setAddress("Address");
		clon.setDescription("Description");
		clon.setEndDate(DateTime.now().plusDays(15).toDate());
		clon.setStartDate(DateTime.now().plusDays(5).toDate());
		final Money m = new Money();
		m.setAmount(50);
		m.setCurrency("euro");
		m.setVatTax(21);
		clon.setmaxPrice(m);
		final int idCat = super.getEntityId("category2");
		final int idWarr = super.getEntityId("warranty2");
		final Category cat = this.categoryService.findOne(idCat);
		final Warranty war = this.warrantyService.findOne(idWarr);
		clon.setWarranty(war);
		clon.setCategory(cat);
		this.exception.expect(IllegalArgumentException.class);
		this.fixUpTaskService.save(clon);
		Assert.isTrue(this.fixUpTaskService.findOne(idFuT).equals(clon));
		super.unauthenticate();
	}

	// Deleting a FuT
	// Expected the delete of the entity
	@Test
	public void testDeleteFixUpTask() {
		super.authenticate("customer");
		final FixUpTask fut = this.fixUpTaskService.findOne(super
				.getEntityId("fixUpTask2"));
		final List<Application> apps = new ArrayList<Application>(
				this.applicationService.findAll());
		apps.retainAll(fut.getApplications());
		this.fixUpTaskService.delete(fut);
		Assert.isNull(this.fixUpTaskService.findOne(super
				.getEntityId("fixUpTask2")));
		Assert.isTrue(apps.isEmpty());
		super.unauthenticate();
	}

	// Deleting a FuT
	// Expected IAE, FuT has an accepted application
	@Test
	public void testDeleteFixUpTask2() {
		super.authenticate("customer");
		final FixUpTask fut = this.fixUpTaskService.findOne(super
				.getEntityId("fixUpTask1"));
		final List<Application> apps = new ArrayList<Application>(
				this.applicationService.findAll());
		apps.retainAll(fut.getApplications());
		this.exception.expect(IllegalArgumentException.class);
		this.fixUpTaskService.delete(fut);
		Assert.isNull(this.fixUpTaskService.findOne(super
				.getEntityId("fixUpTask1")));
		Assert.isTrue(apps.isEmpty());
		super.unauthenticate();
	}

	// Deleting a FuT
	// Expected IAE, the customer2 is not the owner
	@Test
	public void testDeleteFixUpTask3() {
		super.authenticate("customer2");
		final FixUpTask fut = this.fixUpTaskService.findOne(super
				.getEntityId("fixUpTask2"));
		final List<Application> apps = new ArrayList<Application>(
				this.applicationService.findAll());
		apps.retainAll(fut.getApplications());
		this.exception.expect(IllegalArgumentException.class);
		this.fixUpTaskService.delete(fut);
		Assert.isNull(this.fixUpTaskService.findOne(super
				.getEntityId("fixUpTask2")));
		Assert.isTrue(apps.isEmpty());
		super.unauthenticate();
	}

	// Test findAll()
	// Expected all the fixUpTasks returned.
	@Test
	public void testFindAcceptedApplications() {
		Assert.isTrue(this.fixUpTaskService.findAcceptedApplications().size() >= 1);
	}


}
