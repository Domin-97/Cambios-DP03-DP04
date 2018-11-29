package services;

import java.util.ArrayList;
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
import domain.MessageBox;
import security.LoginService;
import security.UserAccount;
import utilities.AbstractTest;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class MessageBoxServiceTest extends AbstractTest {

	@Autowired
	private MessageBoxService	messageBoxService;

	@Rule
	public final ExpectedException exception = ExpectedException.none();
	@Autowired
	private ActorService		actorService;
	
	//Test to create the system boxes
	@Test
	public void testCreateSystemBoxes(){
		List<MessageBox> sysbox = new ArrayList<>(this.messageBoxService.createSystemMessageBoxes());
		Assert.isTrue(sysbox.size()==4);
	}
	//Test the findAll functionality
	@Test
	public void testFindAll(){
		List<MessageBox> mbs = new ArrayList<>(this.messageBoxService.findAll());
		Assert.notEmpty(mbs);
	}
	//Test to find owned message boxes of a logged actor
	@Test 
	public void testFindAllOwned(){
		super.authenticate("customer");
		List<MessageBox> mbs = new ArrayList<>(this.messageBoxService.findOwnMessageBoxes());
		Assert.isTrue(mbs.size()==4);
		super.unauthenticate();
	}
	//Test to create a messageBox and store it in the database
	@Test
	public void testCreateSave(){
		super.authenticate("customer");
		UserAccount principal = LoginService.getPrincipal();
		Actor a = this.actorService.findByUserAccountId(principal.getId());
		
		//Create the message box 
		MessageBox mb = this.messageBoxService.create();
		mb.setName("A elegir");
		
		//Store in the database
		MessageBox saved = this.messageBoxService.save(mb);
		Assert.isTrue(saved.getId()!=0);
		this.messageBoxService.flush();
		MessageBox mbbdd = this.messageBoxService.findMessageBoxByNameAndActorId("A elegir", a.getId());
		
		//Test that now the owned message boxes of the actor contains the new one
		Assert.isTrue(this.messageBoxService.findOwnMessageBoxes().contains(mbbdd));
		super.unauthenticate();
	}
	//Test to create a messageBox, store it in the database and delete it from database
	@Test
	public void testCreateSaveDelete(){
		super.authenticate("customer");
		UserAccount principal = LoginService.getPrincipal();
		Actor a = this.actorService.findByUserAccountId(principal.getId());
		
		//Create the message box  
		MessageBox mb = this.messageBoxService.create();
		mb.setName("A elegir");
		
		//Store in the database
		MessageBox saved = this.messageBoxService.save(mb);
		Assert.isTrue(saved.getId()!=0);
		this.messageBoxService.flush();
		MessageBox mbbdd = this.messageBoxService.findMessageBoxByNameAndActorId("A elegir", a.getId());
		
		//Test that now the owned message boxes of the actor contains the new one
		Assert.isTrue(this.messageBoxService.findOwnMessageBoxes().contains(mbbdd));
		
		//Delete the message box previously created and stored
		this.messageBoxService.delete(mbbdd);
		this.messageBoxService.flush();
		
		//Test that now the owned message boxes of the actor doesn't have the message box
		Assert.isTrue(this.messageBoxService.findOwnMessageBoxes().size()==4);
		super.unauthenticate();
	}
	//Test to create a messageBox, store it in the database and modify its name
	@Test 
	public void testUpdateNameMessageBox(){
		super.authenticate("customer");
		
		//Create the message box 
		MessageBox mb = this.messageBoxService.create();
		mb.setName("A elegir");
		
		//Store in the database
		MessageBox saved = this.messageBoxService.save(mb);
		Assert.isTrue(saved.getId()!=0);
		this.messageBoxService.flush();
		
		//Test that now the owned message boxes of the actor contains the new one
		MessageBox saved2 = this.messageBoxService.findOne(saved.getId());
		Assert.isTrue(this.messageBoxService.findOwnMessageBoxes().contains(saved2));
			
		//Modify the name of the message box
		MessageBox clone = (MessageBox) saved2;
		clone.setName("Me gusta este");
		MessageBox cloned = this.messageBoxService.save(clone);
		this.messageBoxService.flush();
		Assert.isTrue(!saved2.getName().equals(cloned));
		super.unauthenticate();
	}
	//Test to try change a field of a box like the name
	@Test 
	public void testEditNameMessageBoxFallo(){
		super.authenticate("admin");
		List<MessageBox> mbowned;
		MessageBox clone;
		mbowned = new ArrayList<MessageBox>(this.messageBoxService.findOwnMessageBoxes());
		MessageBox result = this.messageBoxService.findOne(mbowned.get(0).getId());
		this.messageBoxService.flush();
		Assert.notNull(result);
		clone = (MessageBox) result.clone();
		clone.setName("");
		//The field name cannot be in blank
		this.exception.expect(IllegalArgumentException.class);
		this.messageBoxService.save(clone);
		this.messageBoxService.flush();
		super.unauthenticate();
		
	}
	//Try to edit 
	@Test 
	public void testEditSystemMessageBoxFallo(){
		super.authenticate("admin");
		List<MessageBox> mbowned;
		MessageBox clone;
		mbowned = new ArrayList<MessageBox>(this.messageBoxService.findOwnMessageBoxes());
		MessageBox result = this.messageBoxService.findOne(mbowned.get(0).getId());
		this.messageBoxService.flush();
		Assert.notNull(result);
		clone = (MessageBox) result.clone();
		clone.setIsSystem(false);
		//The field isSystem cannot be in modified
		this.exception.expect(IllegalArgumentException.class);
		this.messageBoxService.save(clone);
		this.messageBoxService.flush();
		super.unauthenticate();
		
	}
	
}