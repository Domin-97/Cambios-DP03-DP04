
package services;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import repositories.SponsorshipRepository;
import security.Authority;
import security.LoginService;
import security.UserAccount;
import domain.Sponsor;
import domain.Sponsorship;

@Service
@Transactional
public class SponsorshipService {

	//Managed repository
	@Autowired
	private SponsorshipRepository	sponsorshipRepository;
	//Supporting services
	@Autowired
	private SponsorService			sponsorService;
	@Autowired
	private ActorService			actorService;


	//constructor
	public SponsorshipService() {
		super();
	}

	//Simple CRUD methods

	public Sponsorship create() {
		final Sponsorship result = new Sponsorship();
		//Checking that is a sponsor

		final UserAccount principal = LoginService.getPrincipal();
		Assert.notNull(principal);
		Assert.notNull(principal);
		final Authority auth = new Authority();
		auth.setAuthority(Authority.SPONSOR);
		Assert.isTrue(principal.getAuthorities().contains(auth));

		result.setBannerUrl("");
		result.setTargetPage("");
		result.setCreditCard(null);

		return result;
	}
	public Collection<Sponsorship> findAll() {
		Collection<Sponsorship> result;
		result = this.sponsorshipRepository.findAll();
		Assert.notNull(result);
		return result;

	}

	public Sponsorship findOne(final int sponsorshipId) {
		Assert.isTrue(sponsorshipId != 0);
		Sponsorship result;
		result = this.sponsorshipRepository.findOne(sponsorshipId);
		Assert.notNull(result);
		return result;
	}

	public Sponsorship save(final Sponsorship sponsorship) {
		Assert.notNull(sponsorship);
		//Checking that is a sponsor
		final UserAccount principal = LoginService.getPrincipal();
		Assert.notNull(principal);
		Assert.notNull(principal);
		final Authority auth = new Authority();
		auth.setAuthority(Authority.SPONSOR);
		Assert.isTrue(principal.getAuthorities().contains(auth));

		Sponsorship result;
		//We check if is new we  save it 

		if (sponsorship.getId() == 0)
			result = this.sponsorshipRepository.save(sponsorship);
		else {
			//if is an updated one  check that belongs to the logged sponsor
			//we check if is the corresponding sponsor
			final Sponsor spo = (Sponsor) this.actorService.findByUserAccountId(LoginService.getPrincipal().getId());
			Assert.isTrue(spo.getSponsorships().contains(sponsorship));
			result = this.sponsorshipRepository.save(sponsorship);
		}
		return result;

	}

	public void flush() {
		this.sponsorshipRepository.flush();
	}
}
