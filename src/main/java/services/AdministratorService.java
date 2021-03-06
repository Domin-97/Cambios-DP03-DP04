
package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import repositories.AdministratorRepository;
import security.Authority;
import security.LoginService;
import security.UserAccount;
import security.UserAccountService;
import domain.Actor;
import domain.Administrator;
import domain.Customer;
import domain.HandyWorker;
import domain.MessageBox;
import domain.SocialProfile;

@Service
@Transactional
public class AdministratorService {

	// Managed repository -------------------
	@Autowired
	private AdministratorRepository	administratorRepository;

	//-------------------

	// Supporting services -------------------
	@Autowired
	private UserAccountService		userAccountService;
	@Autowired
	private ActorService			actorService;
	@Autowired
	private MessageBoxService		messageBoxService;


	//-------------------

	// Constructors -----------------------------

	public AdministratorService() {
		super();
	}
	//--------------
	public Collection<Administrator> findAll() {
		Collection<Administrator> result;

		result = this.administratorRepository.findAll();

		return result;
	}

	public Administrator findOne(int administratorId) {
		Assert.isTrue(administratorId != 0);
		Administrator result;
		result = this.administratorRepository.findOne(administratorId);
		Assert.notNull(result);
		return result;
	}

	public Administrator create() {
		//Only admins creates admins
		UserAccount principal = LoginService.getPrincipal();
		Assert.notNull(principal);

		Authority auth = new Authority();
		auth.setAuthority(Authority.ADMIN);
		Assert.isTrue(principal.getAuthorities().contains(auth));
		Administrator result = new Administrator();

		Collection<SocialProfile> soc = new ArrayList<SocialProfile>();
		//COSAS COMUNES DE ACTOR
		result.setName("");
		result.setMiddleName("");
		result.setSurname("");
		result.setPhoto("");
		result.setEmail("");
		result.setPhoneNumber("");
		result.setAddress("");
		result.setBanned(false);
		result.setSuspicious(false);
		result.setSocialProfiles(soc);
		List<MessageBox> systemBoxes = new ArrayList<MessageBox>(this.messageBoxService.createSystemMessageBoxes());
		result.setMessageBoxes(systemBoxes);

		//CUENTA
		UserAccount ua = new UserAccount();
		Authority auth2 = new Authority();
		auth2.setAuthority(Authority.ADMIN);
		ua.addAuthority(auth);
		result.setUserAccount(ua);

		return result;
	}

	public Administrator save(Administrator administrator) {
		Assert.notNull(administrator);
		UserAccount principal = LoginService.getPrincipal();
		Assert.notNull(principal);
		//Only admins save admins
		Authority auth = new Authority();
		auth.setAuthority(Authority.ADMIN);
		Assert.isTrue(principal.getAuthorities().contains(auth));
		Administrator result;
		if (administrator.getId() == 0) {
			//Username must be unique
			Collection<String> uas = this.userAccountService.findAllUsernames();
			Assert.isTrue(!uas.contains(administrator.getUserAccount().getUsername()));
			result = this.administratorRepository.save(administrator);
		} else {
			//An actor can NOT list,edit or delete data than belongs to another actor
			Administrator admin = (Administrator) this.actorService.findByUserAccountId(LoginService.getPrincipal().getId());
			Assert.isTrue(admin.getId() == administrator.getId());
			Assert.isTrue(admin.getUserAccount().getAuthorities() == administrator.getUserAccount().getAuthorities());

			result = this.administratorRepository.save(administrator);
		}

		return result;
	}

	//------Queries---------
	//Getting all actors that are suspicious
	public Collection<Actor> getSuspiciousActors() {
		UserAccount principal = LoginService.getPrincipal();
		Assert.notNull(principal);

		Authority auth = new Authority();
		auth.setAuthority(Authority.ADMIN);
		Assert.isTrue(principal.getAuthorities().contains(auth));
		Collection<Actor> result = new ArrayList<Actor>();
		result = this.administratorRepository.getSuspiciousActors();
		return result;

	}
	//The average, the minimum, the maximum, and the standard deviation of the number of fix-up tasks per user.

	public List<Double> getC1() {

		UserAccount principal = LoginService.getPrincipal();
		Assert.notNull(principal);

		Authority auth = new Authority();
		auth.setAuthority(Authority.ADMIN);
		Assert.isTrue(principal.getAuthorities().contains(auth));
		List<Double> result;
		result = this.administratorRepository.getC1();
		return result;

	}
	//The average, the minimum, the maximum, and the standard deviation of the number of applications per fix-up task.

	public List<Double> getC2() {

		UserAccount principal = LoginService.getPrincipal();
		Assert.notNull(principal);

		Authority auth = new Authority();
		auth.setAuthority(Authority.ADMIN);
		Assert.isTrue(principal.getAuthorities().contains(auth));
		List<Double> result;
		result = this.administratorRepository.getC2();
		return result;

	}
	//The average, the minimum, the maximum, and the standard deviation of the maximum price of the fix-up tasks.

	public List<Double> getC3() {

		UserAccount principal = LoginService.getPrincipal();
		Assert.notNull(principal);

		Authority auth = new Authority();
		auth.setAuthority(Authority.ADMIN);
		Assert.isTrue(principal.getAuthorities().contains(auth));
		List<Double> result;
		result = this.administratorRepository.getC3();
		return result;

	}
	//The average, the minimum, the maximum, and the standard deviation of the price offered in the applications.

