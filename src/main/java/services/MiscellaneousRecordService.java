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
		//Solo HandyWorker crea 
		final UserAccount principal = LoginService.getPrincipal();
		Assert.isTrue(principal.getAuthorities().contains(Authority.HANDYWORKER));

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
		//Solo HandyWorker crea sections (Suponemos que edit tb)
		final UserAccount principal = LoginService.getPrincipal();
		Assert.isTrue(principal.getAuthorities().contains(Authority.HANDYWORKER));

		MiscellaneousRecord result;
		if (miscellaneousRecord.getId() == 0)
			result = this.miscellaneousRecordRepository.save(miscellaneousRecord);
		else {
			final Curriculum cu = this.curriculumService.findCurriculumByMisRecordId(miscellaneousRecord.getId());
			cu.getMisRecord().add(miscellaneousRecord);
			this.curriculumService.save(cu);
			result = this.miscellaneousRecordRepository.save(miscellaneousRecord);
		}

		return result;
	}
	public void delete(final MiscellaneousRecord miscellaneousRecord) {
		Assert.notNull(miscellaneousRecord);
		//tiene que ser handyWorker
		final UserAccount principal = LoginService.getPrincipal();
		Assert.isTrue(principal.getAuthorities().contains(Authority.HANDYWORKER));

		//Tiene que ser el el propiertario del curriculum 
		final HandyWorker hw = (HandyWorker) this.actorService.findByUserAccountId(LoginService.getPrincipal().getId());
		final Curriculum cu = this.curriculumService.findCurriculumByMisRecordId(miscellaneousRecord.getId());
		Assert.isTrue(hw.getCurricula().contains(cu));
		//?
		cu.getMisRecord().remove(miscellaneousRecord);
		this.curriculumService.save(cu);

		this.miscellaneousRecordRepository.delete(miscellaneousRecord);
	}
}
