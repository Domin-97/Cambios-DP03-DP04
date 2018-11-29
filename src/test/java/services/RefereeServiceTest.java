package services;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;

import javax.transaction.Transactional;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import domain.HandyWorker;
import domain.Note;
import domain.Referee;
import domain.Report;

import security.Authority;
import security.UserAccount;
import utilities.AbstractTest;


@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class RefereeServiceTest extends AbstractTest {
	
	@Autowired
	public RefereeService refereeService;
	
	@Rule
	public final ExpectedException exception = ExpectedException.none();
	@Test
	public void testFindAll() {
		
		Assert.isTrue(this.refereeService.findAll().size()>=1);
	}
	@Test
	public void testSaveReferee(){
		Referee old;
		old = this.refereeService.findOne(super.getEntityId("referee1"));
		super.authenticate("admin");
		
		
		this.refereeService.save(old);
		super.unauthenticate();

	}
	@Test
	public void testSaveMalReferee(){
		
		super.authenticate("handyWorker2");
		final Collection<Report> reportsWritten = new ArrayList<>();

		Referee old = new Referee();
		Referee clon = (Referee) old.clone();
		
		clon = this.refereeService.findOne(super.getEntityId("referee1"));
		clon.setReportsWritten(reportsWritten);
		this.exception.expect(IllegalArgumentException.class);
		
		clon= old;
		this.refereeService.save(old);
		
		
		super.unauthenticate();

	}
	@Test
	public void testCreateReferee(){
		super.authenticate("admin");
		Referee old = new Referee();
		Referee clon = (Referee) old.clone();
		final UserAccount ua = new UserAccount();
		final Authority auth = new Authority();
		final Collection<Report> reportsWritten = new ArrayList<>();

		clon.setUserAccount(ua);
		clon.setReportsWritten(reportsWritten);
		clon= old;
		this.refereeService.save(old);
		super.unauthenticate();
	}
	@Test
	public void testCreateMalReferee(){
		super.authenticate("customer");
		Referee old = new Referee();
		Referee clon = (Referee) old.clone();
		final UserAccount ua = new UserAccount();
		final Authority auth = new Authority();
		final Collection<Report> reportsWritten = new ArrayList<>();

		clon.setUserAccount(ua);
		clon.setReportsWritten(reportsWritten);
		clon= old;
		this.exception.expect(IllegalArgumentException.class);
		this.refereeService.save(old);
		super.unauthenticate();
	}
	//cambiando un atributo basico de actor 
	@Test
	public void testSaveMalActorReferee(){
		
		super.authenticate("customer"); 
		Referee old = this.refereeService.findOne(super.getEntityId("referee2"));
		Referee clon = (Referee) old.clone();
		//		SchemaPrinter.print(old);

		clon.setName("Nombre cambiado");
		clon.setMiddleName("fff");
		clon.setSurname("eeeee");
		clon.setPhoto("http:www.eeeeeeeeeeeee.es");
		clon.setEmail("maildeprueba2@gmail.com");
		clon.setPhoneNumber("622625336");
		clon.setAddress("murcia");

		old = clon;
		this.exception.expect(IllegalArgumentException.class);
		Referee saved = this.refereeService.save(old);
		Assert.isNull(saved);

		super.unauthenticate();
	}


	@Test
	public void testUpdateBooleanAttributes() {
		super.authenticate("admin");
		Referee old = this.refereeService.findOne(super.getEntityId("referee1"));
		Referee clon = (Referee) old.clone();
		//		SchemaPrinter.print(old);

		clon.setName("Nombre cambiado");
		clon.setMiddleName("NC");
		clon.setSurname("Cambiadito");
		clon.setPhoto("http:www.suuuu.es");
		clon.setEmail("maildeprueba2@gmail.com");
		clon.setPhoneNumber("666676666");
		clon.setAddress("Melilla");
		clon.setBanned(true);
		clon.setSuspicious(true);

		old = clon;
		this.exception.expect(IllegalArgumentException.class);
		Referee saved = this.refereeService.save(old);
		Assert.isNull(saved);

		Assert.isTrue(this.refereeService.findOne(super.getEntityId("referee1")).isSuspicious() == false);
		Assert.isTrue(this.refereeService.findOne(super.getEntityId("referee1s")).isBanned() == false);

		this.refereeService.flush(); //Para que cambie la versión
		//		SchemaPrinter.print(this.handyWorkerService.findOne(super.getEntityId("handyworker2")));

		super.unauthenticate();
	}
	
}
