
package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import domain.Actor;
import domain.Application;
import domain.CreditCard;
import domain.Customer;
import domain.FixUpTask;
import domain.HandyWorker;
import domain.Message;
import domain.Money;

import repositories.ApplicationRepository;
import security.Authority;
import security.LoginService;
import security.UserAccount;
import security.UserAccountService;

@Service
@Transactional
public class ApplicationService {

	// Managed repository-----------------------------------

	@Autowired
	private ApplicationRepository	applicationRepository;

	// Supporting services----------------------------------

	@Autowired
	private HandyWorkerService		handyWorkerService;

	@Autowired
	private CustomerService			customerService;

	@Autowired
	private ActorService			actorService;

	@Autowired
	private FixUpTaskService		fixUpTaskService;

	@Autowired
	private ConfigurationService	configurationService;

	@Autowired
	private MessageService			messageService;
	
	@Autowired
	private UserAccountService		userAccountService;
	
	// Constructor------------------------------------------

	public ApplicationService() {
		super();
	}

	// CRUD methods-----------------------------------------

	public Application create() {
		Application result = new Application();
		UserAccount principal = LoginService.getPrincipal();

		// Solo un handy worker puede crear una application
		Authority auth = new Authority();
		auth.setAuthority(Authority.HANDYWORKER);
		Assert.isTrue(principal.getAuthorities().contains(auth));

		result.setMoment(new Date(System.currentTimeMillis() - 1000));
		// Inicialmente se crea con este estado por defecto
		result.setStatus("PENDING");

		Money m = new Money();
		m.setAmount(1);
		m.setCurrency("Euros");
		m.setVatTax(21);
		result.setOfferedPrice(m);
		result.setComments(new ArrayList<String>());
		result.setFixUpTask(null);

		result.setCreditCard(null);
		//		result.setHandyWorker((HandyWorker) this.actorService.findByUserAccountId(principal.getId()));
		result.setHandyWorker(this.handyWorkerService.findOne(this.actorService.findByUserAccountId(principal.getId()).getId()));

		return result;
	}

	public Collection<Application> findAll() {
		//Suponemos que cualquiera puede listarlas todas
		Collection<Application> result;
		result = applicationRepository.findAll();
		Assert.notNull(result);

		return result;
	}

	public Application findOne(int id) {
		Assert.isTrue(id != 0);

		UserAccount principal = LoginService.getPrincipal();
		Assert.notNull(principal);

		// Vemos si el que intenta acceder es un handyworker o un customer
		Authority auth = new Authority();
		auth.setAuthority(Authority.HANDYWORKER);
		Authority auth2 = new Authority();
		auth2.setAuthority(Authority.CUSTOMER);
		Assert.isTrue(principal.getAuthorities().contains(auth) || principal.getAuthorities().contains(auth2));

		Application result;
		result = this.applicationRepository.findOne(id);
		Assert.notNull(result);

		if (principal.getAuthorities().contains(auth)) {
			HandyWorker hw = this.handyWorkerService.findOne(this.actorService.findByUserAccountId(principal.getId()).getId());
			Assert.isTrue(result.getHandyWorker().getId() == hw.getId());
		} else {
			Customer cus = this.customerService.findOne(this.actorService.findByUserAccountId(principal.getId()).getId());
			Assert.isTrue(cus.getId() == result.getFixUpTask().getCustomer().getId());
		}

		return result;
	}
	public Collection<Application> findFixUpTaskApplications(FixUpTask fut) {
		Assert.notNull(fut);

		UserAccount principal = LoginService.getPrincipal();

		// Solo un customer puede listar las applications de una fixUpTask
		Authority auth = new Authority();
		auth.setAuthority(Authority.CUSTOMER);
		Assert.isTrue(principal.getAuthorities().contains(auth));

		Customer cus = this.customerService.findOne(this.actorService.findByUserAccountId(principal.getId()).getId());
		Assert.isTrue(fut.getCustomer().getId() == cus.getId());

		Collection<Application> result;
		result = this.applicationRepository.findFixUpTaskApplications(fut.getId());
		Assert.notNull(result);

		return result;
	}

