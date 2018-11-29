
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

import repositories.SponsorRepository;
import security.Authority;
import security.LoginService;
import security.UserAccount;
import domain.MessageBox;
import domain.SocialProfile;
import domain.Sponsor;
import domain.Sponsorship;

@Service
@Transactional
public class SponsorService {

	//Managed repository
	@Autowired
	private SponsorRepository	sponsorRepository;
	@Autowired
	private ActorService		actorService;
	@Autowired
	private SponsorshipService	sponsorshipService;
	@Autowired
	private MessageBoxService	messageBoxService;


	//Constructor
	public SponsorService() {
		super();
	}
	//Simple CRUD METHODS 

	public Sponsor create() {
		//Checking that is not logged
		Authentication authentication;
		SecurityContext context;
		context = SecurityContextHolder.getContext();
		Assert.notNull(context);
		authentication = context.getAuthentication();
		Assert.isNull(authentication);

		final Sponsor result = new Sponsor();
		//common properties
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
		final List<MessageBox> systemBoxes = new ArrayList<MessageBox>(this.messageBoxService.createSystemMessageBoxes());
		result.setMessageBoxes(systemBoxes);
		//Account creating
		final UserAccount ua = new UserAccount();
		final Authority auth = new Authority();
		auth.setAuthority(Authority.SPONSOR);
		ua.addAuthority(auth);
		result.setUserAccount(ua);
		//Sponsor property
		result.setSponsorships(new ArrayList<Sponsorship>());

		return result;

	}
	public Collection<Sponsor> findAll() {
		Collection<Sponsor> result;
		result = this.sponsorRepository.findAll();
		Assert.notNull(result);
		return result;
	}

	public Sponsor findOne(final int sponsorId) {
		Assert.isTrue(sponsorId != 0);
		Sponsor result;
		result = this.sponsorRepository.findOne(sponsorId);
		Assert.notNull(result);
		return result;
	}
	public Sponsor save(final Sponsor sponsor) {
		Assert.notNull(sponsor);
		Sponsor result;
		if (sponsor.getId() == 0) {
			//Can`t be logged
			Authentication authentication;
			SecurityContext context;
			context = SecurityContextHolder.getContext();
			Assert.notNull(context);
			authentication = context.getAuthentication();
			Assert.isNull(authentication);

			result = this.sponsorRepository.save(sponsor);
		} else {
			//Only the user and the Admin can update
			final UserAccount principal = LoginService.getPrincipal();
			Assert.notNull(principal);

			final Authority auth = new Authority();
			auth.setAuthority(Authority.SPONSOR);
			final Authority auth2 = new Authority();
			auth2.setAuthority(Authority.ADMIN);
			Assert.isTrue(principal.getAuthorities().contains(auth) || principal.getAuthorities().contains(auth2));
			//Check that it exist
			final Sponsor old = this.sponsorRepository.findOne(sponsor.getId());
			Assert.notNull(old);

			//if the sponsor modifies his information
			if (principal.getAuthorities().contains(auth)) {
				Assert.isTrue(this.actorService.findByUserAccountId(principal.getId()).getId() == (sponsor.getId()));

				//Sponsor can´t update the banned and suspicious properties
				//Can´t add privileges
				Assert.isTrue(sponsor.isSuspicious() == old.isSuspicious());
				Assert.isTrue(sponsor.isBanned() == (old.isBanned()));
				Assert.isTrue(sponsor.getUserAccount().getAuthorities() == old.getUserAccount().getAuthorities());
			} else {
				//If is and Admin can update the boolean properties and more.

				Assert.isTrue(sponsor.getName() == old.getName());
				Assert.isTrue(sponsor.getMiddleName() == old.getMiddleName());
				Assert.isTrue(sponsor.getSurname() == old.getSurname());
				Assert.isTrue(sponsor.getPhoto() == old.getPhoto());
				Assert.isTrue(sponsor.getEmail() == old.getEmail());
				Assert.isTrue(sponsor.getPhoneNumber() == old.getPhoneNumber());
				Assert.isTrue(sponsor.getAddress() == old.getAddress());
				Assert.isTrue(sponsor.getSocialProfiles() == old.getSocialProfiles());
				Assert.isTrue(sponsor.getMessageBoxes() == old.getMessageBoxes());
				Assert.isTrue(sponsor.getName() == old.getName());

				Assert.isTrue(sponsor.getUserAccount().getAuthorities() == old.getUserAccount().getAuthorities());
				Assert.isTrue(sponsor.getSponsorships() == old.getSponsorships());
			}
			result = this.sponsorRepository.saveAndFlush(sponsor);

		}
		return result;
	}

	public void flush() {
		this.sponsorRepository.flush();
	}
}

