package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import repositories.ProfessionalRecordRepository;
import security.Authority;
import security.LoginService;
import security.UserAccount;
import domain.Curriculum;
import domain.HandyWorker;
import domain.ProfessionalRecord;

@Service
@Transactional
public class ProfessionalRecordService {

	//Managed Repository 
	@Autowired
	private ProfessionalRecordRepository	professionalRecordRepository;
	@Autowired
	private ActorService					actorService;
	@Autowired
	private CurriculumService				curriculumService;


	//Constructor
	public ProfessionalRecordService() {
		super();
	}
	//Simple CRUD METHODS

	public ProfessionalRecord create() {
		final ProfessionalRecord result;
		//Checking that is a handy worker
		final UserAccount principal = LoginService.getPrincipal();
		Assert.notNull(principal);
		final Authority auth = new Authority();
		auth.setAuthority(Authority.HANDYWORKER);
		Assert.isTrue(principal.getAuthorities().contains(auth));
		//Create a new professional record
		result = new ProfessionalRecord();
		result.setCompanyName("");
		result.setRole("");
		result.setProAttachment("");
		result.setProStartDate(new Date(System.currentTimeMillis() - 1000));
		result.setProEndDate(null);
		result.setProComments(new ArrayList<String>());

		return result;
	}
	public Collection<ProfessionalRecord> findAll() {
		Collection<ProfessionalRecord> result;
		result = this.professionalRecordRepository.findAll();
		Assert.notNull(result);
		return result;
	}
	public ProfessionalRecord findOne(final int professionalRecordId) {
		Assert.isTrue(professionalRecordId != 0);
		ProfessionalRecord result;
		result = this.professionalRecordRepository.findOne(professionalRecordId);
		Assert.notNull(result);

		return result;
	}
	public ProfessionalRecord save(final ProfessionalRecord professionalRecord) {
		Assert.notNull(professionalRecord);

		//Checking that is a handy worker
		final UserAccount principal = LoginService.getPrincipal();
		Assert.notNull(principal);
		final Authority auth = new Authority();
		auth.setAuthority(Authority.HANDYWORKER);
		Assert.isTrue(principal.getAuthorities().contains(auth));

		ProfessionalRecord result;
		//We check if is new we add save it 

		if (professionalRecord.getId() == 0)
			result = this.professionalRecordRepository.save(professionalRecord);
		else {
			//if is an updated one  check that belongs to the logged handy worker
			//we check if is the corresponding curriculum
			final HandyWorker hw = (HandyWorker) this.actorService.findByUserAccountId(LoginService.getPrincipal().getId());
			final Curriculum cu = this.curriculumService.findCurriculumByProRecordId(professionalRecord.getId());
			Assert.isTrue(hw.getCurricula().contains(cu));
			Assert.isTrue(cu.getProRecord().contains(professionalRecord));
			result = this.professionalRecordRepository.save(professionalRecord);
		}

		return result;
	}

	public void flush() {
		this.professionalRecordRepository.flush();
	}

	public void delete(final ProfessionalRecord professionalRecord) {
		Assert.notNull(professionalRecord);
		//We check that is a handy worker
		final UserAccount principal = LoginService.getPrincipal();
		Assert.notNull(principal);
		final Authority auth = new Authority();
		auth.setAuthority(Authority.HANDYWORKER);
		Assert.isTrue(principal.getAuthorities().contains(auth));

		//We check that the logged handy worker is the owner of the curriculum
		//We check that the professional record is deleted in the right curriculum	
		final HandyWorker hw = (HandyWorker) this.actorService.findByUserAccountId(LoginService.getPrincipal().getId());
		final Curriculum cu = this.curriculumService.findCurriculumByProRecordId(professionalRecord.getId());
		Assert.isTrue(hw.getCurricula().contains(cu));
		Assert.isTrue(cu.getProRecord().contains(professionalRecord));
		//Delete after checking
		cu.getProRecord().remove(professionalRecord);
		this.professionalRecordRepository.delete(professionalRecord);
		this.curriculumService.save(cu);

	}
}
