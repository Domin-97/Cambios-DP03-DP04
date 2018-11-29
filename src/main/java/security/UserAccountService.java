package security;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import services.ActorService;

@Service
@Transactional
public class UserAccountService {

	// Managed repository -----------------------------------------------------

	@Autowired
	private UserAccountRepository	userAccountRepository;

	// Supporting services ----------------------------------------------------

	@Autowired
	private ActorService			actorService;


	// Constructors -----------------------------------------------------------

	public UserAccountService() {
		super();
	}

	// Simple CRUD methods ----------------------------------------------------
	public UserAccount findOne(int userAccountId) {
		Assert.isTrue(userAccountId != 0);
		Assert.isTrue(this.actorService.findByUserAccountId(userAccountId) != null);

		UserAccount result;

		result = this.userAccountRepository.findOne(userAccountId);

		return result;
	}
	public Collection<UserAccount> findAll() {
		Collection<UserAccount> result;
		result = this.userAccountRepository.findAll();
		return result;
	}

	// Other business methods -------------------------------------------------
	public UserAccount findByUsername(String username) {
		Assert.isTrue(username != "");
		Assert.notNull(username);
		//Assert.isTrue(this.actorService.uniqueUsername(username) == true);

		return this.userAccountRepository.findByUsername(username);
	}
	public Collection<String> findAllUsernames() {
		return this.userAccountRepository.findAllUsernames();
	}
}
