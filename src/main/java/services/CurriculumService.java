
package services;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import repositories.CurriculumRepository;
import security.Authority;
import security.LoginService;
import security.UserAccount;
import utilities.Tickers;
import domain.Curriculum;
import domain.EducationRecord;
import domain.EndorserRecord;
import domain.HandyWorker;
import domain.MiscellaneousRecord;
import domain.ProfessionalRecord;

@Service
@Transactional
public class CurriculumService {

	//Repository
	@Autowired
	private CurriculumRepository	curriculumRepository;

	//Services
	@Autowired
	private ActorService			actorService;
	@Autowired
	private HandyWorkerService		handyWorkerService;


	//Constructor
	public CurriculumService() {
		super();
	}
	//Simple CRUD methods
	public Curriculum create() {
		Curriculum result;
		//Checking that is a handy worker
		final UserAccount principal = LoginService.getPrincipal();
		Assert.notNull(principal);
		final Authority auth = new Authority();
		auth.setAuthority(Authority.HANDYWORKER);
		Assert.isTrue(principal.getAuthorities().contains(auth));
		//Create new curriculum
		result = new Curriculum();
		result.setEmail("");
		result.setlinkedInProfile("");
		result.setPhoneNumber("");
		result.setFullname("");
		result.setPhoto("");
		result.setProRecord(new ArrayList<ProfessionalRecord>());
		result.setEndRecord(new ArrayList<EndorserRecord>());
		result.setMisRecord(new ArrayList<MiscellaneousRecord>());
		result.setEduRecord(new ArrayList<EducationRecord>());

		return result;

	}
	public Collection<Curriculum> findAll() {
		Collection<Curriculum> result;
		result = this.curriculumRepository.findAll();
		Assert.notNull(result);
		return result;
	}

	public Curriculum findOne(final int curriculumId) {
		Assert.isTrue(curriculumId != 0);
		Curriculum result;
		result = this.curriculumRepository.findOne(curriculumId);
		Assert.notNull(result);
		return result;

	}

	public Curriculum save(final Curriculum curriculum) {

		Assert.notNull(curriculum);
		//Checking that is a handy worker
		final UserAccount principal = LoginService.getPrincipal();
		Assert.notNull(principal);
		final Authority auth = new Authority();
		auth.setAuthority(Authority.HANDYWORKER);
		Assert.isTrue(principal.getAuthorities().contains(auth));
		//We check if is new we then save it 
		Curriculum result;
		if (curriculum.getId() == 0) {
			curriculum.setTicker(Tickers.generateTicker());
			result = this.curriculumRepository.save(curriculum);
		} else {
			//if is an updated one 
			//check that belongs to the logged handy worker
			final HandyWorker hw = (HandyWorker) this.actorService.findByUserAccountId(LoginService.getPrincipal().getId());
			Assert.isTrue(hw.getCurricula().contains(curriculum));
			result = this.curriculumRepository.save(curriculum);
		}

		return result;

	}

	public void delete(final Curriculum curriculum) {
		Assert.notNull(curriculum);
		//Checking that is a handy worker
		final UserAccount principal = LoginService.getPrincipal();
		Assert.notNull(principal);
		final Authority auth = new Authority();
		auth.setAuthority(Authority.HANDYWORKER);
		Assert.isTrue(principal.getAuthorities().contains(auth));
		//We check that the curriculum belongs to the logged handy worker
		final HandyWorker hw = (HandyWorker) this.actorService.findByUserAccountId(LoginService.getPrincipal().getId());
		Assert.isTrue(hw.getCurricula().contains(curriculum));
		hw.getCurricula().remove(curriculum);
		this.curriculumRepository.delete(curriculum);
		this.handyWorkerService.save(hw);
	}

	//find curriculum by the EduRecordId
	public Curriculum findCurriculumByEduRecordId(final int eduRecordId) {
		return this.curriculumRepository.findCurriculumByEduRecordId(eduRecordId);
	}
	//find curriculum by the ProRecordId
	public Curriculum findCurriculumByProRecordId(final int proRecordId) {
		return this.curriculumRepository.findCurriculumByProRecordId(proRecordId);
	}
	//find curriculum by the MisRecordId
	public Curriculum findCurriculumByMisRecordId(final int misRecordId) {
		return this.curriculumRepository.findCurriculumByMisRecordId(misRecordId);
	}
	//find curriculum by the EndRecordId
	public Curriculum findCurriculumByEndRecordId(final int endRecordId) {
		return this.curriculumRepository.findCurriculumByEndRecordId(endRecordId);
	}
}
