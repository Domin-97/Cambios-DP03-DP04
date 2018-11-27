package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import repositories.EducationRecordRepository;
import security.Authority;
import security.LoginService;
import security.UserAccount;
import domain.Curriculum;
import domain.EducationRecord;
import domain.HandyWorker;

@Service
@Transactional
public class EducationRecordService {

	//Managed repository

	@Autowired
	private EducationRecordRepository	educationRecordRepository;

	//Supporting Services
	@Autowired
	private ActorService				actorService;
	@Autowired
	private CurriculumService			curriculumService;


	//Constructor
	public EducationRecordService() {
		super();
	}
	//Simple CRUD METHODS

	public EducationRecord create() {
		EducationRecord result;
		//Solo HandyWorker crea 
		final UserAccount principal = LoginService.getPrincipal();
		Assert.isTrue(principal.getAuthorities().contains(Authority.HANDYWORKER));

		result = new EducationRecord();

		result.setInstitution("");
		result.setEduTitle("");
		result.setEduAttachment("");
		result.setEduStartDate(new Date(System.currentTimeMillis() - 1000));
		result.setEduEndDate(null);
		result.setComments(new ArrayList<String>());
		return result;

	}
	public Collection<EducationRecord> findAll() {
		Collection<EducationRecord> result;

		result = this.educationRecordRepository.findAll();
		Assert.notNull(result);
		return result;
	}
	public EducationRecord findOne(final int educationRecordId) {
		Assert.isTrue(educationRecordId != 0);
		EducationRecord result;
		result = this.educationRecordRepository.findOne(educationRecordId);
		Assert.notNull(result);

		return result;

	}
	//TODO:NECESITA HANDY WORKER
	public EducationRecord save(final EducationRecord educationRecord) {
		Assert.notNull(educationRecord);

		//Solo HandyWorker crea sections (Suponemos que edit tb)
		final UserAccount principal = LoginService.getPrincipal();
		Assert.notNull(principal);

		final Authority auth = new Authority();
		auth.setAuthority(Authority.HANDYWORKER);
		Assert.isTrue(principal.getAuthorities().contains(auth));


		EducationRecord result;
		if (educationRecord.getId() == 0)
			result = this.educationRecordRepository.save(educationRecord);
		else
			//final Curriculum cum = this.curriculumService.findCurriculumByEduRecordId(educationRecord.getId());
			//cum.getEduRecord().add(educationRecord);
			//this.curriculumService.save(cum);
			result = this.educationRecordRepository.save(educationRecord);
		return result;

	}

	public void delete(final EducationRecord educationRecord) {
		Assert.notNull(educationRecord);
		//tiene que ser handyWorker
		final UserAccount principal = LoginService.getPrincipal();
		Assert.notNull(principal);

		final Authority auth = new Authority();
		auth.setAuthority(Authority.HANDYWORKER);
		Assert.isTrue(principal.getAuthorities().contains(auth));
		//Tiene que ser el el propiertario del curriculum 
		final HandyWorker hw = (HandyWorker) this.actorService.findByUserAccountId(LoginService.getPrincipal().getId());
		final Curriculum cu = this.curriculumService.findCurriculumByEduRecordId(educationRecord.getId());
		Assert.isTrue(hw.getCurricula().contains(cu));
		//?
		cu.getEduRecord().remove(educationRecord);
		this.curriculumService.save(cu);

		this.educationRecordRepository.delete(educationRecord);
	}
}
