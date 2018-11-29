
package services;

import javax.transaction.Transactional;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import utilities.AbstractTest;
import domain.Sponsor;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class SponsorServiceTest extends AbstractTest {

	@Autowired
	private SponsorService			sponsorService;
	@Rule
	public final ExpectedException	exception	= ExpectedException.none();


	//Testing findAll on sponsors
	@Test
	public void testFindAll() {
		Assert.isTrue(this.sponsorService.findAll().size() >= 1);
	}
	//Testing findOne on sponsor1
	@Test
	public void testFindOne() {
		Assert.notNull(this.sponsorService.findOne(super.getEntityId("sponsor1")));
	}
	//Testing creating a sponsor succesfully
	@Test
	public void testCreateSponsor() {
		super.unauthenticate();
		Sponsor spo;
		spo = this.sponsorService.create();
		Assert.notNull(spo);
	}
	//Testing creating a sponsor logged expecting a fail 
	@Test
	public void testCreateFailSponsor() {
		super.authenticate("sponsor");
		Sponsor spo;
		this.exception.expect(IllegalArgumentException.class);
		spo = this.sponsorService.create();
		Assert.notNull(spo);
		super.unauthenticate();
	}
	//Testing saving a sponsor succesfully
	@Test
	public void testSaveSponsor() {
		super.unauthenticate();
		Sponsor spo;
		spo = this.sponsorService.create();

		final Sponsor clon = (Sponsor) spo.clone();

		//Actor properties
		clon.setName("Mercedes");
		clon.setMiddleName("Gucci");
		clon.setSurname("Brahim");
		clon.setPhoto("http:www.bugatti.es");
		clon.setEmail("maildeprueba@mohamed.com");
		clon.setPhoneNumber("696966969");
		clon.setAddress("Castilleja");

		//New Account
		clon.getUserAccount().setUsername("Versace");
		clon.getUserAccount().setPassword("wibuaeskfb123123b");

		spo = clon;
		final Sponsor saved = this.sponsorService.save(spo);
		Assert.notNull(saved);
	}
	//Testing saving a sponsor expecting a fail
	@Test
	public void testSaveSponsorFail() {
		super.unauthenticate();
		Sponsor spo;
		spo = this.sponsorService.create();

		final Sponsor clon = (Sponsor) spo.clone();

		//Actor properties
		clon.setName("Mercedes");
		clon.setMiddleName("Gucci");
		clon.setSurname("Brahim");
		clon.setPhoto("http:www.bugatti.es");
		clon.setEmail("maildeprueba@mohamed.com");
		clon.setPhoneNumber("696966969");
		clon.setAddress("Castilleja");

		//New Account
		clon.getUserAccount().setUsername("Versace");
		clon.getUserAccount().setPassword("wibuaeskfb123123b");
		//logged 
		spo = clon;
		super.authenticate("sponsor2");
		this.exception.expect(IllegalArgumentException.class);
		final Sponsor saved = this.sponsorService.save(spo);
		Assert.isNull(saved);
		super.unauthenticate();
	}
	//Testing updating information from a sponsor
	@Test
	public void testUpdateBasicInformationSponsor() {
		super.authenticate("sponsor2");
		Sponsor old = this.sponsorService.findOne(super.getEntityId("sponsor2"));
		final Sponsor clon = (Sponsor) old.clone();

		clon.setName("Nombre nuevo");
		clon.setMiddleName("NC");
		clon.setSurname("Cambiadito");
		clon.setPhoto("http:www.suuuu.es");
		clon.setEmail("maildeprueba2@gmail.com");
		clon.setPhoneNumber("666676666");
		clon.setAddress("Zevilla");

		old = clon;
		final Sponsor saved = this.sponsorService.save(old);
		Assert.notNull(saved);
		this.sponsorService.flush();

		super.unauthenticate();
	}
	//Testing updating a sponsor as an admin succesfully
	@Test
	public void testAdminUpdate() {
		super.authenticate("admin");
		Sponsor old = this.sponsorService.findOne(super.getEntityId("sponsor2"));
		final Sponsor clon = (Sponsor) old.clone();

		clon.setSuspicious(true);
		clon.setBanned(true);

		old = clon;
		final Sponsor saved = this.sponsorService.save(old);
		Assert.notNull(saved);

		Assert.isTrue(this.sponsorService.findOne(super.getEntityId("sponsor2")).isSuspicious() == true);
		Assert.isTrue(this.sponsorService.findOne(super.getEntityId("sponsor2")).isBanned() == true);

		this.sponsorService.flush();

		super.unauthenticate();
	}

}
