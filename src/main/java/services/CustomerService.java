package services;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import repositories.CustomerRepository;
import security.Authority;
import security.LoginService;
import security.UserAccount;
import domain.Customer;
import domain.FixUpTask;

@Service
@Transactional
public class CustomerService {

	// Managed repository -------------------
	@Autowired
	private CustomerRepository	customerRepository;

	//-------------------

	// Supporting services -------------------
	@Autowired
	private ActorService		actorService;


	//-------------------	

	// Constructors -----------------------------

	public CustomerService() {
		super();
	}
	//--------------

	public Collection<Customer> findAll() {
		Collection<Customer> result;

		result = this.customerRepository.findAll();

		return result;
	}

	public Customer findOne(final int customerId) {
		Assert.isTrue(customerId != 0);
		Customer result;
		result = this.customerRepository.findOne(customerId);
		Assert.notNull(result);
		return result;
	}

	public Customer create() {
		final Customer result = (Customer) this.actorService.create();
		final Collection<FixUpTask> fup = new ArrayList<>();

		final UserAccount ua = new UserAccount();
		final Authority auth = new Authority();
		auth.setAuthority(Authority.CUSTOMER);

		ua.addAuthority(auth);

		result.setUserAccount(ua);

		result.setFixUpTasks(fup);

		return result;

	}

	public Customer save(final Customer customer) {
		Assert.notNull(customer);
		Customer result;
		if (customer.getId() == 0) {

			Assert.isTrue(this.actorService.uniqueUsername(customer.getUserAccount().getUsername()));
			result = this.customerRepository.save(customer);
		} else {
			//An actor can NOT list,edit or delete data than belongs to another actor
			final Customer custom = (Customer) this.actorService.findByUserAccountId(LoginService.getPrincipal().getId());
			Assert.isTrue(custom.getId() == customer.getId());
			Assert.isTrue(custom.getUserAccount().getAuthorities() == customer.getUserAccount().getAuthorities());

			Assert.isTrue(customer.getName() != null || customer.getName() != "");
			Assert.isTrue(customer.getSurname() != null || customer.getSurname() != "");
			Assert.isTrue(customer.getEmail() != null || customer.getEmail() != "");
			Assert.isTrue(customer.isSuspicious() == true || customer.isSuspicious() == false);
			Assert.isTrue(customer.isBanned() == true || customer.isBanned() == false);

			result = this.customerRepository.save(customer);
		}

		return result;
	}

	public void delete(final Customer customer) {
		//No puedo borrar customer que tengan fixuptask publicadas
		Assert.isTrue(customer.getFixUpTasks().size() == 0);

		Assert.notNull(customer);
		Assert.isTrue(this.customerRepository.exists(customer.getId()));
		this.customerRepository.delete(customer);
	}

}
