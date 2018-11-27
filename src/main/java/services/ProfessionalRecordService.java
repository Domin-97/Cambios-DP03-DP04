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
		//Solo HandyWorker crea 
		final UserAccount principal = LoginService.getPrincipal();
		Assert.isTrue(principal.getAuthorities().contains(Authority.HANDYWORKER));

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
	//TODO:
	public ProfessionalRecord save(final ProfessionalRecord professionalRecord) {
		Assert.notNull(professionalRecord);

		//Solo HandyWorker crea sections (Suponemos que edit tb)
		final UserAccount principal = LoginService.getPrincipal();
		Assert.isTrue(principal.getAuthorities().contains(Authority.HANDYWORKER));

		ProfessionalRecord result;
		if (professionalRecord.getId() == 0)
			result = this.professionalRecordRepository.save(professionalRecord);
		else {
			final Curriculum cu = this.curriculumService.findCurriculumByProRecordId(professionalRecord.getId());
			cu.getProRecord().add(professionalRecord);
			this.curriculumService.save(cu);
			result = this.professionalRecordRepository.save(professionalRecord);
		}

		return result;
	}
	//TODO
	public void delete(final ProfessionalRecord professionalRecord) {
		Assert.notNull(professionalRecord);
		//tiene que ser handyWorker
		final UserAccount principal = LoginService.getPrincipal();
		Assert.isTrue(principal.getAuthorities().contains(Authority.HANDYWORKER));

		//Tiene que ser el el propiertario del curriculum 
		final HandyWorker hw = (HandyWorker) this.actorService.findByUserAccountId(LoginService.getPrincipal().getId());
		final Curriculum cu = this.curriculumService.findCurriculumByProRecordId(professionalRecord.getId());
		Assert.isTrue(hw.getCurricula().contains(cu));
		//?
		cu.getProRecord().remove(professionalRecord);
		this.curriculumService.save(cu);

		this.professionalRecordRepository.delete(professionalRecord);
	}
}
