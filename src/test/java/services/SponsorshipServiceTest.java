
package services;

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

import utilities.AbstractTest;
import domain.CreditCard;
import domain.Sponsorship;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class SponsorshipServiceTest extends AbstractTest {

	@Autowired
	private SponsorshipService		sponsorshipService;

	@Rule
	public final ExpectedException	exception	= ExpectedException.none();


	//Testing findAll sponsorships
	@Test
	public void testFindAll() {
		Assert.isTrue(this.sponsorshipService.findAll().size() >= 1);
	}
	//Testing findOne for sponsorship1
	@Test
	public void testFindOne() {
		Assert.notNull(this.sponsorshipService.findOne(super.getEntityId("sponsorship1")));
	}
	//Testing creating and saving a sponsorship succesfully
	@Test
	public void testCreateAndSaveSponsorship() {
		super.authenticate("sponsor");
		final Sponsorship spo = this.sponsorshipService.create();
		Sponsorship saved;
		final CreditCard card = new CreditCard();
		card.setCvv(754);
		card.setBrandName("Visa");
		card.setHolderName("Bill Gates");
		card.setNumber("4521587463258947");
		card.setExpirationMonth(3);
		card.setExpirationYear(20);

		spo.setBannerUrl("http:fotodespam.com");
		spo.setCreditCard(card);
		spo.setTargetPage("http:cocacola.com");

		saved = this.sponsorshipService.save(spo);
		final Collection<Sponsorship> ships = this.sponsorshipService.findAll();
		Assert.isTrue(ships.contains(saved));
		super.unauthenticate();

	}
	//Testing saving a sponsorship with a handy worker expecting a fail
	@Test
	public void testSaveSponsorshipFail() {
		super.authenticate("handyworker");
		final Sponsorship spo = this.sponsorshipService.findOne(super.getEntityId("sponsorship1"));
		Sponsorship saved;
		final CreditCard card = new CreditCard();
		card.setCvv(754);
		card.setBrandName("Visa");
		card.setHolderName("Bill Gates");
		card.setNumber("4521587463258947");
		card.setExpirationMonth(3);
		card.setExpirationYear(20);

		spo.setBannerUrl("http:fotodespam.com");
		spo.setCreditCard(card);
		spo.setTargetPage("http:cocacola.com");

		this.exception.expect(IllegalArgumentException.class);
		saved = this.sponsorshipService.save(spo);
		Assert.isNull(saved);
		super.unauthenticate();
	}
	//Testing updating an existing sponsorship
	@Test
	public void testSaveSponsorship() {
		super.authenticate("sponsor");
		Sponsorship old, clon;
		old = this.sponsorshipService.findOne(super.getEntityId("sponsorship1"));
		clon = (Sponsorship) old.clone();
		clon.setTargetPage("https://www.forocoches.com/");
		old = clon;
		final Sponsorship saved = this.sponsorshipService.save(old);
		Assert.notNull(saved);
		super.unauthenticate();
	}
	//Testing creating a sponsorship with a customer expecting a fail
	@Test
	public void testCreateAuntheticationFail() {
		super.authenticate("customer");
		this.exception.expect(IllegalArgumentException.class);
		this.sponsorshipService.create();
		super.unauthenticate();
	}

}
