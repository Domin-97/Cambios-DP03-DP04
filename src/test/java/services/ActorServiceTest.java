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

import security.LoginService;
import security.UserAccount;
import utilities.AbstractTest;
import utilities.internal.SchemaPrinter;


@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class ActorServiceTest  extends AbstractTest {

	@Autowired
	public ActorService actorService;
	
	@Test
	public void testFindAllActors(){
		List<Actor> acs = new ArrayList<Actor>(this.actorService.findAll());
		//SchemaPrinter.print(acs);
	}
	@Test
	public void testFindOneActor(){
		List<Actor> acs = new ArrayList<Actor>(this.actorService.findAll());
											//7096 Handyworker
		Actor ac = this.actorService.findOne(acs.get(3).getId());
		//SchemaPrinter.print(ac);
	}
//	@Test	
//	public void testCreateActor(){
//		Actor a = this.actorService.create();
//		SchemaPrinter.print(a);
//		a.setAddress("er keli loco");
//		SchemaPrinter.print(a);
//	
//	}
	@Test
	public void testDeleteActor(){
		super.authenticate("admin");
		Actor a = this.actorService.findOne(7096);
		this.actorService.delete(a);
		//Se rompe el findAll
		List<Actor> acs = new ArrayList<>(this.actorService.findAll());
		Assert.isTrue(!acs.contains(a));
		SchemaPrinter.print(acs);
	}
}