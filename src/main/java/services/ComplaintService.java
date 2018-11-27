package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import repositories.ComplaintRepository;
import security.Authority;
import security.LoginService;
import security.UserAccount;
import domain.Complaint;
import domain.Customer;
import domain.FixUpTask;
import domain.HandyWorker;
import domain.Report;

@Transactional
@Service
public class ComplaintService {

	// Manage repository----------------------------------------------------------------------------
	@Autowired
	private ComplaintRepository	complaintRepository;

	//Suporting services -------------------------------------------------------------------------------
	@Autowired
	private ActorService		actorService;


	//constructors --------------------------------------------------------------------------------

	public ComplaintService() {
		super();
	}
	//Simple crud method------------------------------------------------------------------------------
	public Collection<Complaint> findAll() {
		Collection<Complaint> result;
		result = this.complaintRepository.findAll();
		return result;
	}

	public Complaint findOne(final int complaintId) {
		Assert.isTrue(complaintId != 0);
		Complaint result;
		result = this.complaintRepository.findOne(complaintId);
		return result;
	}
	public Complaint create() {

		final UserAccount principal = LoginService.getPrincipal();

		Assert.isTrue(principal.getAuthorities().contains(Authority.CUSTOMER));
		final Complaint result = new Complaint();

		final String ticker = "";
		final Date moment = new Date();
		;
		final String description = "";
		final Collection<String> attachments = new ArrayList<>();
		final Report report = new Report();
		final Customer customer = new Customer();
		;
		final FixUpTask fixUpTask = new FixUpTask();
		result.setAttachments(attachments);
		result.setCustomer(customer);
		result.setDescription(description);
		result.setFixUpTask(fixUpTask);
		result.setMoment(moment);
		result.setReport(report);
		result.setTicker(ticker);

		return result;
	}
	public Complaint save(final Complaint complaint) {

		final UserAccount principal = LoginService.getPrincipal();

		Assert.isTrue(principal.getAuthorities().contains(Authority.CUSTOMER));

		Assert.notNull(complaint);

		return this.complaintRepository.save(complaint);
	}
	public Collection<Complaint> getComplaintWithOutReport() {
		Collection<Complaint> result;
		result = this.complaintRepository.getComplaintWithOutReport();

		return result;
	}
	public Collection<Complaint> getComplaintOfHandyWorker() {
		Collection<Complaint> result;
		final HandyWorker hw = (HandyWorker) this.actorService.findByUserAccountId(LoginService.getPrincipal().getId());
		result = this.complaintRepository.getComplaintOfHandyWorker(hw.getId());

		return result;
	}
	public Collection<Complaint> getComplaintOfCustomer() {
		Collection<Complaint> result;
		final Customer cus = (Customer) this.actorService.findByUserAccountId(LoginService.getPrincipal().getId());
		result = this.complaintRepository.getComplaintOfCustomer(cus.getId());

		return result;
	}

	public Collection<Complaint> getComplaintOfFixUpTask(final int id) {
		Collection<Complaint> result;
		result = this.complaintRepository.findFixUpTaskApplications(id);

		return result;
	}
}
