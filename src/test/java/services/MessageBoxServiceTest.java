package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.validation.ConstraintViolationException;

import org.apache.commons.collections.CollectionUtils;
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
import domain.MessageBox;
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
public class MessageBoxServiceTest extends AbstractTest {

	@Autowired
	private MessageBoxService	messageBoxService;

//	@Rule
//	public final ExpectedException exception = ExpectedException.none();
	@Autowired
	private ActorService		actorService;
//	@Autowired
//	private UserAccountService	userAccountService;
//
//	@Autowired
//	private MessageService	messageService;
//	
	@Test
	public void testFindAll(){
		super.authenticate("customer");
		List<MessageBox> mbs = new ArrayList<>(this.messageBoxService.findAll());
		SchemaPrinter.print(mbs);
	}
	@Test
	public void testFindOne() {
		super.authenticate("admin");
		List<MessageBox> mbowned = new ArrayList<MessageBox>(this.messageBoxService.findOwnMessageBoxes());
		MessageBox result = this.messageBoxService.findOne(mbowned.get(0).getId());
		Assert.notNull(result);
		//SchemaPrinter.print(result);
		super.unauthenticate();
	}
	@Test 
	public void testCreateMessageBox(){
		super.authenticate("customer");
		MessageBox mb = this.messageBoxService.create();
		mb.setName("Betis");
		//SchemaPrinter.print(mb);
		MessageBox saved = this.messageBoxService.save(mb);
		//SchemaPrinter.print(saved);
		Assert.isTrue(saved.getId()!=0);
		super.unauthenticate();
		
	}
	@Test 
	public void testEditMessageBox(){
		super.authenticate("customer");
		UserAccount principal = LoginService.getPrincipal();
		Actor a = this.actorService.findByUserAccountId(principal.getId());
		MessageBox mb = this.messageBoxService.create();
		mb.setName("Betis");
		//SchemaPrinter.print(mb);
		MessageBox saved = this.messageBoxService.save(mb);
		//SchemaPrinter.print(saved);
		MessageBox clone;
		clone = (MessageBox) this.messageBoxService.findOne(saved.getId());
		//SchemaPrinter.print(clone);
//		saved = clone;
//		MessageBox saved2 = this.messageBoxService.save(saved);
//		Assert.isTrue(saved2.getId()!=0);
//		SchemaPrinter.print(saved2);
		super.unauthenticate();
		
	}
	
	
	
}