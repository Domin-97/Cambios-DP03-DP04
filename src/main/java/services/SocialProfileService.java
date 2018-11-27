
package services;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import domain.Actor;
import domain.Administrator;
import domain.Customer;
import domain.HandyWorker;
import domain.Referee;
import domain.SocialProfile;
import domain.Sponsor;

import repositories.SocialProfileRepository;
import security.Authority;
import security.LoginService;
import security.UserAccount;

@Service
@Transactional
public class SocialProfileService {

	// Managed repository -----------------------------------------------------
	@Autowired
	private SocialProfileRepository	socialProfileRepository;

	// Supporting service --------------------------------------------------
	@Autowired
	private ActorService			actorService;
	@Autowired
	private AdministratorService administratorService; 
	@Autowired
	private CustomerService customerService; 
	@Autowired
	private HandyWorkerService handyWorkerService; 
	@Autowired
	private RefereeService refereeService; 
	@Autowired
	private SponsorService sponsorService; 
 

	// Constructors -----------------------------------------------------------

	public SocialProfileService() {
		super();
	}

	//Simple CRUD Methods------------------------------------------------------

	public SocialProfile create() {
		Actor a = this.actorService.findByUserAccountId(LoginService.getPrincipal().getId());
		Assert.isTrue(a.getId()!=0);
		SocialProfile result = new SocialProfile();
		result.setLink("");
		result.setNick("");
		result.setSocialNetworkName("");
		return result;
	}

	public Collection<SocialProfile> findOwnSocialProfiles() {
		UserAccount ua = LoginService.getPrincipal();
		Actor a = this.actorService.findByUserAccountId(ua.getId());
		return this.socialProfileRepository.findAllByActorId(a.getId());
	}

	public SocialProfile findOne(int socialProfileId) {
		Assert.isTrue(socialProfileId != 0);
		SocialProfile result;

		result = this.socialProfileRepository.findOne(socialProfileId);
		Assert.notNull(result);

		return result;
	}
	public Collection<SocialProfile> findAll() {
		Collection<SocialProfile> result;

		result = this.socialProfileRepository.findAll();

		return result;
	}

