
package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import repositories.RefereeRepository;
import security.Authority;
import security.LoginService;
import security.UserAccount;
import domain.Finder;
import domain.HandyWorker;
import domain.MessageBox;
import domain.Referee;
import domain.Report;
import domain.SocialProfile;

@Service
@Transactional
public class RefereeService {

	// Manage repository----------------------------------------------------------------------------
	@Autowired
	private ActorService			actorService;
	@Autowired
	private RefereeRepository	refereeRepository;
	@Autowired
	private MessageBoxService	messageBoxService;
	


	//Suporting services -------------------------------------------------------------------------------

	//constructors --------------------------------------------------------------------------------
	public RefereeService() {
		super();
	}
	//Simple crud method------------------------------------------------------------------------------
	public Collection<Referee> findAll() {
		Collection<Referee> result;
		result = this.refereeRepository.findAll();
		return result;
	}

	public Referee findOne( int refereeId) {
		Assert.isTrue(refereeId != 0);
		Referee result;
		result = this.refereeRepository.findOne(refereeId);
		return result;
	}
	public Referee create() {
		Referee result =new Referee();
		 UserAccount principal = LoginService.getPrincipal();
		Authority auth = new Authority();
		auth.setAuthority(Authority.ADMIN);
		Assert.isTrue((principal.getAuthorities().contains(auth)));
		
		
		
		
		
		 Collection<Report> reportsWritten = new ArrayList<>();

		 UserAccount ua = new UserAccount();
		 Authority auth1 = new Authority();
		auth1.setAuthority(Authority.REFEREE);

		ua.addAuthority(auth);
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
				result.setSocialProfiles(new ArrayList<SocialProfile>());
				List<MessageBox> systemBoxes = new ArrayList<MessageBox>(this.messageBoxService.createSystemMessageBoxes());
				result.setMessageBoxes(systemBoxes);

		result.setUserAccount(ua);
		result.setReportsWritten(reportsWritten);

		return result;
	}
	public Referee save( Referee referee) {
		Assert.notNull(referee);
		Referee result;

		if (referee.getId() == 0) {
			 UserAccount principal = LoginService.getPrincipal();
				Authority auth = new Authority();
				auth.setAuthority(Authority.ADMIN);
				Assert.isTrue((principal.getAuthorities().contains(auth)));
			
//				Collection<Report> reportsWritten = new ArrayList<Report>();
//			result.setReportsWritten(reportsWritten);
			result = this.refereeRepository.save(referee);
		} else {
		

			UserAccount principal = LoginService.getPrincipal();
			Authority auth = new Authority();
			auth.setAuthority(Authority.ADMIN);
			Assert.isTrue((principal.getAuthorities().contains(auth)));
			Referee old = this.refereeRepository.findOne(referee.getId());
			Assert.notNull(old);

			

				Assert.isTrue(referee.getName() == old.getName());
				Assert.isTrue(referee.getMiddleName() == old.getMiddleName());
				Assert.isTrue(referee.getSurname() == old.getSurname());
				Assert.isTrue(referee.getPhoto() == old.getPhoto());
				Assert.isTrue(referee.getEmail() == old.getEmail());
				Assert.isTrue(referee.getPhoneNumber() == old.getPhoneNumber());
				Assert.isTrue(referee.getAddress() == old.getAddress());
				Assert.isTrue(referee.getSocialProfiles() == old.getSocialProfiles());
				Assert.isTrue(referee.getMessageBoxes() == old.getMessageBoxes());
				Assert.isTrue(referee.getName() == old.getName());
				Collection<Report> reportsWritten = new ArrayList<>();
				old.setReportsWritten(reportsWritten);
			}

			result = this.refereeRepository.save(referee);
		
		return result;
	}
	
	public void flush() {
		this.refereeRepository.flush();
	}
}
