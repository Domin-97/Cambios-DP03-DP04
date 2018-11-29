

package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import repositories.CustomerRepository;
import security.Authority;
import security.LoginService;
import security.UserAccount;
import domain.Customer;
import domain.Endorsement;
import domain.MessageBox;
import domain.SocialProfile;

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

	@Autowired
	private MessageBoxService	messageBoxService;


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
		//You cant be logged to create a new actor
		Authentication authentication;
		SecurityContext context;
		context = SecurityContextHolder.getContext();
		Assert.notNull(context);
		authentication = context.getAuthentication();
		Assert.isNull(authentication);

		final Customer result = new Customer();

		final Collection<SocialProfile> soc = new ArrayList<SocialProfile>();
		//Common Actor properties
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
		final List<MessageBox> systemBoxes = new ArrayList<MessageBox>(this.messageBoxService.createSystemMessageBoxes());
		result.setMessageBoxes(systemBoxes);
		//Create the new account
		final UserAccount ua = new UserAccount();
		final Authority auth = new Authority();
		auth.setAuthority(Authority.CUSTOMER);
		ua.addAuthority(auth);
		result.setUserAccount(ua);

		result.setEndorsementsByMe(new ArrayList<Endorsement>());
		result.setEndorsementsByOther(new ArrayList<Endorsement>());
		result.setScore(0.);

		return result;
	}
	public Customer save(final Customer customer) {
		Assert.notNull(customer);
		Customer result;

		if (customer.getId() == 0) {//New customer

			//You cant be logged to create a new actor
			Authentication authentication;
			SecurityContext context;
			context = SecurityContextHolder.getContext();
			Assert.notNull(context);
			authentication = context.getAuthentication();
			Assert.isNull(authentication);

			result = this.customerRepository.save(customer);
		} else {//editing  customer
			//Only admins and owner of the actor can edit it
			final UserAccount principal = LoginService.getPrincipal();
			Assert.notNull(principal);

			final Authority auth = new Authority();
			auth.setAuthority(Authority.CUSTOMER);
			final Authority auth2 = new Authority();
			auth2.setAuthority(Authority.ADMIN);
			Assert.isTrue(principal.getAuthorities().contains(auth) || principal.getAuthorities().contains(auth2));

			//Checking if the customer exists
			final Customer old = this.customerRepository.findOne(customer.getId());
			Assert.notNull(old);

			//Customer is the one trying to edit
			if (principal.getAuthorities().contains(auth)) {
				Assert.isTrue(this.actorService.findByUserAccountId(principal.getId()).getId() == customer.getId());

				//Customer cannot modify isSuspicious and isBanned fields
				//Only admin can update the privileges
				Assert.isTrue(customer.isSuspicious() == old.isSuspicious());
				Assert.isTrue(customer.isBanned() == old.isBanned());
				Assert.isTrue(customer.getUserAccount().getAuthorities() == old.getUserAccount().getAuthorities());
			} else {
				//Admin is the one trying to edit
				//He can only changes name, password and boolean atributes

				Assert.isTrue(customer.getName() == old.getName());
				Assert.isTrue(customer.getMiddleName() == old.getMiddleName());
				Assert.isTrue(customer.getSurname() == old.getSurname());
				Assert.isTrue(customer.getPhoto() == old.getPhoto());
				Assert.isTrue(customer.getEmail() == old.getEmail());
				Assert.isTrue(customer.getPhoneNumber() == old.getPhoneNumber());
				Assert.isTrue(customer.getAddress() == old.getAddress());
				Assert.isTrue(customer.getSocialProfiles() == old.getSocialProfiles());
				Assert.isTrue(customer.getMessageBoxes() == old.getMessageBoxes());
				Assert.isTrue(customer.getName() == old.getName());

				Assert.isTrue(customer.getUserAccount().getAuthorities() == old.getUserAccount().getAuthorities());

				Assert.isTrue(customer.getEndorsementsByMe() == old.getEndorsementsByMe());
				Assert.isTrue(customer.getEndorsementsByOther() == old.getEndorsementsByOther());
				Assert.isTrue(customer.getScore() == old.getScore());

			}

			result = this.customerRepository.save(customer);
		}
		return result;
	}

	public void delete(final Customer customer) {
		//Can`t delete a customer with fixuptask published
		Assert.isTrue(customer.getFixUpTasks().size() == 0);
		//Only owner can delete his actor
		final UserAccount principal = LoginService.getPrincipal();
		Assert.notNull(principal);
		//Check that is a customer
		final Authority auth = new Authority();
		auth.setAuthority(Authority.CUSTOMER);
		Assert.isTrue(principal.getAuthorities().contains(auth));

		final Customer cus = (Customer) this.actorService.findByUserAccountId(LoginService.getPrincipal().getId());
		Assert.isTrue(cus.equals(customer));
		Assert.notNull(customer);
		Assert.isTrue(this.customerRepository.exists(customer.getId()));
		this.customerRepository.delete(customer);
	}

	public void flush() {
		this.customerRepository.flush();
	}
	//OTHER METHODS
	public void updateCreatedEndorsements(final Endorsement endorsement, final Customer cus) {
		Assert.notNull(endorsement);
		Assert.notNull(cus);
		cus.getEndorsementsByMe().add(endorsement);
		this.customerRepository.save(cus);
	}

	public void updateObtainedEndorsements(final Endorsement endorsement, final Customer cus) {
		Assert.notNull(endorsement);
		Assert.notNull(cus);
		cus.getEndorsementsByOther().add(endorsement);
		this.customerRepository.save(cus);
	}
	public void deleteCreatedEndorsements(final Endorsement endorsement, final Customer cus) {
		Assert.notNull(endorsement);
		Assert.notNull(cus);
		cus.getEndorsementsByMe().remove(endorsement);
		this.customerRepository.save(cus);
	}

	public void deleteObtainedEndorsements(final Endorsement endorsement, final Customer cus) {
		Assert.notNull(endorsement);
		Assert.notNull(cus);
		cus.getEndorsementsByOther().remove(endorsement);
		this.customerRepository.save(cus);
	}
}