	public Collection<Application> findHandyWorkerApplications(HandyWorker hw) {
		Assert.notNull(hw);

		UserAccount principal = LoginService.getPrincipal();

		// Solo un handyworker puede listar sus applications
		Authority auth = new Authority();
		auth.setAuthority(Authority.HANDYWORKER);
		Assert.isTrue(principal.getAuthorities().contains(auth));

		HandyWorker handyLogged = this.handyWorkerService.findOne(this.actorService.findByUserAccountId(principal.getId()).getId());
		Assert.isTrue(hw.getId() == handyLogged.getId());

		Collection<Application> result;
		result = this.applicationRepository.findHandyWorkerApplications(hw.getId());
		Assert.notNull(result);

		return result;
	}

	public Application save(Application application) {
		Assert.notNull(application);

		Application result = new Application();

		if (application.getId() == 0) {
			UserAccount principal = LoginService.getPrincipal();

			// Solo un handy worker puede guardar una application por primera vez
			Authority auth = new Authority();
			auth.setAuthority(Authority.HANDYWORKER);
			Assert.isTrue(principal.getAuthorities().contains(auth));

			result.setMoment(new Date(System.currentTimeMillis() - 1000));
			result.setStatus("PENDING");
			result.setComments(new ArrayList<String>());
			result.setCreditCard(null);
			HandyWorker handy = this.handyWorkerService.findOne(this.actorService.findByUserAccountId(principal.getId()).getId());
			result.setHandyWorker(handy);

			result.setOfferedPrice(application.getOfferedPrice());

			//HACER CON QUERY
			FixUpTask fut = application.getFixUpTask();
			Assert.notNull(fut);
			//Hay que ver si la fixUpTask no tiene ya una application accepted
			//Hay que ver si no tiene una application pendiente en la fixuptask
			boolean acceptedOrAnotherPending = false;
			//Hay que ver si no tiene una application pendiente en la fixuptask
			//HACER CON QUERY
			for (Application a : fut.getApplications()) {
				if ((a.getStatus().equals("ACCEPTED") || (a.getHandyWorker().getId() == handy.getId() && a.getStatus().equals("PENDING")))) {
					acceptedOrAnotherPending = true;
					break;
				}
			}
			Assert.isTrue(!acceptedOrAnotherPending);

			//Hay que comprobar que no se haya superado la end date
			Assert.isTrue(fut.getEndDate().after(result.getMoment()));

			result.setFixUpTask(application.getFixUpTask());

			result = this.applicationRepository.save(application);
			Assert.notNull(result);
			
			handy.getApplications().add(result);
			fut.getApplications().add(result);

			this.handyWorkerService.save(handy);
			this.fixUpTaskService.save(fut);

		} else {
			//La application puede ser modificada por el handyworker propietario de
			//la application o por el customer dueño de la fixuptask
			UserAccount principal = LoginService.getPrincipal();

			Authority auth = new Authority();
			auth.setAuthority(Authority.HANDYWORKER);
			Authority auth2 = new Authority();
			auth2.setAuthority(Authority.CUSTOMER);
			Assert.isTrue(principal.getAuthorities().contains(auth) || principal.getAuthorities().contains(auth2));

			//Si es el handyworker el que modifica la aplicación
			if (principal.getAuthorities().contains(auth)) {
				HandyWorker handy = this.handyWorkerService.findOne(this.actorService.findByUserAccountId(principal.getId()).getId());
				//Vemos si el handyworker es el dueño de la application
				Assert.isTrue(handy.getId() == application.getHandyWorker().getId());

				//Capturamos la versión antigua
				Application old = this.applicationRepository.findOne(application.getId());
				Assert.notNull(old);
				
				Assert.isTrue(old.getStatus().equals(application.getStatus()));
				Assert.isTrue(old.getMoment().equals(application.getMoment()));
				Assert.isTrue((old.getCreditCard() == null && application.getCreditCard() == null) || old.getCreditCard().equals(application.getCreditCard()));
				Assert.isTrue(old.getHandyWorker().getId() == application.getHandyWorker().getId());
				Assert.isTrue(old.getFixUpTask().getId() == application.getFixUpTask().getId());

				//El usuario solo puede modificar el precio si la application está en estado "PENDING"
				if (!old.getStatus().equals("PENDING"))
					Assert.isTrue(old.getOfferedPrice().equals(application.getOfferedPrice()));

				old.setOfferedPrice(application.getOfferedPrice());

				//El usuario no puede borrar comentarios
				Assert.isTrue(application.getComments().containsAll(old.getComments()));
				old.setComments(application.getComments());
				
				result = this.applicationRepository.save(old);
				
				//Envio de notificacion de cambio
				if(old.getStatus()!=application.getStatus()){
					Message noti = this.messageService.create();
					List<Actor> recipients = new ArrayList<Actor>();
					recipients.add(old.getHandyWorker()); 
					recipients.add(application.getFixUpTask().getCustomer());
					noti.setRecipients(recipients);
					noti.setSubject("Change in status of application");
					noti.setBody("Change to "+result.getStatus());
					noti.setPriority(Message.HIGH);
					noti.setSendDate(new Date (System.currentTimeMillis()));
					UserAccount admin = this.userAccountService.findByUsername("admin");
					noti.setSender(this.actorService.findByUserAccountId(admin.getId()));
					this.messageService.sendNotification(noti);
				}
			} else {
				Customer cus = this.customerService.findOne(this.actorService.findByUserAccountId(principal.getId()).getId());
				//El customer logeado es el dueño de la fixUpTask referenciada en la application
				Assert.isTrue(cus.getId() == application.getFixUpTask().getCustomer().getId());

				//Capturamos la versión antigua
				Application old = this.applicationRepository.findOne(application.getId());
				Assert.notNull(old);

				//Solo la puede actualizar si tiene estado "PENDING"
				Assert.isTrue(old.getStatus().equals("PENDING"));

				//Solo puede actualizar la application si cambia su estado a "ACCEPTED" o "REJECTED"
				Assert.isTrue(!old.getStatus().equals(application.getStatus()));

				//No se pueden cambiar estos parámetros
				Assert.isTrue(old.getMoment().equals(application.getMoment()));
				Assert.isTrue(old.getHandyWorker().getId() == application.getHandyWorker().getId());
				Assert.isTrue(old.getFixUpTask().getId() == application.getFixUpTask().getId());
				Assert.isTrue(old.getOfferedPrice().equals(application.getOfferedPrice()));

				//No se pueden eliminar comentarios anteriores
				Assert.isTrue(application.getComments().containsAll(old.getComments()));
				old.setComments(application.getComments());

				//El customer rechaza la application
				if (application.getStatus().equals("REJECTED")) {
					old.setStatus("REJECTED");
					Assert.isTrue(application.getCreditCard() == null);
				} else if (application.getStatus().equals("ACCEPTED")) {
					Assert.isTrue(application.getCreditCard() != null);

					//Hay que comprobar que la tarjeta es correcta
					CreditCard creditCard = application.getCreditCard();
					boolean valid = true;

					if (creditCard.getBrandName() == null || !this.configurationService.findOne().getCreditCardMakes().contains(creditCard.getBrandName())) {
						valid = false;
					}
					if (creditCard.getExpirationMonth() == null) {
						valid = false;
					}
					if (creditCard.getExpirationYear() == null) {
						valid = false;
					}
					if (creditCard.getCvv() == null) {
						valid = false;
					}
					if (creditCard.getHolderName() == null) {
						valid = false;
					}
					if (creditCard.getNumber() == null) {
						valid = false;
					}
					Assert.isTrue(valid);
					old.setCreditCard(creditCard);
					old.setStatus("ACCEPTED");
					
				}
				result = this.applicationRepository.save(old);
				Assert.notNull(result);
				//Si una application pasa a "ACCEPTED", el resto de las applications
				//de la FixUpTask pasan a "REJECTED"
				if (result.getStatus().equals("ACCEPTED")) {
					this.setRejected(result.getFixUpTask(), result);
				}
				//Envio de mensaje de notificacion de cambio
				if(result.getStatus().equals(application.getStatus())){
					Message noti = this.messageService.create();
					List<Actor> recipients = new ArrayList<Actor>();
					recipients.add(cus);
					recipients.add(result.getHandyWorker());
					noti.setRecipients(recipients);
					noti.setSubject("Change in status of application");
					noti.setBody("Change to "+result.getStatus());
					noti.setPriority(Message.HIGH);
					noti.setSendDate(new Date (System.currentTimeMillis()));
					UserAccount admin = this.userAccountService.findByUsername("admin");
					Assert.notNull(admin);
					noti.setSender(this.actorService.findByUserAccountId(admin.getId()));
					this.messageService.sendNotification(noti);
					
				}
			}
		}
		return result;
	}

