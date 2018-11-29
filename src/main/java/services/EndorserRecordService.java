
package services;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import repositories.EndorserRecordRepository;
import security.Authority;
import security.LoginService;
import security.UserAccount;
import domain.Curriculum;
import domain.EndorserRecord;
import domain.HandyWorker;

@Service
@Transactional
public class EndorserRecordService {

	//Managed Repository
	@Autowired
	private EndorserRecordRepository	endorserRecordRepository;
	//Supporting services
	@Autowired
	private ActorService				actorService;
	@Autowired
	private CurriculumService			curriculumService;


	//Constructor
	public EndorserRecordService() {
		super();
	}
	//Simple Methods CRUD

	//TODO
	public EndorserRecord create() {
		final EndorserRecord result;
		//Checking that is a handy worker
		final UserAccount principal = LoginService.getPrincipal();
		Assert.notNull(principal);
		final Authority auth = new Authority();
		auth.setAuthority(Authority.HANDYWORKER);
		Assert.isTrue(principal.getAuthorities().contains(auth));

		result = new EndorserRecord();
		result.setFullname("");
		result.setEmail("");
		result.setLinkedInProfile("");
		result.setPhoneNumber("");
		result.setEndComment(new ArrayList<String>());

		return result;

	}
	public Collection<EndorserRecord> findAll() {
		Collection<EndorserRecord> result;
		result = this.endorserRecordRepository.findAll();
		Assert.notNull(result);
		return result;
	}
	public EndorserRecord findOne(final int endorserRecordId) {
		Assert.isTrue(endorserRecordId != 0);
		EndorserRecord result;
		result = this.endorserRecordRepository.findOne(endorserRecordId);
		Assert.notNull(result);
		return result;

	}

	public EndorserRecord save(final EndorserRecord endorserRecord) {
		Assert.notNull(endorserRecord);

		//Checking that is a handy worker
		final UserAccount principal = LoginService.getPrincipal();
		Assert.notNull(principal);
		final Authority auth = new Authority();
		auth.setAuthority(Authority.HANDYWORKER);
		Assert.isTrue(principal.getAuthorities().contains(auth));

		EndorserRecord result;
		//We check if is new we  save it 
		if (endorserRecord.getId() == 0)
			result = this.endorserRecordRepository.save(endorserRecord);
		else {
			//if is an updated one  check that belongs to the logged handy worker
			//we check if is the corresponding curriculum
			final HandyWorker hw = (HandyWorker) this.actorService.findByUserAccountId(LoginService.getPrincipal().getId());
			final Curriculum cu = this.curriculumService.findCurriculumByEndRecordId(endorserRecord.getId());
			Assert.isTrue(hw.getCurricula().contains(cu));
			Assert.isTrue(cu.getEndRecord().contains(endorserRecord));
			result = this.endorserRecordRepository.save(endorserRecord);

		}
		return result;

	}
	public void flush() {
		this.endorserRecordRepository.flush();
	}

	public void delete(final EndorserRecord endorserRecord) {
		Assert.notNull(endorserRecord);
		//We check that is a handy worker
		final UserAccount principal = LoginService.getPrincipal();
		Assert.notNull(principal);
		final Authority auth = new Authority();
		auth.setAuthority(Authority.HANDYWORKER);
		Assert.isTrue(principal.getAuthorities().contains(auth));

		//We check that the logged handy worker is the owner of the curriculum
		//We check that the endorser record is deleted in the right curriculum
		final HandyWorker hw = (HandyWorker) this.actorService.findByUserAccountId(LoginService.getPrincipal().getId());
		final Curriculum cu = this.curriculumService.findCurriculumByEndRecordId(endorserRecord.getId());
		Assert.isTrue(hw.getCurricula().contains(cu));
		Assert.isTrue(cu.getEndRecord().contains(endorserRecord));
		//Delete after checking
		cu.getEndRecord().remove(endorserRecord);
		this.endorserRecordRepository.delete(endorserRecord);
		this.curriculumService.save(cu);

	}
}
