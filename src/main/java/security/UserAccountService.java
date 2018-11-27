package  security;
import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import services.ActorService;

import domain.Actor;

@Service
@Transactional
public class UserAccountService {

	// Managed repository -----------------------------------------------------

	@Autowired
	private UserAccountRepository	userAccountRepository;
	
	// Supporting services ----------------------------------------------------

	@Autowired
	private ActorService actorService;
	
	// Constructors -----------------------------------------------------------

	public UserAccountService() {
		super();
	}

	// Simple CRUD methods ----------------------------------------------------
	public UserAccount create(){
		UserAccount result = new UserAccount();
		result.setAuthorities(new ArrayList<Authority>());
		result.setPassword("");
		result.setUsername("");
		return result;
	}
	
	public UserAccount findOne(int userAccountId){
		Assert.isTrue(userAccountId!=0);
		Assert.isTrue(this.actorService.findByUserAccountId(userAccountId)!=null);
		
		UserAccount result;
		
		result = this.userAccountRepository.findOne(userAccountId);
		
		
		return result;
	}
	public Collection<UserAccount> findAll(){
		Collection<UserAccount> result;
		result = this.userAccountRepository.findAll();
		return result;
	}
	public UserAccount save(UserAccount userAccount){
		Assert.notNull(userAccount);
		Assert.isTrue(userAccount.getId()!=0);
		UserAccount ua = LoginService.getPrincipal();
		
		Assert.isTrue(userAccount.equals(ua));
		
		return this.userAccountRepository.save(userAccount);
		
	}
	
	public void delete(UserAccount userAccount){
		Assert.isTrue(userAccountRepository.exists(userAccount.getId()));
		UserAccount ua = LoginService.getPrincipal();
		Assert.isTrue(userAccount.equals(ua));
		
		this.userAccountRepository.delete(ua);
		
	}

	// Other business methods -------------------------------------------------
	public UserAccount findByUsername(String username){
		Assert.isTrue(username!="");
		Assert.notNull(username);
		Assert.isTrue(this.actorService.uniqueUsername(username)==false);
		
		return this.userAccountRepository.findByUsername(username);
	}
	
	public UserAccount findByActor(final Actor actor) {
		Assert.notNull(actor);

		UserAccount result;
		result = this.userAccountRepository.findByActorId(actor.getId());

		return result;
	}

	
	public Collection<String> findAllUsernames() {
		return this.userAccountRepository.findAllUsernames();
	}
}
