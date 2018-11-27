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
import security.UserAccount;
import utilities.AbstractTest;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class ComplaintServiceTest extends AbstractTest {

	@Autowired
	public ComplaintService			complaintService;
	public CustomerService customerService;
	public ReportService reportService;

	@Rule
	public final ExpectedException	exception	= ExpectedException.none();


	@Test
	public void testFindAll() {

		Assert.isTrue(this.complaintService.findAll().size() >= 1);
	}
	@Test
	public void testSaveComplaint() {
		Complaint old;
		old = this.complaintService.findOne(6282);
		super.authenticate("admin");

		this.complaintService.save(old);
		super.unauthenticate();

	}
	@Test
	public void testSaveMalComplaint() {
		super.authenticate("handyworker");
		Complaint old;
		old = this.complaintService.findOne(6282);

		this.exception.expect(IllegalArgumentException.class);
		this.complaintService.save(old);
		super.unauthenticate();
	}
	@Test
	public void testCreateComplaint() {
		super.authenticate("admin");
		Complaint old = new Complaint();
		final UserAccount ua = new UserAccount();
		final Authority auth = new Authority();
		final Collection<Report> reportsWritten = new ArrayList<>();
		final FixUpTask fixUpTask = new FixUpTask();
		final Date moment = new Date();
		final Collection<String> attachments = new ArrayList<>();
		
		
		old.setAttachments(attachments);
		
		old.setDescription("");
		old.setFixUpTask(fixUpTask);
		old.setMoment(moment);
		old.setReport(reportService.findOne(6204));
		old.setTicker("");
		old.setCustomer(customerService.findOne(6093));
		
		this.complaintService.save(old);
		super.unauthenticate();
	}

}
