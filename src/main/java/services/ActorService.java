package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import domain.Actor;
import domain.MessageBox;
import domain.SocialProfile;

import repositories.ActorRepository;
import security.LoginService;
import security.UserAccount;
import security.UserAccountService;

@Service
@Transactional
public class ActorService {
	
	// Managed repository -----------------------------------------------------

	@Autowired
	private ActorRepository actorRepository;
	
	// Supporting services ----------------------------------------------------

	@Autowired
	private UserAccountService userAccountService;
	@Autowired
	private MessageBoxService messageBoxService;
	// Constructors -----------------------------------------------------------

	public ActorService() {
		super();
	}
	
	//Simple CRUD Methods------------------------------------------------------
	public Actor create(){
		Actor a = new Actor();
		a.setName("");
		a.setMiddleName("");
		a.setSurname("");
		a.setPhoto("");
		a.setEmail("");
		a.setPhoneNumber("");
		a.setAddress("");
		a.setBanned(false);
		a.setSuspicious(false);
		a.setSocialProfiles(new ArrayList<SocialProfile>());
		List<MessageBox> systemBoxes = new ArrayList<MessageBox>(this.messageBoxService.createSystemMessageBoxes());
		a.setMessageBoxes(systemBoxes);
		return a;
	}
	public Collection<Actor> findAll(){
		Collection<Actor> result;
		
		result=actorRepository.findAll();
		
		return result;
	}
	
	public Actor findOne(int actorId){
		Assert.isTrue(actorId!=0);
		Actor result;
		
		result=actorRepository.findOne(actorId);
		Assert.notNull(result);
		
		return result;
	}
	
	public Actor save(Actor actor) {
		Assert.notNull(actor);

		Actor result;

		result = actorRepository.save(actor);

		return result;
	}

	public void delete(Actor actor) {
		Assert.notNull(actor);
		UserAccount principal = LoginService.getPrincipal();
		Actor a = this.actorRepository.findActorByUserAccountId(principal.getId()); 
		Assert.isTrue(a.getId()!=actor.getId());
		Assert.isTrue(actor.getId() != 0);
		Assert.isTrue(actorRepository.exists(actor.getId()));

		actorRepository.delete(actor);
	}
	public void activateSuspicious(Actor actor){
		UserAccount principal = LoginService.getPrincipal();
		Actor a = this.findByUserAccountId(principal.getId());
		Assert.isTrue(actor.getId()==a.getId());
		a.setSuspicious(true);
		this.actorRepository.save(a);
	}
	
	//Other business Methods---------------------------------------------------
	
	public Actor findByUserAccountId(int userAccountId){		
		return this.actorRepository.findActorByUserAccountId(userAccountId);
		
	}
}
