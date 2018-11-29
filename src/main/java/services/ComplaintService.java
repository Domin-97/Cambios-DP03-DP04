
package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import repositories.ComplaintRepository;
import repositories.FixUpTaskRepository;
import security.Authority;
import security.LoginService;
import security.UserAccount;
import domain.Actor;
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
	@Autowired
	private CustomerService		customerService;
	@Autowired
	private ReportService		reportService;
	@Autowired
	private FixUpTaskService	fixUpTaskService;

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

	public Complaint findOne(int complaintId) {
		Assert.isTrue(complaintId != 0);
		Complaint result;
		result = this.complaintRepository.findOne(complaintId);
		return result;
	}
	public Complaint create() {

		UserAccount principal = LoginService.getPrincipal();
		Actor a = this.actorService.findByUserAccountId(principal.getId());
		Authority auth = new Authority();
		auth.setAuthority(Authority.CUSTOMER);
		Assert.isTrue((principal.getAuthorities().contains(auth)));
		Complaint result = new Complaint();

		result.setTicker("");
		Date moment = new Date();
		result.setMoment(moment);
		result.setDescription("");
		List<String> attachments = new ArrayList<>();
		result.setAttachments(attachments);
		Customer customer = (Customer) this.actorService.findByUserAccountId(principal.getId());
		result.setCustomer(customer);
		return result;
	}
	public Complaint save(Complaint complaint) {
		Assert.notNull(complaint);
		Assert.isTrue(complaint.getId()==0);
		
		UserAccount principal = LoginService.getPrincipal();
		Authority auth = new Authority();
		auth.setAuthority(Authority.CUSTOMER);
		Assert.isTrue((principal.getAuthorities().contains(auth)));
		
		FixUpTask fxt = this.fixUpTaskService.findOne(complaint.getFixUpTask().getId());
		Assert.notNull(fxt);
		
		Complaint saved = this.complaintRepository.save(complaint);
		fxt.getComplaints().add(saved);
		this.fixUpTaskService.save(fxt);
		return saved;
	}
	public Collection<Complaint> getComplaintWithOutReport() {
		Collection<Complaint> result;
		result = this.complaintRepository.getComplaintWithOutReport();

		return result;
	}
	public Collection<Complaint> getComplaintOfHandyWorker(int hwId) {
		Collection<Complaint> result;
		//HandyWorker hw = (HandyWorker)this.actorService.findByUserAccountId(LoginService.getPrincipal().getId());
		result = this.complaintRepository.getComplaintOfHandyWorker(hwId);

		return result;
	}
	public Collection<Complaint> getComplaintOfCustomer(int cusId) {
		Collection<Complaint> result;
		//Customer cus = (Customer)this.actorService.findByUserAccountId(LoginService.getPrincipal().getId());
		result = this.complaintRepository.getComplaintOfCustomer(cusId);

		return result;
	}
	public Collection<Complaint> getComplaintOfFixUpTask(int id) {
		Collection<Complaint> result;
		result = this.complaintRepository.findFixUpTaskApplications(id);

		return result;
	}
	public Complaint FindComplaintByReportId(int reportId) {
		Complaint result;
		result = this.complaintRepository.findComplaintByReportId(reportId);

		return result;
	}
	public void flush() {
		this.complaintRepository.flush();
		
	}
}
