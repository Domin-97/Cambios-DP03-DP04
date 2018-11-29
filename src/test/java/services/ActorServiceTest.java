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

import utilities.AbstractTest;



@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class ActorServiceTest extends AbstractTest {

	@Autowired
	public ActorService actorService;
	
	//Test the findAll functionality
	@Test
	public void testFindAllActors(){
		List<Actor> acs = new ArrayList<Actor>(this.actorService.findAll());
		Assert.notEmpty(acs);
	}
	//Test the findOne functionality
	@Test
	public void testFindOneActor(){
		List<Actor> acs = new ArrayList<Actor>(this.actorService.findAll());
		Actor ac = this.actorService.findOne(acs.get(2).getId());
		Assert.notNull(ac);		
	}
	//Test to find an Actor using his UserAccountId 
	@Test
	public void testFindByUserAccountId(){
		Actor a = this.actorService.findByUserAccountId(super.getEntityId("userAccount2"));
		Assert.notNull(a);
	}
	
}