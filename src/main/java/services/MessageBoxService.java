
package services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import domain.Actor;
import domain.Administrator;
import domain.Customer;
import domain.HandyWorker;
import domain.Message;
import domain.MessageBox;
import domain.Referee;
import domain.Sponsor;

import repositories.MessageBoxRepository;
import security.Authority;
import security.LoginService;
import security.UserAccount;

@Service
@Transactional
public class MessageBoxService {

	// Managed repository -----------------------------------------------------
	@Autowired
	private MessageBoxRepository	messageBoxRepository;

	// Supporting service --------------------------------------------------
	@Autowired
	private ActorService			actorService;
	@Autowired
	private MessageService			messageService;

	@Autowired
	private AdministratorService	administratorService;
	@Autowired
	private CustomerService			customerService;
	@Autowired
	private HandyWorkerService		handyWorkerService;
	@Autowired
	private RefereeService			refereeService;
	@Autowired
	private SponsorService			sponsorService;


	// Constructors -----------------------------------------------------------

	public MessageBoxService() {
		super();
	}

	//Simple CRUD Methods------------------------------------------------------

	public MessageBox create() {
		MessageBox result = new MessageBox();
		result.setName("");
		result.setIsSystem(false);
		result.setMessages(new ArrayList<Message>());

		return result;
	}

	public MessageBox findOne(int messageBoxId) {
		Assert.isTrue(messageBoxId != 0);
		Assert.isTrue(this.findOwnMessageBoxes().contains(this.messageBoxRepository.findOne(messageBoxId)));
		return this.messageBoxRepository.findOne(messageBoxId);
	}

	public Collection<MessageBox> findAll() {
		Collection<MessageBox> result;
		result = this.messageBoxRepository.findAll();
		return result;
	}

	public void delete(MessageBox messageBox) {
		Assert.notNull(messageBox);
		Assert.isTrue(this.findOwnMessageBoxes().contains(messageBox));

		Assert.isTrue(messageBox.getIsSystem() == false);
		if (messageBox.getMessages().isEmpty() == true) {
			this.messageBoxRepository.delete(messageBox);
		} else {
			for (Message m : messageBox.getMessages()) {
				this.messageService.delete(m);
			}
			this.messageBoxRepository.delete(messageBox);
		}
	}
	public MessageBox save(MessageBox messageBox) {
		MessageBox result;
		Assert.isTrue(messageBox!=null, "La carpeta es null");
		UserAccount principal= LoginService.getPrincipal();
		
		if (messageBox.getId() == 0) {
			result = this.messageBoxRepository.save(messageBox);
			Assert.isTrue(messageBox.getName() != "in box" && messageBox.getName() != "out box" && messageBox.getName() != "trash box" && messageBox.getName() != "spam box");
			
			Actor a = this.actorService.findByUserAccountId(principal.getId());

			//NECESITO SABER QUE TIPO DE ACTOR ES PARA GUARDAR ESE ACTOR CON SU NUEVO COLLECTION de MessageBoxes
			Authority adAuth = new Authority();
			adAuth.setAuthority(Authority.ADMIN);
			Authority cusAuth = new Authority();
			cusAuth.setAuthority(Authority.CUSTOMER);
			Authority hwAuth = new Authority();
			hwAuth.setAuthority(Authority.HANDYWORKER);
			Authority refAuth = new Authority();
			refAuth.setAuthority(Authority.REFEREE);
			Authority spoAuth = new Authority();
			spoAuth.setAuthority(Authority.SPONSOR);
			//COMO TRATAR A LOS ACTORES
			if (a.getUserAccount().getAuthorities().contains(adAuth)) {
				Administrator ad = (Administrator) this.actorService.findByUserAccountId(principal.getId());
				ad.getMessageBoxes().add(result);
				this.administratorService.save(ad);
			} else if (a.getUserAccount().getAuthorities().contains(cusAuth)) {
				Customer cus = (Customer) this.actorService.findByUserAccountId(principal.getId());
				cus.getMessageBoxes().add(result);
				this.customerService.save(cus);
			} else if (a.getUserAccount().getAuthorities().contains(hwAuth)) {
				HandyWorker hw = (HandyWorker) this.actorService.findByUserAccountId(principal.getId());
				hw.getMessageBoxes().add(result);
				this.handyWorkerService.save(hw);
			} else if (a.getUserAccount().getAuthorities().contains(refAuth)) {
				Referee ref = (Referee) this.actorService.findByUserAccountId(principal.getId());
				ref.getMessageBoxes().add(result);
				this.refereeService.save(ref);
			} else if( a.getUserAccount().getAuthorities().contains(spoAuth)){
				Sponsor spo = (Sponsor) this.actorService.findByUserAccountId(principal.getId());
				this.sponsorService.save(spo);
			}
			
		} else {
			result = this.messageBoxRepository.save(messageBox);
		}

		return result;
	}

	//Other business Methods---------------------------------------------------
	public Collection<MessageBox> findOwnMessageBoxes() {
		Actor a = this.actorService.findByUserAccountId(LoginService.getPrincipal().getId());
		return this.messageBoxRepository.findMessageBoxesByActorId(a.getId());

	}
	public MessageBox findByMessageId(int messageId) {
		return this.messageBoxRepository.findByMessageId(messageId);
	}
	public MessageBox findMessageBoxByNameAndActorId(String name, int actorId) {
		return this.messageBoxRepository.findMessageBoxByNameAndActorId(name, actorId);
	}
	public Collection<MessageBox> createSystemMessageBoxes() {
		Collection<MessageBox> result = new ArrayList<MessageBox>();
		List<String> names = Arrays.asList("in box", "out box", "trash box", "spam box");
		for (String n : names) {
			MessageBox messageBox = this.create();
			messageBox.setName(n);
			messageBox.setIsSystem(true);
			messageBox.setMessages(new ArrayList<Message>());
			result.add(messageBox);
		}
		return result;
	}

	public void flush() {
		this.messageBoxRepository.flush();
		
	}

}