	public List<Double> getC4() {

		UserAccount principal = LoginService.getPrincipal();
		Assert.notNull(principal);

		Authority auth = new Authority();
		auth.setAuthority(Authority.ADMIN);
		Assert.isTrue(principal.getAuthorities().contains(auth));
		List<Double> result;
		result = this.administratorRepository.getC4();
		return result;

	}
	//The ratio of pending applications.

	public Double getC5() {

		UserAccount principal = LoginService.getPrincipal();
		Assert.notNull(principal);

		Authority auth = new Authority();
		auth.setAuthority(Authority.ADMIN);
		Assert.isTrue(principal.getAuthorities().contains(auth));
		Double result;
		result = this.administratorRepository.getC5();
		return result;

	}
	//The ratio of accepted applications.

	public Double getC6() {

		UserAccount principal = LoginService.getPrincipal();
		Assert.notNull(principal);

		Authority auth = new Authority();
		auth.setAuthority(Authority.ADMIN);
		Assert.isTrue(principal.getAuthorities().contains(auth));
		Double result;
		result = this.administratorRepository.getC6();
		return result;

	}
	//The ratio of rejected applications.

	public Double getC7() {

		UserAccount principal = LoginService.getPrincipal();
		Assert.notNull(principal);

		Authority auth = new Authority();
		auth.setAuthority(Authority.ADMIN);
		Assert.isTrue(principal.getAuthorities().contains(auth));
		Double result;
		result = this.administratorRepository.getC7();
		return result;

	}
	//The ratio of pending applications that cannot change its status because their time period's elapsed.

	public Double getC8() {

		UserAccount principal = LoginService.getPrincipal();
		Assert.notNull(principal);

		Authority auth = new Authority();
		auth.setAuthority(Authority.ADMIN);
		Assert.isTrue(principal.getAuthorities().contains(auth));
		Double result;
		result = this.administratorRepository.getC8();
		return result;

	}
	//The listing of customers who have published at least 10% more fix-up tasks than the average, ordered by number of applications.

	public Collection<Customer> getC9() {

		UserAccount principal = LoginService.getPrincipal();
		Assert.notNull(principal);

		Authority auth = new Authority();
		auth.setAuthority(Authority.ADMIN);
		Assert.isTrue(principal.getAuthorities().contains(auth));
		Collection<Customer> result;
		result = this.administratorRepository.getC9();
		return result;

	}
	//The listing of handy workers who have got accepted at least 10% more applications than the average, ordered by number of applications.

	public Collection<HandyWorker> getC10() {

		UserAccount principal = LoginService.getPrincipal();
		Assert.notNull(principal);

		Authority auth = new Authority();
		auth.setAuthority(Authority.ADMIN);
		Assert.isTrue(principal.getAuthorities().contains(auth));
		Collection<HandyWorker> result;
		result = this.administratorRepository.getC10();
		return result;

	}
	//The minimum, the maximum, the average, and the standard deviation of the number of complaints per fix-up task.

	public List<Double> getB1() {
		UserAccount principal = LoginService.getPrincipal();
		Assert.notNull(principal);

		Authority auth = new Authority();
		auth.setAuthority(Authority.ADMIN);
		Assert.isTrue(principal.getAuthorities().contains(auth));
		List<Double> result;
		result = this.administratorRepository.getB1();
		return result;

	}
	//The minimum, the maximum, the average, and the standard deviation of the number of notes per referee report.

	public List<Double> getB2() {

		UserAccount principal = LoginService.getPrincipal();
		Assert.notNull(principal);

		Authority auth = new Authority();
		auth.setAuthority(Authority.ADMIN);
		Assert.isTrue(principal.getAuthorities().contains(auth));
		List<Double> result;
		result = this.administratorRepository.getB2();
		return result;

	}
	//The ratio of fix-up tasks with a complaint.

	public Double getB3() {

		UserAccount principal = LoginService.getPrincipal();
		Assert.notNull(principal);

		Authority auth = new Authority();
		auth.setAuthority(Authority.ADMIN);
		Assert.isTrue(principal.getAuthorities().contains(auth));
		Double result;
		result = this.administratorRepository.getB3();
		return result;

	}
	//The top-three customers in terms of complaints.

	public Collection<Customer> getB4() {

		UserAccount principal = LoginService.getPrincipal();
		Assert.notNull(principal);

		Authority auth = new Authority();
		auth.setAuthority(Authority.ADMIN);
		Assert.isTrue(principal.getAuthorities().contains(auth));
		List<Customer> result = new ArrayList<>(this.administratorRepository.getB4());
		result.subList(0, 3);
		return result;

	}
	//The top-three handy workers in terms of complaints.
	public Collection<HandyWorker> getB5() {

		UserAccount principal = LoginService.getPrincipal();
		Assert.notNull(principal);

		Authority auth = new Authority();
		auth.setAuthority(Authority.ADMIN);
		Assert.isTrue(principal.getAuthorities().contains(auth));
		List<HandyWorker> result = new ArrayList<>(this.administratorRepository.getB5());
		result.subList(0, 3);
		return result;
	}

	public void flush() {
		this.administratorRepository.flush();
	}

}
