
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

		//Only handyworkers create sections
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

		//Only handyworkers save sections
		final UserAccount principal = LoginService.getPrincipal();
		Assert.notNull(principal);

		final Authority auth = new Authority();
		auth.setAuthority(Authority.HANDYWORKER);
		Assert.isTrue(principal.getAuthorities().contains(auth));

		Section result;
		if (section.getId() == 0)//new section
			result = this.sectionRepository.save(section);
		else {//editing section
				//Checking that the owner of the section is the one trying to edit it

			final Tutorial tut = this.tutorialService.findTutorialBySectionId(section.getId());
			final HandyWorker hw = (HandyWorker) this.actorService.findByUserAccountId(LoginService.getPrincipal().getId());

			Assert.isTrue(hw.getTutorials().contains(tut));
			Assert.isTrue(tut.getSections().contains(section));

			result = this.sectionRepository.save(section);
		}
		return result;
	}

	public void delete(final Section section) {
		Assert.notNull(section);

		///Only handyworkers delete sections
		final UserAccount principal = LoginService.getPrincipal();
		Assert.notNull(principal);

		final Authority auth = new Authority();
		auth.setAuthority(Authority.HANDYWORKER);
		Assert.isTrue(principal.getAuthorities().contains(auth));

		//Checking that the owner of the tutorial where the section is saved is the one trying to edit it
		final HandyWorker hw = (HandyWorker) this.actorService.findByUserAccountId(LoginService.getPrincipal().getId());
		Assert.isTrue(hw.equals(this.tutorialService.findTutorialBySectionId(section.getId()).getHandyWorker()));
		final Tutorial tu1 = this.tutorialService.findTutorialBySectionId(section.getId());
		Assert.isTrue(tu1.getSections().contains(section));

		//deleting the section of the tutorial
		Assert.isTrue(this.sectionRepository.exists(section.getId()));
		final Tutorial tu = this.tutorialService.findTutorialBySectionId(section.getId());

		Assert.isTrue(tu.getSections().size() >= 1);  // From 1 to *
		tu.getSections().remove(section);

		this.sectionRepository.delete(section);
		this.tutorialService.save(tu);
	}
	public void flush() {
		this.sectionRepository.flush();
	}

}

