package services;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import repositories.SponsorRepository;
import security.Authority;
import security.LoginService;
import security.UserAccount;
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


	//Constructor
	public SponsorService() {
		super();
	}
	//Simple CRUD METHODS 

	public Sponsor create() {
		final Sponsor result = (Sponsor) this.actorService.create();

		final UserAccount useraccount = new UserAccount();
		final Authority auth = new Authority();
		auth.setAuthority("SPONSOR");

		useraccount.addAuthority(auth);
		result.setUserAccount(useraccount);

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

		final UserAccount principal = LoginService.getPrincipal();
		if (sponsor.getId() == 0)
			result = this.sponsorRepository.save(sponsor);
		else {
			final Sponsor old = this.sponsorRepository.findOne(sponsor.getId());
			old.setSponsorships(sponsor.getSponsorships());
			//seguir hasta que respondan
			result = this.sponsorRepository.save(old);
		}

		return result;

	}
	//TENER EN CUENTA QUE EN ESTE CASO NO SE BORRAN SUS SPONSORSHIPS
	public void delete(final Sponsor sponsor) {
		UserAccount useraccount;

		useraccount = LoginService.getPrincipal();
		Assert.isTrue(useraccount.equals(sponsor.getUserAccount()));

		this.sponsorRepository.delete(sponsor);

	}
}