	public Collection<SocialProfile> save(SocialProfile socialProfile) {
		
		Assert.notNull(socialProfile);
		Collection<SocialProfile> result;
		Actor a = this.actorService.findByUserAccountId(LoginService.getPrincipal().getId());
		if (socialProfile.getId() == 0) {
			result = this.socialProfileRepository.findAllByActorId(a.getId());
			result.add(socialProfile);
			this.socialProfileRepository.save(result);
		} else {
			//SocialProfile sp = this.socialProfileRepository.findOne(socialProfile.getId());
			Assert.isTrue(socialProfile.getNick() != null || socialProfile.getNick() == "");
			Assert.isTrue(socialProfile.getLink() != null || socialProfile.getLink() == "");
			Assert.isTrue(socialProfile.getSocialNetworkName() != null || socialProfile.getSocialNetworkName() == "");
			this.socialProfileRepository.save(socialProfile);
			//ARREGLAR
			result=this.socialProfileRepository.findAllByActorId(a.getId());
			result.add(socialProfile);
		}

		//NECESITO SABER QUE TIPO DE ACTOR ES PARA GUARDAR ESE ACTOR CON SU NUEVO COLLECTION de SOCIALPROFILES
		Authority adAuth = new Authority();
		adAuth.setAuthority(Authority.ADMIN);
		Authority cusAuth = new Authority();
		cusAuth.setAuthority(Authority.CUSTOMER);
		Authority hwAuth = new Authority();
		hwAuth.setAuthority(Authority.HANDYWORKER);
		Authority refAuth = new Authority();
		refAuth.setAuthority(Authority.REFEREE);
		Authority spoAuth = new Authority();
		spoAuth.setAuthority(Authority.SPONSOR);
		//COMO TRATAR AL SUPERUSER Y AL RESTO DE ACTORES
		if(LoginService.getPrincipal().getAuthorities().contains(adAuth)){
			Administrator ad = (Administrator) this.actorService.findByUserAccountId(a.getId());
			ad.setSocialProfiles(result);
			this.administratorService.save(ad);
		}else if(LoginService.getPrincipal().getAuthorities().contains(cusAuth)){
			Customer cus = (Customer) this.actorService.findByUserAccountId(a.getId());
			cus.setSocialProfiles(result);
			this.customerService.save(cus);
		}else if(LoginService.getPrincipal().getAuthorities().contains(hwAuth)){
			HandyWorker hw = (HandyWorker) this.actorService.findByUserAccountId(a.getId());
			hw.setSocialProfiles(result);
			this.handyWorkerService.save(hw);
		}else if(LoginService.getPrincipal().getAuthorities().contains(refAuth)){
			Referee ref = (Referee) this.actorService.findByUserAccountId(a.getId());
			ref.setSocialProfiles(result);	
			this.refereeService.save(ref);
		}else if(LoginService.getPrincipal().getAuthorities().contains(spoAuth)){
			Sponsor spo = (Sponsor) this.actorService.findByUserAccountId(a.getId());
			spo.setSocialProfiles(result);
			this.sponsorService.save(spo);
		}

		return result;
	}
	public void delete(SocialProfile sp) {
		Assert.notNull(sp);
		Assert.isTrue(sp.getId() != 0);
		Actor a = this.actorService.findByUserAccountId(LoginService.getPrincipal().getId());
		Collection<SocialProfile> spowned = this.findOwnSocialProfiles();
		Assert.isTrue(spowned.contains(sp));
		spowned.remove(sp);
		a.setSocialProfiles(spowned);
		
		//NECESITO SABER QUE TIPO DE ACTOR ES PARA GUARDAR ESE ACTOR CON SU NUEVO COLLECTION de SOCIALPROFILES
		Authority adAuth = new Authority();
		adAuth.setAuthority(Authority.ADMIN);
		Authority cusAuth = new Authority();
		cusAuth.setAuthority(Authority.CUSTOMER);
		Authority hwAuth = new Authority();
		hwAuth.setAuthority(Authority.HANDYWORKER);
		Authority refAuth = new Authority();
		refAuth.setAuthority(Authority.REFEREE);
		Authority spoAuth = new Authority();
		spoAuth.setAuthority(Authority.SPONSOR);
		//COMO TRATAR AL SUPERUSER Y AL RESTO DE ACTORES
		if(LoginService.getPrincipal().getAuthorities().contains(adAuth)){
			Administrator ad = (Administrator) this.actorService.findByUserAccountId(a.getId());
			ad.setSocialProfiles(spowned);
			this.administratorService.save(ad);
		}else if(LoginService.getPrincipal().getAuthorities().contains(cusAuth)){
			Customer cus = (Customer) this.actorService.findByUserAccountId(a.getId());
			cus.setSocialProfiles(spowned);
			this.customerService.save(cus);
		}else if(LoginService.getPrincipal().getAuthorities().contains(hwAuth)){
			HandyWorker hw = (HandyWorker) this.actorService.findByUserAccountId(a.getId());
			hw.setSocialProfiles(spowned);
			this.handyWorkerService.save(hw);
		}else if(LoginService.getPrincipal().getAuthorities().contains(refAuth)){
			Referee ref = (Referee) this.actorService.findByUserAccountId(a.getId());
			ref.setSocialProfiles(spowned);	
			this.refereeService.save(ref);
		}else if(LoginService.getPrincipal().getAuthorities().contains(spoAuth)){
			Sponsor spo = (Sponsor) this.actorService.findByUserAccountId(a.getId());
			spo.setSocialProfiles(spowned);
			this.sponsorService.save(spo);
		}

		//Other business Methods---------------------------------------------------

	}

}
