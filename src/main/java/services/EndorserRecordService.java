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
		//Solo HandyWorker crea 
		final UserAccount principal = LoginService.getPrincipal();
		Assert.isTrue(principal.getAuthorities().contains(Authority.HANDYWORKER));

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
	//TODO
	public EndorserRecord save(final EndorserRecord endorserRecord) {
		Assert.notNull(endorserRecord);

		//Solo HandyWorker crea sections (Suponemos que edit tb)
		final UserAccount principal = LoginService.getPrincipal();
		Assert.isTrue(principal.getAuthorities().contains(Authority.HANDYWORKER));

		EndorserRecord result;
		if (endorserRecord.getId() == 0)
			result = this.endorserRecordRepository.save(endorserRecord);
		else {
			final Curriculum cu = this.curriculumService.findCurriculumByEndRecordId(endorserRecord.getId());
			cu.getEndRecord().add(endorserRecord);
			this.curriculumService.save(cu);
			result = this.endorserRecordRepository.save(endorserRecord);

		}
		return result;

	}

	//TODO
	public void delete(final EndorserRecord endorserRecord) {
		Assert.notNull(endorserRecord);
		//tiene que ser handyWorker
		final UserAccount principal = LoginService.getPrincipal();
		Assert.isTrue(principal.getAuthorities().contains(Authority.HANDYWORKER));

		//Tiene que ser el el propiertario del curriculum 
		final HandyWorker hw = (HandyWorker) this.actorService.findByUserAccountId(LoginService.getPrincipal().getId());
		final Curriculum cu = this.curriculumService.findCurriculumByEndRecordId(endorserRecord.getId());
		Assert.isTrue(hw.getCurricula().contains(cu));
		//?
		cu.getEduRecord().remove(endorserRecord);
		this.curriculumService.save(cu);

		this.endorserRecordRepository.delete(endorserRecord);
	}
}
