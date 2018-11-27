
package services;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import repositories.SectionRepository;
import security.Authority;
import security.LoginService;
import security.UserAccount;
import domain.HandyWorker;
import domain.Section;
import domain.Tutorial;

@Service
@Transactional
public class SectionService {

	// Managed repository -------------------

	@Autowired
	private SectionRepository	sectionRepository;

	//-------------------		

	// Supporting services -------------------
	@Autowired
	private ActorService		actorService;
	@Autowired
	private TutorialService		tutorialService;


	//-------------------	

	// Constructors -----------------------------

	public SectionService() {
		super();
	}
	//--------------

	public Collection<Section> findAll() {
		Collection<Section> result;

		result = this.sectionRepository.findAll();

		return result;
	}

	public Section findOne(final int sectionId) {
		Assert.isTrue(sectionId != 0);
		Section result;
		result = this.sectionRepository.findOne(sectionId);
		//Assert.notNull(result);
		return result;
	}

	public Section create() {
		Section result;

		//Solo HandyWorker crea sections
		final UserAccount principal = LoginService.getPrincipal();
		Assert.notNull(principal);

		final Authority auth = new Authority();
		auth.setAuthority(Authority.HANDYWORKER);
		Assert.isTrue(principal.getAuthorities().contains(auth));

		final Collection<String> pic = new ArrayList<>();

		result = new Section();
		result.setNumber(0);
		result.setTitle("");
		result.setText("");
		result.setPictures(pic);

		return result;

	}
	public Section save(final Section section) {
		Assert.notNull(section);

		//Solo HandyWorker crea sections (Suponemos que edit tb)
		final UserAccount principal = LoginService.getPrincipal();
		Assert.notNull(principal);

		final Authority auth = new Authority();
		auth.setAuthority(Authority.HANDYWORKER);
		Assert.isTrue(principal.getAuthorities().contains(auth));

		Section result;
		if (section.getId() == 0)
			result = this.sectionRepository.save(section);
		else {
			final Tutorial tu = this.tutorialService.findTutorialBySectionId(section.getId());
			tu.getSections().add(section);
			result = this.sectionRepository.save(section);
		}
		return result;
	}
	//HandyWorker elimina sections
	public void delete(final Section section) {
		Assert.notNull(section);

		//Solo HandyWorker crea sections (Suponemos que edit tb)
		final UserAccount principal = LoginService.getPrincipal();
		Assert.notNull(principal);

		final Authority auth = new Authority();
		auth.setAuthority(Authority.HANDYWORKER);
		Assert.isTrue(principal.getAuthorities().contains(auth));

		//Comprobar que el hw que borra la section es propietaria de del tutorial al que pertenece la section
		final HandyWorker hw = (HandyWorker) this.actorService.findByUserAccountId(LoginService.getPrincipal().getId());
		Assert.isTrue(hw.equals(this.tutorialService.findTutorialBySectionId(section.getId()).getHandyWorker()));

		//Borro la section del tutorial
		Assert.isTrue(this.sectionRepository.exists(section.getId()));
		final Tutorial tu = this.tutorialService.findTutorialBySectionId(section.getId());

		Assert.isTrue(tu.getSections().size() >= 1);  // De 1 a *
		tu.getSections().remove(section);

		this.sectionRepository.delete(section);
		this.tutorialService.save(tu);
	}
	public void flush() {
		this.sectionRepository.flush();
	}

}
