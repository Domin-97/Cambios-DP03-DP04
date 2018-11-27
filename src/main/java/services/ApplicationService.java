package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import domain.Application;
import domain.FixUpTask;
import domain.HandyWorker;
import domain.Money;

import repositories.ApplicationRepository;
import security.Authority;
import security.LoginService;
import security.UserAccount;

@Service
@Transactional
public class ApplicationService {

	// Managed repository-----------------------------------

	@Autowired
	private ApplicationRepository applicationRepository;

	// Supporting services----------------------------------

	@Autowired
	private HandyWorkerService handyWorkerService;
	
	@Autowired
	private ActorService actorService;

	// Constructor------------------------------------------

	public ApplicationService() {
		super();
	}

	// CRUD methods-----------------------------------------

	public Application create() {
		Application result = new Application();
		UserAccount principal = LoginService.getPrincipal();

		// Solo un handy worker puede crear una application
		Assert.isTrue(principal.getAuthorities()
				.contains(Authority.HANDYWORKER));

		// Inicialmente se crea con este estado por defecto
		result.setStatus("PENDING");
		result.setComments(new ArrayList<String>());
		result.setMoment(new Date(System.currentTimeMillis() - 1000));
		
		Money m = new Money();
		m.setAmount(1);
		m.setCurrency("Euros");
		m.setVatTax(21);
		result.setOfferedPrice(m);
		
		result.setCreditCard(null);
		result.setHandyWorker((HandyWorker) this.actorService.findByUserAccountId(principal.getId()));
		
		//result.setFixUpTask(fixUpTask);

		HandyWorker hw = this.handyWorkerService.findOne(principal.getId());
		hw.getApplications().add(result);

		// Hay que añadirlo también a la lista de aplicaciones de la task
		// correspondiente

		return result;
	}

	public Collection<Application> findAll() {
		Collection<Application> result;

		result = applicationRepository.findAll();
		Assert.notNull(result);

		return result;
	}

	public Collection<Application> findFixUpTaskApplications(FixUpTask fut) {
		Assert.notNull(fut);
		Collection<Application> result;

		result = this.applicationRepository.findFixUpTaskApplications(fut
				.getId());
		Assert.notNull(result);

		return result;
	}

	public Collection<Application> findHandyWorkerApplications(HandyWorker hw) {
		Assert.notNull(hw);
		Collection<Application> result;

		result = this.applicationRepository.findHandyWorkerApplications(hw
				.getId());
		Assert.notNull(result);

		return result;
	}

	public Application save(Application application) {
		Assert.notNull(application);
		Application result;
		
		if (application.getId()==0) {
			result = this.applicationRepository.save(application);
		} else {
			Application old = this.applicationRepository.findOne(application.getId());
			Assert.notNull(old);
			
			result = this.applicationRepository.save(application);
		}
		
		return result;
	}
	
	//NO DICE QUE SE PUEDAN BORRAR

}
