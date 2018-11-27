
package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import repositories.PhaseRepository;
import security.Authority;
import security.LoginService;
import security.UserAccount;
import domain.FixUpTask;
import domain.HandyWorker;
import domain.Phase;

@Service
@Transactional
public class PhaseService {

	//Managed repositories

	@Autowired
	private PhaseRepository	phaseRepository;

	//Supporting services

	@Autowired
	private ActorService	actorService;


	// Constructors

	public PhaseService() {
		super();
	}

	//Simple CRUD methods

	public Phase create() {

		UserAccount userAccount;
		userAccount = LoginService.getPrincipal();
		final Authority auth = new Authority();
		auth.setAuthority(Authority.HANDYWORKER);
		Assert.isTrue(userAccount.getAuthorities().contains(auth));

		Phase result;

		result = new Phase();

		return result;

	}

	public Phase save(final Phase phase) {
		Assert.notNull(phase);
		Phase result;
		UserAccount userAccount;
		userAccount = LoginService.getPrincipal();
		final Authority auth = new Authority();
		auth.setAuthority(Authority.HANDYWORKER);
		Assert.isTrue(userAccount.getAuthorities().contains(auth));

		if (phase.getId() != 0) {
			final HandyWorker hw = (HandyWorker) this.actorService.findByUserAccountId(userAccount.getId());
			final FixUpTask fut = this.findFixUpTaskfromAPhase(hw.getId(), phase.getId());
			Assert.notNull(fut);
			Assert.isTrue(fut.getStartDate().before(phase.getStartMoment()) && fut.getEndDate().after(phase.getEndTime()));
			result = this.phaseRepository.save(phase);
		} else
			result = this.phaseRepository.save(phase);
		return result;

	}
	public Collection<Phase> findAll() {
		Collection<Phase> result;
		result = this.phaseRepository.findAll();
		return result;
	}

	public Phase findOne(final int phaseId) {
		Assert.isTrue(phaseId != 0);
		Phase result;
		result = this.phaseRepository.findOne(phaseId);
		return result;
	}

	public void delete(final Phase phase) {

		Assert.notNull(phase);
		UserAccount userAccount;
		userAccount = LoginService.getPrincipal();
		final Authority auth = new Authority();
		auth.setAuthority(Authority.HANDYWORKER);
		Assert.isTrue(userAccount.getAuthorities().contains(auth));

		final HandyWorker hw = (HandyWorker) this.actorService.findByUserAccountId(userAccount.getId());
		Assert.isTrue(this.findPhasesFromAcceptedApplications(hw.getId()).contains(phase));

		this.phaseRepository.delete(phase);
	}

	//Other methods

	Collection<Phase> findPhasesFromAcceptedApplications(final int hwId) {
		final List<Phase> result = new ArrayList<Phase>();
		result.addAll(this.phaseRepository.findPhasesFromAcceptedApplications(hwId));
		return result;
	}

	FixUpTask findFixUpTaskfromAPhase(final int hwId, final int phId) {
		final FixUpTask result = this.phaseRepository.findFixUpTaskfromAPhase(hwId, phId);
		return result;
	}

}
