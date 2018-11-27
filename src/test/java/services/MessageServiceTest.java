
package services;

import java.util.ArrayList;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import domain.Actor;
import domain.Message;
import domain.MessageBox;

import security.LoginService;
import utilities.AbstractTest;
import utilities.internal.SchemaPrinter;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class MessageServiceTest extends AbstractTest {

	@Autowired
	private MessageService	messageService;

	@Autowired
	private ActorService	actorService;


	//	@Autowired
	//	private UserAccountService	userAccountService;

	@Autowired
	private MessageBoxService	messageBoxService;
		

	@Test
	public void testFindOne() {
		Assert.notNull(this.messageService.findOne(7041));
	}
	@Test
	public void testSendMesage() {
		super.authenticate("admin");
		Actor a = this.actorService.findByUserAccountId(LoginService.getPrincipal().getId());
		//SchemaPrinter.print(a);
		Message m;
		m = this.messageService.create();
		m.setSubject("Real");
		m.setBody("Betis");
		m.setPriority("HIGH");
		List<Actor> recipients = new ArrayList<Actor>(); 
		recipients.add(this.actorService.findByUserAccountId(6917));
		m.setRecipients(recipients);
		Assert.notNull(m);
		//SchemaPrinter.print(m);
		Message mess = this.messageService.send(m);
		this.messageService.flush();
		//SchemaPrinter.print(mess);
		Assert.notNull(mess);
		MessageBox outadmin = this.messageBoxService.findMessageBoxByNameAndActorId("out box", a.getId());
		//SchemaPrinter.print(outadmin);
		Assert.isTrue(outadmin.getMessages().contains(mess));
		super.unauthenticate();
		super.authenticate("customer4");
		Actor a2 = this.actorService.findByUserAccountId(LoginService.getPrincipal().getId());
		MessageBox incus = this.messageBoxService.findMessageBoxByNameAndActorId("in box", a2.getId());
		//SchemaPrinter.print(incus);
	}
	@Test
	public void testDeleteMessage(){
		super.authenticate("handyworker");
		Actor a2 = this.actorService.findByUserAccountId(LoginService.getPrincipal().getId());
		SchemaPrinter.print(a2);
		MessageBox incus = this.messageBoxService.findMessageBoxByNameAndActorId("in box", a2.getId());
		SchemaPrinter.print(incus);
		Message m = this.messageService.findOne(7041);
		this.messageService.delete(m);
		MessageBox nuevo = this.messageBoxService.findMessageBoxByNameAndActorId("in box", a2.getId());
		MessageBox trash = this.messageBoxService.findMessageBoxByNameAndActorId("trash box", a2.getId());
		SchemaPrinter.print(nuevo);
		//Compruebo que funciona y me lo manda al trash (funca el move)
		SchemaPrinter.print(trash);
		Assert.isTrue(!nuevo.getMessages().contains(m));
		
	}	
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
		//SchemaPrinter.print(m);
		super.unauthenticate();
	}
	//POR ARREGLAR
	//@Test
	public void testSendBroadcastMesage() {
		super.authenticate("admin");
		Actor a = this.actorService.findByUserAccountId(LoginService.getPrincipal().getId());
		//SchemaPrinter.print(a);
		Message m;
		m = this.messageService.create();
		m.setSubject("Real");
		m.setBody("Betis");
		m.setPriority("HIGH");
		Assert.notNull(m);
		SchemaPrinter.print(m);
		Message mess = this.messageService.sendBroadcast(m);
		this.messageService.flush();
		SchemaPrinter.print(mess);
		Assert.notNull(mess);
		MessageBox outadmin = this.messageBoxService.findMessageBoxByNameAndActorId("out box", a.getId());
		SchemaPrinter.print(outadmin);
		Assert.isTrue(outadmin.getMessages().contains(mess));
		super.unauthenticate();
		super.authenticate("customer4");
		Actor a2 = this.actorService.findByUserAccountId(LoginService.getPrincipal().getId());
		MessageBox incus = this.messageBoxService.findMessageBoxByNameAndActorId("in box", a2.getId());
		SchemaPrinter.print(incus);
	}

}
