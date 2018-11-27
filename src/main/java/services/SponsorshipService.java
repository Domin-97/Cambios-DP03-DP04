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

	//CUANDO CREAMOS UN SPONSORSHIP SE LE AÑADE A SU SPONSOR EN SU LISTA
	//ENCONTRAR LOS SPONSOR POR CADA SPONSOR 
	//repositorio
	@Autowired
	private SponsorshipRepository	sponsorshipRepository;
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
		final UserAccount principal = LoginService.getPrincipal();

		//Solo un sponsor puede crear los sponsorships
		Assert.isTrue(principal.getAuthorities().contains(Authority.SPONSOR));
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
		//Solo sponsor crea sponsorship
		final UserAccount principal = LoginService.getPrincipal();
		Assert.isTrue(principal.getAuthorities().contains(Authority.SPONSOR));

		Sponsorship result;
		if (sponsorship.getId() == 0)
			result = this.sponsorshipRepository.save(sponsorship);
		else {
			//TODO:SI EN CASO DE QUE NO SEA CERO NO TE DEJARA CAMBIAR 
			final Sponsor spo = (Sponsor) this.actorService.findByUserAccountId(LoginService.getPrincipal().getId());
			spo.getSponsorships().add(sponsorship);
			this.sponsorService.save(spo);
			result = this.sponsorshipRepository.save(sponsorship);
		}
		return result;

	}

	//SEGUN MULLER NO SE PUEDEN BORRAR 
	//public void delete()
	public void delete() {

	}
}