	public void setRejected(FixUpTask fut, Application accepted) {
		for (Application a : fut.getApplications()) {
			if (!a.equals(accepted)) {
				a.setStatus("REJECTED");
				Message noti = this.messageService.create();
				List<Actor> recipients = new ArrayList<Actor>();
				recipients.add(a.getHandyWorker()); 
				noti.setRecipients(recipients);
				noti.setSubject("Change in status of application");
				noti.setBody("Change to REJECTED");
				noti.setPriority(Message.HIGH);
				noti.setSendDate(new Date (System.currentTimeMillis()));
				UserAccount admin = this.userAccountService.findByUsername("admin");
				noti.setSender(this.actorService.findByUserAccountId(admin.getId()));
				this.messageService.sendNotification(noti);
				this.applicationRepository.save(a);
			}
		}
	}

	public void delete(Application application) {
		Assert.notNull(application);

		UserAccount principal = LoginService.getPrincipal();

		// Solo un handyworker puede borrar sus applications
		Authority auth = new Authority();
		auth.setAuthority(Authority.HANDYWORKER);
		Assert.isTrue(principal.getAuthorities().contains(auth));

		HandyWorker handyLogged = this.handyWorkerService.findOne(this.actorService.findByUserAccountId(principal.getId()).getId());
		Assert.isTrue(application.getHandyWorker().getId() == handyLogged.getId());

		//Pillamos la versión antigua
		Application old = this.applicationRepository.findOne(application.getId());
		Assert.notNull(old);

		Assert.isTrue(old.getStatus().equals("PENDING"));

		this.applicationRepository.delete(old);
	}

	public void flush() {
		this.applicationRepository.flush();
	}

	public void deleteFixUpTaskApplications(FixUpTask fut) {
		Assert.notNull(fut);
		UserAccount principal = LoginService.getPrincipal();

		// Solo un customer puede borrar las applications pertenecientes a sus FixUpTasks
		//Solo se llama a este método al borrar una FixUpTAsk
		Authority auth = new Authority();
		auth.setAuthority(Authority.CUSTOMER);
		Assert.isTrue(principal.getAuthorities().contains(auth));

		Customer loggedCustomer = this.customerService.findOne(this.actorService.findByUserAccountId(principal.getId()).getId());
		Assert.isTrue(loggedCustomer.getId() == fut.getCustomer().getId());

		Assert.isTrue(this.fixUpTaskService.findAcceptedApplications(fut.getId()).isEmpty());

		List<Application> appList = new ArrayList<Application>(this.applicationRepository.findFixUpTaskApplications(fut.getId()));
		this.applicationRepository.delete(appList);
	}
}
