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
import domain.Message;
import domain.MessageBox;

import repositories.MessageRepository;
import security.Authority;
import security.LoginService;
import security.UserAccount;


@Service
@Transactional
public class MessageService {

	// Managed repository -----------------------------------------------------
	@Autowired
	private MessageRepository	messageRepository;
	// Supporting message --------------------------------------------------
	@Autowired
	private ActorService		actorService;
	@Autowired
	private MessageBoxService	messageBoxService;


	@Autowired
	private ConfigurationService configurationService;

	// Constructors -----------------------------------------------------------

	public MessageService() {
		super();
	}

	//Simple CRUD Methods------------------------------------------------------

	public Message create() {
		UserAccount ua = LoginService.getPrincipal();

		Message m = new Message();
		Date d = new Date(System.currentTimeMillis() - 1000);
		m.setSendDate(d);
		m.setSubject("");
		m.setBody("");
		m.setPriority("");
		m.setSender(this.actorService.findByUserAccountId(ua.getId()));
		m.setRecipients(new ArrayList<Actor>());
		m.setTags(new ArrayList<String>());
		return m;
	}

	public void delete(Message m) {
		Assert.notNull(m);
		
		Assert.isTrue(m.getId() != 0);
		
		UserAccount principal = LoginService.getPrincipal();
		Actor a = this.actorService.findByUserAccountId(principal.getId());
		MessageBox trash = this.messageBoxService.findMessageBoxByNameAndActorId("trash box", a.getId());

		if(trash.getMessages().contains(m)){
			trash.getMessages().remove(m);
			this.messageBoxService.save(trash);
			this.messageRepository.delete(m);
		}else{
			this.move(m, trash);
		}

	}

	public Message send(Message message) {
		Assert.notNull(message);
		Assert.isTrue(message.getId() == 0);
		UserAccount principal = LoginService.getPrincipal();
		Assert.isTrue(principal.equals(message.getSender().getUserAccount()));
		Assert.isTrue(!message.getRecipients().isEmpty());

		//GUARDO EL MENSAJE EN BBDD
		Message result = this.messageRepository.save(message);
		
		//GUARDO MENSAJE EN LA OUTBOX DEL SENDER
		MessageBox outbox = this.messageBoxService.findMessageBoxByNameAndActorId("out box", message.getSender().getId());
		outbox.getMessages().add(result);
		this.messageBoxService.save(outbox);
		
		//COMPRUEBO TABOO WORDS
		if (this.checkTabooWords(message) == true) {
			for (Actor actor : message.getRecipients()) {
				//GUARDO MESSAGE EN EL SPAMBOX DE CADA ACTOR
				Message mess= this.messageRepository.save(message);
				MessageBox spambox = this.messageBoxService.findMessageBoxByNameAndActorId("spam box", actor.getId());
				spambox.getMessages().add(mess);
				this.messageBoxService.save(spambox);
			}
		} else {
			for (Actor actor : message.getRecipients()) {
				//GUARDO MESSAGE EN LA INBOX DE CADA ACTOR
				Message mess = this.messageRepository.save(message);
				MessageBox inbox = this.messageBoxService.findMessageBoxByNameAndActorId("in box", actor.getId());
				inbox.getMessages().add(mess);
				this.messageBoxService.save(inbox);
			}
		}
		return result;
	}

	public Message findOne(int messageId) {
		Assert.isTrue(messageId != 0);
		//Comprobar que sea el propietario del actor
		return this.messageRepository.findOne(messageId);
	}
	public void flush(){
		this.messageRepository.flush();
	}

	//Other business Methods---------------------------------------------------

	public Collection<Message> findOwnMessages() {
		UserAccount ua = LoginService.getPrincipal();
		return this.messageRepository.findOwnMessages(this.actorService.findByUserAccountId(ua.getId()).getId());
	}

	public void move(Message m, MessageBox destination) {
		Assert.notNull(m);
		Assert.notNull(destination);
		
		Collection<Message> mowned = this.findOwnMessages();
		Assert.isTrue(mowned.contains(m));
		
		UserAccount principal = LoginService.getPrincipal();
		Actor a = this.actorService.findByUserAccountId(principal.getId());
		Assert.isTrue(a.getMessageBoxes().contains(destination));
		
		MessageBox source = this.messageBoxService.findByMessageId(m.getId());
		source.getMessages().remove(m);
		this.messageRepository.flush();
		
		destination.getMessages().add(m);
		this.messageBoxService.save(source);
		this.messageBoxService.save(destination);
	}
	private boolean checkTabooWords(Message m) {
		boolean result = false;
		Collection<String> tabooWords = new ArrayList<String>(this.configurationService.findSpamWords());
		for (String spam : tabooWords) {
			if (m.getBody().contains(spam) || m.getSubject().contains(spam)) {
				result = true;
				break;
			}
		}
		return result;
	}
	public Message sendBroadcast(Message m) {
		Assert.notNull(m);
		Assert.isTrue(m.getId() == 0);
		
		UserAccount principal = LoginService.getPrincipal();
		
		Authority auth = new Authority();
		auth.setAuthority(Authority.ADMIN);
		Assert.isTrue(principal.getAuthorities().contains(auth));
		Assert.isTrue(principal.equals(m.getSender().getUserAccount()));

		List<Actor> broadcast = new ArrayList<Actor>(this.actorService.findAll());
		broadcast.remove(m.getSender());
		m.setRecipients(broadcast);
		
		Message copy = this.messageRepository.save(m);
		
		MessageBox out = this.messageBoxService.findMessageBoxByNameAndActorId("out box", m.getSender().getId());
		out.getMessages().add(m);
		this.messageBoxService.save(out);
		
		if (this.checkTabooWords(m) == true) {
			for (Actor ac : m.getRecipients()) {
				Message mess = this.messageRepository.save(m);
				MessageBox spam = this.messageBoxService.findMessageBoxByNameAndActorId("spam box", ac.getId());
				spam.getMessages().add(mess);
				this.messageBoxService.save(spam);
			}
		} else {
			for (Actor ac : m.getRecipients()) {
				Message mess = this.messageRepository.save(m);
				MessageBox in = this.messageBoxService.findMessageBoxByNameAndActorId("in box", ac.getId());
				in.getMessages().add(mess);
				this.messageBoxService.save(in);
			}
		}

		return copy;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
