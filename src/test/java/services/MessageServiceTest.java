
package services;

import java.util.ArrayList;
import java.util.Date;

import java.util.List;

import javax.validation.ConstraintViolationException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import domain.Actor;
import domain.Customer;
import domain.HandyWorker;
import domain.Message;
import domain.MessageBox;

import security.LoginService;
import security.UserAccount;
import security.UserAccountService;
import utilities.AbstractTest;
import utilities.internal.SchemaPrinter;


@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class MessageServiceTest extends AbstractTest {

	@Autowired
	private MessageService			messageService;
	@Rule
	public final ExpectedException	exception	= ExpectedException.none();
	@Autowired
	private ActorService			actorService;
	@Autowired
	private CustomerService			customerService;
	@Autowired
	private MessageBoxService		messageBoxService;
	@Autowired
	private UserAccountService		userAccountService;
	@Autowired
	private AdministratorService	administratorService;

	//Probar el servicio create funciona correctamente
	@Test
	public void testCreateMesage() {
		super.authenticate("admin");
		Message m;
		m = this.messageService.create();
		m.setSubject("Real");
		m.setBody("Betis");
		m.setPriority("HIGH");
		List<Actor> recipients = new ArrayList<Actor>();
		recipients.add(this.actorService.findByUserAccountId(6917));
		m.setRecipients(recipients);
		Assert.notNull(m);
		super.unauthenticate();
	}

	//Test the correct copy of a message to another folder of the same actor
	@Test
	public void testCopyMessage() {
		super.authenticate("customer");
		UserAccount principal = LoginService.getPrincipal();
		Actor a = this.actorService.findByUserAccountId(principal.getId());

		//Create the message box destination
		MessageBox mb = this.messageBoxService.create();
		mb.setName("Betis");

		//Save in database the message box
		MessageBox saved = this.messageBoxService.save(mb);
		this.messageBoxService.flush();
		List<MessageBox> mbowned = new ArrayList<MessageBox>(this.messageBoxService.findOwnMessageBoxes());
		Assert.isTrue(mbowned.contains(saved));

		//Find the message to be copied
		MessageBox source = this.messageBoxService.findMessageBoxByNameAndActorId("in box", a.getId());
		List<Message> m = new ArrayList<Message>(source.getMessages());
		Message copia = m.get(0);
		//Copy the message
		this.messageService.copy(copia, saved);
		this.messageBoxService.flush();
		//Test that the same message is in both message boxes
		Assert.isTrue(saved.getMessages().contains(copia) && source.getMessages().contains(copia));
	}
	//test the operation of sending a message
	@Test
	public void testSendMesage() {
		//Save the previous size of inbox of the recipient
		super.authenticate("customer4");
		MessageBox in = this.messageBoxService.findMessageBoxByNameAndActorId("in box", super.getEntityId("customer4"));
		int size = in.getMessages().size();
		super.unauthenticate();

		//Log in with the sender account
		super.authenticate("admin");
		Actor a = this.actorService.findByUserAccountId(LoginService.getPrincipal().getId());

		//Create the message
		Message m;
		m = this.messageService.create();
		m.setSubject("Real");
		m.setBody("Betis");
		m.setPriority("HIGH");
		List<Actor> recipients = new ArrayList<Actor>();
		int idrec = super.getEntityId("customer4");
		Customer rec = this.customerService.findOne(idrec);
		this.messageService.flush();
		recipients.add(rec);
		m.setRecipients(recipients);
		Assert.notNull(m);

		//Send the message
		Message mess = this.messageService.send(m);
		this.messageService.flush();
		Assert.notNull(mess);

		//Test look for a message in bbdd by Id
		Assert.notNull(this.messageService.findOne(mess.getId()));

		//Test that the outbox contains the new message
		MessageBox outadmin = this.messageBoxService.findMessageBoxByNameAndActorId("out box", a.getId());
		Assert.isTrue(outadmin.getMessages().contains(mess));
		super.unauthenticate();

		//Test that number of messages of the in box of customer4 has increased
		super.authenticate("customer4");
		Actor a2 = this.actorService.findByUserAccountId(LoginService.getPrincipal().getId());
		MessageBox incus = this.messageBoxService.findMessageBoxByNameAndActorId("in box", a2.getId());
		Assert.isTrue(incus.getMessages().size() > size);
		super.unauthenticate();
	}

	//test the operation of sending a message with content with spam words and turns the suspicous boolean to true
	@Test
	public void testSendMesageSpam() {
		//Save the previous size of inbox of the recipient
		super.authenticate("customer4");
		MessageBox in = this.messageBoxService.findMessageBoxByNameAndActorId("spam box", super.getEntityId("customer4"));
		int size = in.getMessages().size();
		super.unauthenticate();

		//Log in with the sender account
		super.authenticate("customer");
		Actor a = this.actorService.findByUserAccountId(LoginService.getPrincipal().getId());

		//Create the message
		Message m;
		m = this.messageService.create();
		m.setSubject("viagra");
		m.setBody("sex");
		m.setPriority("HIGH");
		List<Actor> recipients = new ArrayList<Actor>();
		int idrec = super.getEntityId("customer4");
		Customer rec = this.customerService.findOne(idrec);
		this.messageService.flush();
		recipients.add(rec);
		m.setRecipients(recipients);
		Assert.notNull(m);

		//Send the message
		Message mess = this.messageService.send(m);
		this.messageService.flush();
		Assert.notNull(mess);

		//Test look for a message in bbdd by Id
		Assert.notNull(this.messageService.findOne(mess.getId()));

		//Test that the out box contains the new message
		MessageBox outadmin = this.messageBoxService.findMessageBoxByNameAndActorId("out box", a.getId());
		Assert.isTrue(outadmin.getMessages().contains(mess));
		super.unauthenticate();

		//Test that the number of messages of the message box of spam from customer4 has increased
		super.authenticate("customer4");
		Actor a2 = this.actorService.findByUserAccountId(LoginService.getPrincipal().getId());
		MessageBox spamcus = this.messageBoxService.findMessageBoxByNameAndActorId("spam box", a2.getId());
		Assert.isTrue(spamcus.getMessages().size() > size);
		super.unauthenticate();
	}
	//test the operation of sending a message with the same sender and recipient
	@Test
	public void testAutosendMesage() {
		//Log in with the sender account
		super.authenticate("customer");
		Actor a = this.actorService.findByUserAccountId(LoginService.getPrincipal().getId());
		
		//Save the previous size of inbox of the recipient
		MessageBox in = this.messageBoxService.findMessageBoxByNameAndActorId("spam box", super.getEntityId("customer4"));
		int size = in.getMessages().size();
		
		//Create the message
		Message m;
		m = this.messageService.create();
		m.setSubject("REAL");
		m.setBody("BETIS");
		m.setPriority("HIGH");
		List<Actor> recipients = new ArrayList<Actor>();
		int idrec = super.getEntityId("customer");
		Customer rec = this.customerService.findOne(idrec);
		this.messageService.flush();
		recipients.add(rec);
		m.setRecipients(recipients);
		Assert.notNull(m);

		//Send the message
		Message mess = this.messageService.send(m);
		this.messageService.flush();
		Assert.notNull(mess);

		//Test look for a message in bbdd by Id
		Assert.notNull(this.messageService.findOne(mess.getId()));

		//Test that the outbox contains the new message
		MessageBox outadmin = this.messageBoxService.findMessageBoxByNameAndActorId("out box", a.getId());
		Assert.isTrue(outadmin.getMessages().contains(mess));
		super.unauthenticate();

		//Test that number of messages of customer4 has increased
		super.authenticate("customer");
		Actor a2 = this.actorService.findByUserAccountId(LoginService.getPrincipal().getId());
		MessageBox incus = this.messageBoxService.findMessageBoxByNameAndActorId("in box", a2.getId());
		Assert.isTrue(incus.getMessages().size() > size);
		super.unauthenticate();
	}
	@Test
	public void testSendNotification(){
		super.authenticate("customer");
		Actor a = this.actorService.findByUserAccountId(LoginService.getPrincipal().getId());
		MessageBox in = this.messageBoxService.findMessageBoxByNameAndActorId("in box", super.getEntityId("customer"));
		int size = in.getMessages().size();
		super.unauthenticate();
		super.authenticate("handyworker");
		UserAccount principal = LoginService.getPrincipal();
		Actor hw = this.actorService.findByUserAccountId(principal.getId());
		Message noti = this.messageService.create();
		List<Actor> recipients = new ArrayList<Actor>();
		recipients.add(hw);
		recipients.add(a);
		noti.setRecipients(recipients);
		noti.setSubject("Change in status of application");
		noti.setBody("Change to ACCEPTED");
		noti.setPriority(Message.HIGH);
		noti.setSendDate(new Date (System.currentTimeMillis()));
		noti.setSender(this.administratorService.findOne(super.getEntityId("admin1")));
		this.messageService.sendNotification(noti);
		this.messageService.flush();
		
		MessageBox inmod = this.messageBoxService.findMessageBoxByNameAndActorId("in box", super.getEntityId("customer"));
		int nuevo = inmod.getMessages().size();
		Assert.isTrue(nuevo>size);
	}
	
	//Prove that you do not send a message due to lack of necessary fields to send the message
	@Test
	public void testSendMesageSubjectVacio() {

		super.authenticate("admin");
		Message m;
		m = this.messageService.create();

		//Do not let send it because this message has empty the subject and the body
		m.setPriority("HIGH");
		List<Actor> recipients = new ArrayList<Actor>();
		int idrec = super.getEntityId("customer4");
		Customer rec = this.customerService.findOne(idrec);
		this.messageService.flush();
		recipients.add(rec);
		m.setRecipients(recipients);
		Assert.notNull(m);

		//Cannot send the message, expected exception
		this.exception.expect(ConstraintViolationException.class);
		this.messageService.send(m);
		this.messageService.flush();

	}
	//Prove that you do not send a message due to wrong information to send the message
	@Test
	public void testSendMesageNotSender() {
		//Try to send with another user, not the logged one
		super.authenticate("admin");
		Message m;
		m = this.messageService.create();
		m.setSubject("Real");
		m.setBody("Betis");
		m.setPriority("HIGH");
		Actor a = this.actorService.findByUserAccountId(super.getEntityId("userAccount2"));
		m.setSender(a);
		List<Actor> recipients = new ArrayList<Actor>();
		int idrec = super.getEntityId("customer4");
		Customer rec = this.customerService.findOne(idrec);
		this.messageService.flush();
		recipients.add(rec);
		m.setRecipients(recipients);
		Assert.notNull(m);
		this.exception.expect(IllegalArgumentException.class);
		this.messageService.send(m);
		this.messageService.flush();
	}
	//Test that the message is moved to the trash box if it's deleted
	@Test
	public void testDeleteMessageToTrashBox() {
		super.authenticate("handyworker");
		Actor a2 = this.actorService.findByUserAccountId(LoginService.getPrincipal().getId());
		MessageBox incus = this.messageBoxService.findMessageBoxByNameAndActorId("in box", a2.getId());
		List<Message> mess = new ArrayList<>(incus.getMessages());

		//Choose a message in the in box of handyworker
		Message m = mess.get(0);

		//Delete the message from the inbox 
		this.messageService.delete(m, incus);
		this.messageService.flush();
		MessageBox nuevo = this.messageBoxService.findMessageBoxByNameAndActorId("in box", a2.getId());
		MessageBox trash = this.messageBoxService.findMessageBoxByNameAndActorId("trash box", a2.getId());

		//Test that the message has been removed from inbox, and the message goes to trash box
		Assert.isTrue(!nuevo.getMessages().contains(m) && trash.getMessages().contains(m));
	}
	//Test that the message is deleted in the database
	@Test
	public void testDeleteMessageFull() {

		super.authenticate("handyworker");
		Actor a2 = this.actorService.findByUserAccountId(LoginService.getPrincipal().getId());
		MessageBox trashcus = this.messageBoxService.findMessageBoxByNameAndActorId("trash box", a2.getId());
		List<Message> mess = new ArrayList<>(trashcus.getMessages());

		//Choose a message in the trash box of handyworker
		Message m = mess.get(0);

		//Delete the message from trash box
		this.messageService.delete(m, trashcus);
		this.messageService.flush();

		//Test that the message isn't at the own messages of the actor
		MessageBox trash = this.messageBoxService.findMessageBoxByNameAndActorId("trash box", a2.getId());
		Assert.isTrue(!this.messageService.findOwnMessages().contains(m) && !trash.getMessages().contains(m));

	}
	//Test the correct service of sending a broadcast messsage from Admin 
	@Test
	public void testSendBroadcastMesage() {
		//Save the previous size of inbox of a random recipient
		super.authenticate("customer4");
		MessageBox in = this.messageBoxService.findMessageBoxByNameAndActorId("in box", super.getEntityId("customer4"));
		int size = in.getMessages().size();
		super.unauthenticate();

		//Authenticate as admin
		super.authenticate("admin");
		Actor a = this.actorService.findByUserAccountId(LoginService.getPrincipal().getId());

		//Create the message
		Message m;
		m = this.messageService.create();
		m.setSubject("Real");
		m.setBody("Betis");
		m.setPriority("HIGH");
		Assert.notNull(m);

		//Send the message
		Message mess = this.messageService.sendBroadcast(m);
		this.messageService.flush();
		Assert.notNull(mess);

		//Test that the outbox contains the new message
		MessageBox outadmin = this.messageBoxService.findMessageBoxByNameAndActorId("out box", a.getId());
		this.messageService.flush();
		Assert.isTrue(outadmin.getMessages().contains(mess));
		super.unauthenticate();

		//Test that number of messages of customer4 has increased
		super.authenticate("customer4");
		Actor a2 = this.actorService.findByUserAccountId(LoginService.getPrincipal().getId());
		this.messageService.flush();
		MessageBox incus = this.messageBoxService.findMessageBoxByNameAndActorId("in box", a2.getId());
		Assert.isTrue(incus.getMessages().size() > size);
		super.unauthenticate();

	}
	//Test that you do not send a brocrodcast messsage if it is not Admin
	@Test
	public void testSendBroadcastMesageNotAdmin() {
		//Save the previous size of inbox of the recipient
		super.authenticate("customer4");
		MessageBox in = this.messageBoxService.findMessageBoxByNameAndActorId("in box", super.getEntityId("customer4"));
		int size = in.getMessages().size();
		super.unauthenticate();

		//Authenticate as customer
		super.authenticate("customer");
		Actor a = this.actorService.findByUserAccountId(LoginService.getPrincipal().getId());

		//Create the message
		Message m;
		m = this.messageService.create();
		m.setSubject("Real");
		m.setBody("Betis");
		m.setPriority("HIGH");
		Assert.notNull(m);

		//Send the message
		this.exception.expect(IllegalArgumentException.class);
		Message mess = this.messageService.sendBroadcast(m);
		this.messageService.flush();
		Assert.notNull(mess);

		//Test that the outbox doesn't contain the new message
		MessageBox outadmin = this.messageBoxService.findMessageBoxByNameAndActorId("out box", a.getId());
		this.messageService.flush();
		Assert.isTrue(!outadmin.getMessages().contains(mess));
		super.unauthenticate();

		//Test that number of messages of customer4 hasn't increased
		super.authenticate("customer4");
		Actor a2 = this.actorService.findByUserAccountId(LoginService.getPrincipal().getId());
		this.messageService.flush();
		MessageBox incus = this.messageBoxService.findMessageBoxByNameAndActorId("in box", a2.getId());
		Assert.isTrue(incus.getMessages().size() == size);
		this.messageService.flush();
	}
	//Test to find the message boxes of an actor logged in
	@Test
	public void testFindOwnMessages() {

		super.authenticate("customer");
		List<Message> mown = new ArrayList<Message>(this.messageService.findOwnMessages());
		Assert.isTrue(mown.size() != 0);
		super.unauthenticate();
	}

}
