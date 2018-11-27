package services;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import domain.Application;
import domain.Curriculum;
import domain.HandyWorker;
import domain.Tutorial;

import repositories.HandyWorkerRepository;
import security.Authority;

import security.UserAccount;

@Service
@Transactional
public class HandyWorkerService {

	// Managed repository-----------------------------------

	@Autowired
	private HandyWorkerRepository handyWorkerRepository;

	// Supporting services----------------------------------
	
	@Autowired
	private FinderService finderService;

	// Constructor------------------------------------------

	public HandyWorkerService() {
		super();
	}

	// CRUD methods-----------------------------------------

	public HandyWorker create() {
		HandyWorker result = new HandyWorker();

		UserAccount ua = new UserAccount();

		Authority auth = new Authority();
		auth.setAuthority(Authority.HANDYWORKER);

		ua.addAuthority(auth);
		result.setUserAccount(ua);
		
		result.setMake(result.getName() + result.getSurname());
		result.setCurricula(new ArrayList<Curriculum>());
		result.setTutorials(new ArrayList<Tutorial>());
		result.setFinder(finderService.create());
		result.setApplications(new ArrayList<Application>());
		
		return result;
	}

	public Collection<HandyWorker> findAll() {
		Collection<HandyWorker> result;

		result = handyWorkerRepository.findAll();
		Assert.notNull(result);

		return result;
	}

	public HandyWorker findOne(int id) {
		Assert.isTrue(id != 0);

		HandyWorker result;
		result = handyWorkerRepository.findOne(id);
		Assert.notNull(result);

		return result;
	}
	
	public HandyWorker save(HandyWorker hw) {
		Assert.notNull(hw);
		HandyWorker result;
		
		if (hw.getId()==0) {
			result = this.handyWorkerRepository.save(hw);
		} else {
			HandyWorker old = this.handyWorkerRepository.findOne(hw.getId());
			Assert.notNull(old);
			
			result = this.handyWorkerRepository.save(hw);
		}
		
		return result;
	}
	
	public void flush() {
		this.handyWorkerRepository.flush();
	}
	
	//De momento no se permite borrar handy workers

}
