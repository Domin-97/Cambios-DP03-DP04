
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
import domain.HandyWorker;
import domain.Money;
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
	private ApplicationService	applicationService;


	//Simple CRUD methods
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

	public FixUpTask create() {
		UserAccount userAccount;
		//Check that is a customer
		userAccount = LoginService.getPrincipal();
		final Authority auth = new Authority();
		auth.setAuthority(Authority.CUSTOMER);
		Assert.isTrue(userAccount.getAuthorities().contains(auth));

		FixUpTask result;
		result = new FixUpTask();
		result.setApplications(new ArrayList<Application>());
		result.setComplaints(new ArrayList<Complaint>());
		//Customer that is authenticated
		final Customer c = (Customer) this.actorService.findByUserAccountId(userAccount.getId());
		result.setCustomer(c);
		result.setWorkPlan(new ArrayList<Phase>());
		result.setmaxPrice(new Money());

		return result;

	}

	public FixUpTask save(final FixUpTask fixUpTask) {
		Assert.notNull(fixUpTask);
		FixUpTask result;
		UserAccount userAccount;
		//Check that is a customer or handy worker
		userAccount = LoginService.getPrincipal();
		final Authority auth = new Authority();
		auth.setAuthority(Authority.CUSTOMER);
		final Authority auth2 = new Authority();
		auth2.setAuthority(Authority.HANDYWORKER);
		Assert.isTrue(userAccount.getAuthorities().contains(auth) || userAccount.getAuthorities().contains(auth2));

		//CASE 1: creating a new fixUpTask
		if (fixUpTask.getId() == 0) {
			Assert.isTrue(userAccount.getAuthorities().contains(auth));
			final Customer c = (Customer) this.actorService.findByUserAccountId(userAccount.getId());
			fixUpTask.setApplications(new ArrayList<Application>());
			fixUpTask.setComplaints(new ArrayList<Complaint>());
			fixUpTask.setWorkPlan(new ArrayList<Phase>());
			fixUpTask.setCustomer(c);
			fixUpTask.setPublishedMoment(DateTime.now().minusMillis(1000).toDate());
			fixUpTask.setTicker(Tickers.generateTicker());
			Assert.isTrue(fixUpTask.getPublishedMoment().before(fixUpTask.getStartDate()) && fixUpTask.getStartDate().before(fixUpTask.getEndDate()));
			Assert.notNull(fixUpTask.getWarranty());
			Assert.isTrue(fixUpTask.getWarranty().getFinalMode());
			result = this.fixUpTaskRepository.save(fixUpTask);
			//CASE 2: HandyWorker updating
		} else if (userAccount.getAuthorities().contains(auth2)) {
			final HandyWorker hw = (HandyWorker) this.actorService.findByUserAccountId(userAccount.getId());
			final FixUpTask old = this.findOne(fixUpTask.getId());
			Assert.isTrue(old.getAddress().equals(fixUpTask.getAddress()));
			Assert.isTrue(old.getCategory().equals(fixUpTask.getCategory()));
			Assert.isTrue(old.getCustomer().equals(fixUpTask.getCustomer()));
			Assert.isTrue(old.getDescription().equals(fixUpTask.getDescription()));
			Assert.isTrue(old.getEndDate().equals(fixUpTask.getEndDate()));
			Assert.isTrue(old.getmaxPrice().equals(fixUpTask.getmaxPrice()));
			Assert.isTrue(old.getPublishedMoment().equals(fixUpTask.getPublishedMoment()));
			Assert.isTrue(old.getStartDate().equals(fixUpTask.getStartDate()));
			Assert.isTrue(old.getTicker().equals(fixUpTask.getTicker()));
			Assert.isTrue(old.getWarranty().equals(fixUpTask.getWarranty()));
			final List<Application> acceptedAppl = new ArrayList<Application>(this.findAcceptedApplications(fixUpTask.getId()));
			//Update work plan
			if (acceptedAppl.isEmpty() || (!acceptedAppl.isEmpty() && !acceptedAppl.get(0).getHandyWorker().equals(hw)))
				Assert.isTrue(old.getWorkPlan().equals(fixUpTask.getWorkPlan()));
			//Update applications
			if (this.findAcceptedApplications(fixUpTask.getId()).size() == 1)
				Assert.isTrue(old.getApplications().equals(fixUpTask.getApplications()));
			else {
				final List<Application> aux = new ArrayList<Application>(fixUpTask.getApplications());
				aux.removeAll(old.getApplications());
				Assert.isTrue(aux.isEmpty() || (aux.size() == 1 && aux.get(0).getHandyWorker().equals(hw)));
			}
			result = this.fixUpTaskRepository.save(fixUpTask);
			//CASE 3: Customer updating
		} else {
			final Customer c = (Customer) this.actorService.findByUserAccountId(userAccount.getId());
			final FixUpTask old = this.findOne(fixUpTask.getId());
			Assert.isTrue(old.getWorkPlan().equals(fixUpTask.getWorkPlan()));
			Assert.isTrue(old.getTicker().equals(fixUpTask.getTicker()));
			Assert.isTrue(old.getCustomer().equals(fixUpTask.getCustomer()) && fixUpTask.getCustomer().equals(c));
			Assert.isTrue(old.getPublishedMoment().equals(fixUpTask.getPublishedMoment()));

			Assert.isTrue(fixUpTask.getPublishedMoment().before(fixUpTask.getStartDate()) && fixUpTask.getStartDate().before(fixUpTask.getEndDate()));
			Assert.notNull(fixUpTask.getWarranty());
			Assert.isTrue(fixUpTask.getWarranty().getFinalMode());

			result = this.fixUpTaskRepository.save(fixUpTask);
		}
		return result;
	}

	public void delete(final FixUpTask fixUpTask) {

		Assert.notNull(fixUpTask);
		//Check that is a customer
		UserAccount userAccount;
		userAccount = LoginService.getPrincipal();
		final Authority auth = new Authority();
		auth.setAuthority(Authority.CUSTOMER);
		Assert.isTrue(userAccount.getAuthorities().contains(auth));

		final Customer c = (Customer) this.actorService.findByUserAccountId(userAccount.getId());

		Assert.isTrue(this.findByCustomer(c.getId()).contains(fixUpTask));
		Assert.isTrue(this.findAcceptedApplications(fixUpTask.getId()).isEmpty());
		Assert.isTrue(fixUpTask.getComplaints().isEmpty());

		this.applicationService.deleteFixUpTaskApplications(fixUpTask);

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
	public Collection<Application> findAcceptedApplications(final int FixUpTaskid) {
		final List<Application> result = new ArrayList<Application>();
		result.addAll(this.fixUpTaskRepository.findAcceptedApplications(FixUpTaskid));
		return result;

	}
	public Collection<Application> findAcceptedApplications() {
		final List<Application> result = new ArrayList<Application>();
		result.addAll(this.fixUpTaskRepository.findAcceptedApplications());
		return result;

	}
}
