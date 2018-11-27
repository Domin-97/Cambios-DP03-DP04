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
		old = this.refereeService.findOne(6213);
		super.authenticate("admin");
		
		
		this.refereeService.save(old);
		super.unauthenticate();

	}
	@Test
	public void testSaveMalReferee(){
		
		super.authenticate("handyWorker");
		
		Referee old;
		old = this.refereeService.findOne(6213);
		this.exception.expect(IllegalArgumentException.class);
		this.refereeService.save(old);
		
		
		super.unauthenticate();

	}
	@Test
	public void testCreateReferee(){
		super.authenticate("admin");
		Referee old = new Referee();
		final UserAccount ua = new UserAccount();
		final Authority auth = new Authority();
		final Collection<Report> reportsWritten = new ArrayList<>();

		old.setUserAccount(ua);
		old.setReportsWritten(reportsWritten);
		this.refereeService.save(old);
		super.unauthenticate();
	}
	public void testCreateMalReferee(){
		super.authenticate("customer1");
		Referee old = new Referee();
		final UserAccount ua = new UserAccount();
		final Authority auth = new Authority();
		final Collection<Report> reportsWritten = new ArrayList<>();

		old.setUserAccount(ua);
		old.setReportsWritten(reportsWritten);
		this.exception.expect(IllegalArgumentException.class);
		this.refereeService.save(old);
		super.unauthenticate();
	}
}
