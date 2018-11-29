package services;

import java.util.ArrayList;
import java.util.Collection;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import repositories.MiscellaneousRecordRepository;
import security.Authority;
import security.LoginService;
import security.UserAccount;
import domain.Curriculum;
import domain.HandyWorker;
import domain.MiscellaneousRecord;

@Service
@Transactional
public class MiscellaneousRecordService {

	@Autowired
	private MiscellaneousRecordRepository	miscellaneousRecordRepository;
	@Autowired
	private ActorService					actorService;
	@Autowired
	private CurriculumService				curriculumService;


	public MiscellaneousRecordService() {
		super();
	}
	//TODO
	public MiscellaneousRecord create() {
		final MiscellaneousRecord result;
		//Checking that is a handy worker
		final UserAccount principal = LoginService.getPrincipal();
		Assert.notNull(principal);
		final Authority auth = new Authority();
		auth.setAuthority(Authority.HANDYWORKER);
		Assert.isTrue(principal.getAuthorities().contains(auth));
		//Create new miscellaneous record
		result = new MiscellaneousRecord();
		result.setMisTitle("");
		result.setAttachment("");
		result.setComments(new ArrayList<String>());

		return result;
	}
	public Collection<MiscellaneousRecord> findAll() {
		Collection<MiscellaneousRecord> result;
		result = this.miscellaneousRecordRepository.findAll();
		Assert.notNull(result);
		return result;
	}
	public MiscellaneousRecord findOne(final int miscellaneousRecordId) {
		Assert.isTrue(miscellaneousRecordId != 0);
		MiscellaneousRecord result;
		result = this.miscellaneousRecordRepository.findOne(miscellaneousRecordId);
		Assert.notNull(result);
		return result;
	}
	public MiscellaneousRecord save(final MiscellaneousRecord miscellaneousRecord) {
		Assert.notNull(miscellaneousRecord);
		//Checking that is a handy worker
		final UserAccount principal = LoginService.getPrincipal();
		Assert.notNull(principal);

		final Authority auth = new Authority();
		auth.setAuthority(Authority.HANDYWORKER);
		Assert.isTrue(principal.getAuthorities().contains(auth));

		MiscellaneousRecord result;
		//We check if is new we add save it 
		if (miscellaneousRecord.getId() == 0)
			result = this.miscellaneousRecordRepository.save(miscellaneousRecord);
		else {
			//if is an updated one  check that belongs to the logged handy worker
			//we check if is the corresponding curriculum
			final HandyWorker hw = (HandyWorker) this.actorService.findByUserAccountId(LoginService.getPrincipal().getId());
			final Curriculum cu = this.curriculumService.findCurriculumByMisRecordId(miscellaneousRecord.getId());
			Assert.isTrue(hw.getCurricula().contains(cu));
			Assert.isTrue(cu.getMisRecord().contains(miscellaneousRecord));
			result = this.miscellaneousRecordRepository.save(miscellaneousRecord);
		}

		return result;
	}
	public void flush() {
		this.miscellaneousRecordRepository.flush();
	}
	public void delete(final MiscellaneousRecord miscellaneousRecord) {
		Assert.notNull(miscellaneousRecord);
		//We check that is a handy worker
		final UserAccount principal = LoginService.getPrincipal();
		Assert.notNull(principal);

		final Authority auth = new Authority();
		auth.setAuthority(Authority.HANDYWORKER);
		Assert.isTrue(principal.getAuthorities().contains(auth));

		//We check that the logged handy worker is the owner of the curriculum
		//We check that the miscellaneous record is deleted in the right curriculum		
		final HandyWorker hw = (HandyWorker) this.actorService.findByUserAccountId(LoginService.getPrincipal().getId());
		final Curriculum cu = this.curriculumService.findCurriculumByMisRecordId(miscellaneousRecord.getId());
		Assert.isTrue(hw.getCurricula().contains(cu));
		Assert.isTrue(cu.getMisRecord().contains(miscellaneousRecord));
		//Delete after checking
		cu.getMisRecord().remove(miscellaneousRecord);
		this.miscellaneousRecordRepository.delete(miscellaneousRecord);
		this.curriculumService.save(cu);

	}
}
