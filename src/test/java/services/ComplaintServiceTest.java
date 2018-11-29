package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.transaction.Transactional;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import domain.Complaint;
import domain.Customer;
import domain.FixUpTask;
import domain.Referee;
import domain.Report;

import security.Authority;
import security.LoginService;
import security.UserAccount;
import utilities.AbstractTest;
import utilities.internal.SchemaPrinter;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class ComplaintServiceTest extends AbstractTest {

	@Autowired
	private ComplaintService complaintService;
	@Autowired
	private CustomerService customerService;
	@Autowired
	private ReportService reportService;
	@Autowired
	private ActorService actorService;
	@Autowired
	private	FixUpTaskService fixUpTaskService;

	@Rule
	public final ExpectedException	exception	= ExpectedException.none();


	@Test
	public void testFindAll() {

		Assert.isTrue(this.complaintService.findAll().size() >= 1);
	}
	@Test
	public void testCreateComplaint() {
		super.authenticate("customer");
		Complaint old = this.complaintService.create();
		Date date = new Date(System.currentTimeMillis());
		//Tienes que usar un generador de tickers
		old.setTicker("20170106-564534");
		old.setMoment(date);
		old.setDescription("DP presioso");
		old.setAttachments(new ArrayList<String>());
		UserAccount principal = LoginService.getPrincipal();
		Customer cus = (Customer)this.actorService.findByUserAccountId(principal.getId());
		old.setCustomer(cus);
		//Este fixUpTask no existe en el nuevo populate
		FixUpTask fut = this.fixUpTaskService.findOne(super.getEntityId("fixUpTask1"));
		old.setFixUpTask(fut);
		Complaint saved =this.complaintService.save(old);
		this.complaintService.flush();
		super.unauthenticate();
	}


}
