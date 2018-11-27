
package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import repositories.FixUpTaskRepository;
import security.Authority;
import security.LoginService;
import security.UserAccount;
import utilities.Tickers;
import domain.Application;
import domain.Complaint;
import domain.Customer;
import domain.FixUpTask;
import domain.Phase;

@Service
@Transactional
public class FixUpTaskService {

	//Managed repositories

	@Autowired
	private FixUpTaskRepository	fixUpTaskRepository;

	//Suporting repositories
	@Autowired
	private ActorService		actorService;
	@Autowired
	private CategoryService		categoryService;
	@Autowired
	private WarrantyService		warrantyService;
	@Autowired
	private ApplicationService	applicationService;
	@Autowired
	private ComplaintService	complaintService;


	//Simple CRUD methods

	public FixUpTask create() {
		UserAccount userAccount;
		userAccount = LoginService.getPrincipal();
		final Authority auth = new Authority();
		auth.setAuthority(Authority.CUSTOMER);
		Assert.isTrue(userAccount.getAuthorities().contains(auth));

		FixUpTask result;
		result = new FixUpTask();
		result.setTicker(Tickers.getTickerFixUpTicker(result.hashCode()));
		result.setApplications(new ArrayList<Application>());
		result.setComplaints(new ArrayList<Complaint>());
		//Customer que este autenticado
		final Customer c = (Customer) this.actorService.findByUserAccountId(userAccount.getId());
		result.setCustomer(c);
		result.setWorkPlan(new ArrayList<Phase>());

		return result;

	}

	public FixUpTask save(final FixUpTask fixUpTask) {
		Assert.notNull(fixUpTask);
		UserAccount userAccount;
		userAccount = LoginService.getPrincipal();
		final Authority auth = new Authority();
		auth.setAuthority(Authority.CUSTOMER);
		Assert.isTrue(userAccount.getAuthorities().contains(auth));

		final FixUpTask result;
		final Customer c = (Customer) this.actorService.findByUserAccountId(userAccount.getId());

		if (fixUpTask.getCategory() == null)
			fixUpTask.setCategory(this.categoryService.findByName("CATEGORY"));
		else if (this.categoryService.findOne(fixUpTask.getCategory().getId()) == null)
			fixUpTask.setCategory(this.categoryService.save(fixUpTask.getCategory()));

		if (fixUpTask.getWarranty() == null || this.categoryService.findOne(fixUpTask.getCategory().getId()) == null)
			fixUpTask.setWarranty(this.warrantyService.findWarrantiesFinalMode().get(0));

		Assert.isTrue(fixUpTask.getWarranty().getFinalMode());

		if (fixUpTask.getId() != 0) {
			Assert.isTrue(this.findByCustomer(c.getId()).contains(fixUpTask));
			Assert.isTrue(fixUpTask.getStartDate().after(fixUpTask.getPublishedMoment()) && fixUpTask.getEndDate().after(fixUpTask.getPublishedMoment()) && fixUpTask.getEndDate().after(fixUpTask.getStartDate()));
			final List<Application> applications = new ArrayList<Application>(this.applicationService.findFixUpTaskApplications(fixUpTask));
			fixUpTask.getApplications().removeAll(applications);
			for (final Application a : applications) {
				Assert.isTrue(a.getFixUpTask().equals(fixUpTask));
				this.applicationService.save(a);

			}
			//Terminar lo de complaints
			final List<Complaint> complaints = new ArrayList<Complaint>(this.complaintService.getComplaintOfFixUpTask(fixUpTask.getId()));
			complaints.removeAll(fixUpTask.getComplaints());

			result = this.fixUpTaskRepository.save(fixUpTask);

		} else {
			fixUpTask.setPublishedMoment(DateTime.now().toDate());
			Assert.isTrue(fixUpTask.getStartDate().after(fixUpTask.getPublishedMoment()) && fixUpTask.getEndDate().after(fixUpTask.getPublishedMoment()) && fixUpTask.getEndDate().after(fixUpTask.getStartDate()));
			final List<Application> applications = new ArrayList<Application>(this.applicationService.findFixUpTaskApplications(fixUpTask));
			fixUpTask.getApplications().removeAll(applications);
			for (final Application a : applications) {
				Assert.isTrue(a.getFixUpTask().equals(fixUpTask));
				this.applicationService.save(a);

			}
			result = this.fixUpTaskRepository.save(fixUpTask);

		}
		return result;
	}
	public Collection<FixUpTask> findAll() {
		Collection<FixUpTask> result;
		result = this.fixUpTaskRepository.findAll();
		return result;
	}

	public FixUpTask findOne(final int fixUpTaskId) {
		FixUpTask result;
		result = this.fixUpTaskRepository.findOne(fixUpTaskId);
		return result;
	}

	public void delete(final FixUpTask fixUpTask) {

		Assert.notNull(fixUpTask);
		UserAccount userAccount;
		userAccount = LoginService.getPrincipal();
		final Authority auth = new Authority();
		auth.setAuthority(Authority.CUSTOMER);
		Assert.isTrue(userAccount.getAuthorities().contains(auth));

		final Customer c = (Customer) this.actorService.findByUserAccountId(userAccount.getId());

		Assert.isTrue(this.findByCustomer(c.getId()).contains(fixUpTask));

		this.fixUpTaskRepository.delete(fixUpTask);

	}

	//Other methods

	public Collection<FixUpTask> findByKeyword(final String key) {
		final List<FixUpTask> result = new ArrayList<FixUpTask>();
		result.addAll(this.fixUpTaskRepository.findByKeyWork(key));
		return result;
	}

	public Collection<FixUpTask> findByRangeMoney(final int a, final int b) {
		final List<FixUpTask> result = new ArrayList<FixUpTask>();
		result.addAll(this.fixUpTaskRepository.findByMoney(a, b));
		return result;

	}

	public Collection<FixUpTask> findByRangeDate(final Date start, final Date end) {
		final List<FixUpTask> result = new ArrayList<FixUpTask>();
		result.addAll(this.fixUpTaskRepository.findByDates(start, end));
		return result;

	}
	public Collection<FixUpTask> findByCustomer(final int id) {
		final List<FixUpTask> result = new ArrayList<FixUpTask>();
		result.addAll(this.fixUpTaskRepository.findByCustomer(id));
		return result;

	}
}
