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
import domain.Curriculum;
import domain.EducationRecord;
import domain.EndorserRecord;
import domain.HandyWorker;
import domain.MiscellaneousRecord;
import domain.ProfessionalRecord;

@Service
@Transactional
public class CurriculumService {

	//Repositorios
	@Autowired
	private CurriculumRepository	curriculumRepository;

	//Services
	@Autowired
	private ActorService			actorService;
	@Autowired
	private HandyWorkerService		handyWorkerService;


	public CurriculumService() {
		super();
	}
	//Simple CRUD methods

	//CUANDO CREAMOS EL CURRICULUM SE LE AÑADE AL HANDY WORKER QUE LO CREA 
	public Curriculum create() {
		Curriculum result;
		//Solo HandyWorker crea curriculum
		final UserAccount principal = LoginService.getPrincipal();
		Assert.notNull(principal);

		final Authority auth = new Authority();
		auth.setAuthority(Authority.HANDYWORKER);
		Assert.isTrue(principal.getAuthorities().contains(auth));

		result = new Curriculum();
		result.setEmail("");
		result.setlinkedInProfile("");
		result.setPhoneNumber("");
		result.setFullname("");
		result.setPhoto("");
		//NECESITA GENERAR TICKERS
		result.setTicker("");
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

		final UserAccount principal = LoginService.getPrincipal();
		Assert.notNull(principal);

		final Authority auth = new Authority();
		auth.setAuthority(Authority.HANDYWORKER);
		Assert.isTrue(principal.getAuthorities().contains(auth));

		Curriculum result;
		if (curriculum.getId() == 0)
			result = this.curriculumRepository.save(curriculum);
		else {
			final HandyWorker hw = (HandyWorker) this.actorService.findByUserAccountId(LoginService.getPrincipal().getId());
			Assert.isTrue(hw.getCurricula().contains(curriculum));
			result = this.curriculumRepository.save(curriculum);
		}

		return result;

	}

	public void delete(final Curriculum curriculum) {
		final UserAccount principal = LoginService.getPrincipal();
		Assert.notNull(principal);

		final Authority auth = new Authority();
		auth.setAuthority(Authority.HANDYWORKER);
		Assert.isTrue(principal.getAuthorities().contains(auth));

		Assert.notNull(curriculum);
		final HandyWorker hw = (HandyWorker) this.actorService.findByUserAccountId(LoginService.getPrincipal().getId());

		Assert.isTrue(hw.getCurricula().contains(curriculum));
		hw.getCurricula().remove(curriculum);
		this.handyWorkerService.save(hw);
		this.curriculumRepository.delete(curriculum);
	}

	public Curriculum findCurriculumByEduRecordId(final int eduRecordId) {
		return this.curriculumRepository.findCurriculumByEduRecordId(eduRecordId);
	}
	public Curriculum findCurriculumByProRecordId(final int proRecordId) {
		return this.curriculumRepository.findCurriculumByProRecordId(proRecordId);
	}
	public Curriculum findCurriculumByMisRecordId(final int misRecordId) {
		return this.curriculumRepository.findCurriculumByMisRecordId(misRecordId);
	}
	public Curriculum findCurriculumByEndRecordId(final int endRecordId) {
		return this.curriculumRepository.findCurriculumByEndRecordId(endRecordId);
	}
}
