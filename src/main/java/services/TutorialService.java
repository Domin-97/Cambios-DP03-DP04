
package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import repositories.TutorialRepository;
import security.Authority;
import security.LoginService;
import security.UserAccount;
import domain.HandyWorker;
import domain.Section;
import domain.Sponsorship;
import domain.Tutorial;

@Service
@Transactional
public class TutorialService {

	// Managed repository -------------------

	@Autowired
	private TutorialRepository	tutorialRepository;

	//-------------------		

	// Supporting services -------------------

	@Autowired
	private ActorService		actorService;

	@Autowired
	private HandyWorkerService	handyWorkerService;


	//-------------------	

	// Constructors -----------------------------

	public TutorialService() {
		super();
	}
	//--------------

	public Collection<Tutorial> findAll() {
		Collection<Tutorial> result;

		result = this.tutorialRepository.findAll();

		return result;
	}

	public Tutorial findOne(final int tutorialId) {
		Assert.isTrue(tutorialId != 0);
		Tutorial result;
		result = this.tutorialRepository.findOne(tutorialId);
		Assert.notNull(result);
		return result;
	}

	public Tutorial create() {
		Tutorial result;

		//Solo HandyWorker crea tutorials (Suponemos que edit tb)
		final UserAccount principal = LoginService.getPrincipal();
		Assert.notNull(principal);

		final Authority auth = new Authority();
		auth.setAuthority(Authority.HANDYWORKER);
		Assert.isTrue(principal.getAuthorities().contains(auth));

		final Collection<Sponsorship> sp = new ArrayList<>();
		final List<String> pic = new ArrayList<String>();
		final Collection<Section> sec = new ArrayList<>();
		final Date da = new Date();

		//
		final HandyWorker handyWorker = (HandyWorker) this.actorService.findByUserAccountId(LoginService.getPrincipal().getId());

		result = new Tutorial();
		result.setTitle("");
		result.setLastUpdated(da);
		result.setSummary("");
		result.setPictures(pic);
		result.setSections(sec);
		result.setHandyWorker(handyWorker);
		result.setSponsorShips(sp);

		return result;

	}
	public Tutorial save(final Tutorial tutorial) {
		Assert.notNull(tutorial);

		//Solo HandyWorker crea tutorials (Suponemos que edit tb)
		final UserAccount principal = LoginService.getPrincipal();
		Assert.notNull(principal);

		final Authority auth = new Authority();
		auth.setAuthority(Authority.HANDYWORKER);
		Assert.isTrue(principal.getAuthorities().contains(auth));

		Tutorial result;
		if (tutorial.getId() == 0)
			result = this.tutorialRepository.save(tutorial);
		else {
			final HandyWorker hw = (HandyWorker) this.actorService.findByUserAccountId(LoginService.getPrincipal().getId());
			Assert.isTrue(hw.getTutorials().contains(tutorial));//Comprobar que es el propietario del tutorial
			hw.getTutorials().add(tutorial);

			result = this.tutorialRepository.save(tutorial);
		}

		return result;
	}

	//Handyworker elimina tutorials
	public void delete(final Tutorial tutorial) {
		//Solo HandyWorker deletea tutorials
		final UserAccount principal = LoginService.getPrincipal();
		Assert.notNull(principal);

		final Authority auth = new Authority();
		auth.setAuthority(Authority.HANDYWORKER);
		Assert.isTrue(principal.getAuthorities().contains(auth));

		Assert.notNull(tutorial);
		final HandyWorker hw = (HandyWorker) this.actorService.findByUserAccountId(LoginService.getPrincipal().getId());

		Assert.isTrue(hw.getTutorials().contains(tutorial));//Comprobar que es el propietario del tutorial
		Assert.isTrue(this.tutorialRepository.exists(tutorial.getId()));
		hw.getTutorials().remove(tutorial);
		this.tutorialRepository.delete(tutorial);
		this.handyWorkerService.save(hw);
	}

	public Collection<Tutorial> getHandyWorkerTutorials(final int handyWorkerId) {
		Collection<Tutorial> result;
		result = this.tutorialRepository.getHandyWorkerTutorials(handyWorkerId);
		return result;
	}

	public Tutorial findTutorialBySectionId(final int sectionId) {
		Tutorial result;
		result = this.tutorialRepository.findTutorialBySectionId(sectionId);
		return result;
	}

}
