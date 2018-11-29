
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
		//Checking that is a handy worker

		final UserAccount principal = LoginService.getPrincipal();
		Assert.notNull(principal);
		final Authority auth = new Authority();
		auth.setAuthority(Authority.HANDYWORKER);
		Assert.isTrue(principal.getAuthorities().contains(auth));
		//Create the new education record
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
	public EducationRecord save(final EducationRecord educationRecord) {
		Assert.notNull(educationRecord);

		//Checking that is a handy worker
		final UserAccount principal = LoginService.getPrincipal();
		Assert.notNull(principal);
		final Authority auth = new Authority();
		auth.setAuthority(Authority.HANDYWORKER);
		Assert.isTrue(principal.getAuthorities().contains(auth));

		EducationRecord result;
		//We check if is new we save it 

		if (educationRecord.getId() == 0)
			result = this.educationRecordRepository.save(educationRecord);
		else {
			//if is an updated one  check that belongs to the logged handy worker
			//we check if is the corresponding curriculum
			final HandyWorker hw = (HandyWorker) this.actorService.findByUserAccountId(LoginService.getPrincipal().getId());
			final Curriculum cu = this.curriculumService.findCurriculumByEduRecordId(educationRecord.getId());
			Assert.isTrue(hw.getCurricula().contains(cu));
			Assert.isTrue(cu.getEduRecord().contains(educationRecord));
			result = this.educationRecordRepository.save(educationRecord);
		}
		return result;

	}
	public void flush() {
		this.educationRecordRepository.flush();
	}

	public void delete(final EducationRecord educationRecord) {
		Assert.notNull(educationRecord);
		//We check that is a handy worker
		final UserAccount principal = LoginService.getPrincipal();
		Assert.notNull(principal);
		final Authority auth = new Authority();
		auth.setAuthority(Authority.HANDYWORKER);
		Assert.isTrue(principal.getAuthorities().contains(auth));

		//We check that the logged handy worker is the owner of the curriculum
		//We check that the education record is deleted in the right curriculum
		final HandyWorker hw = (HandyWorker) this.actorService.findByUserAccountId(LoginService.getPrincipal().getId());
		final Curriculum cu = this.curriculumService.findCurriculumByEduRecordId(educationRecord.getId());
		Assert.isTrue(hw.getCurricula().contains(cu));
		Assert.isTrue(cu.getEduRecord().contains(educationRecord));
		//Delete after checking
		cu.getEduRecord().remove(educationRecord);
		this.educationRecordRepository.delete(educationRecord);
		this.curriculumService.save(cu);

	}
}
