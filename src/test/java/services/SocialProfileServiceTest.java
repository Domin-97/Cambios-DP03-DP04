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

import domain.SocialProfile;
import utilities.AbstractTest;


@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class SocialProfileServiceTest  extends AbstractTest {

	@Autowired
	private SocialProfileService socialProfileService;
	
	@Rule
	public final ExpectedException exception = ExpectedException.none();
	
	//Test the findAll functionality
	@Test
	public void testFindAll(){
		List<SocialProfile> sps = new ArrayList<SocialProfile>(this.socialProfileService.findAll());
		Assert.notEmpty(sps);
		super.unauthenticate();
	}
	//Test the findOwnSocialProfile of an actor random
	@Test
	public void testFindAllOwnSocialProfiles(){
		super.authenticate("customer");
		List<SocialProfile> sps = new ArrayList<SocialProfile>(this.socialProfileService.findOwnSocialProfiles());
		Assert.notEmpty(sps);
		super.unauthenticate();
	}
	//Test the findOne functionality
	@Test
	public void testFindOne(){
		SocialProfile sp = this.socialProfileService.findOne(super.getEntityId("SocialProfileCus1"));
		Assert.notNull(sp);
		super.unauthenticate();
	}
	//Test the creation of a socialProfile and store it in database
	@Test
	public void testCreateSocialProfile(){
		super.authenticate("customer");
		SocialProfile sp, nuevo;
		sp = this.socialProfileService.create();
		sp.setNick("GioLoCelso");
		sp.setLink("https://www.pinterest.es/pin/480477853992968071/");
		sp.setSocialNetworkName("Feisbook");
		nuevo = this.socialProfileService.save(sp);
		this.socialProfileService.flush();
		Assert.isTrue(nuevo.getId()!=0);
		super.unauthenticate();
	}
	//Test the elimination of a socialProfile which exists in database
	@Test
	public void testDeleteSocialProfile(){
		super.authenticate("customer");
		List<SocialProfile> spown = new ArrayList<SocialProfile>(this.socialProfileService.findOwnSocialProfiles());
		SocialProfile sp = spown.get(0);
		this.socialProfileService.delete(sp);
		this.socialProfileService.flush();
		Assert.isTrue(!this.socialProfileService.findOwnSocialProfiles().contains(sp));
	}
	//Log in as customer and try to delete the one from another actor
	@Test
	public void testDeleteSocialProfileMal(){
		super.authenticate("customer");
		SocialProfile sp = this.socialProfileService.findOne(super.getEntityId("SocialProfileAd1"));
		this.exception.expect(IllegalArgumentException.class);
		this.socialProfileService.delete(sp);
		this.socialProfileService.flush();
		Assert.isTrue(this.socialProfileService.findOwnSocialProfiles().contains(sp));
	}
	//Log in as customer and try to edit the one from another actor
	@Test
	public void testEditSocialProfile(){
		super.authenticate("customer");
		SocialProfile sp = this.socialProfileService.findOne(super.getEntityId("SocialProfileCus1"));
		SocialProfile clone = (SocialProfile) sp.clone();
		clone.setNick("Hackeador");
		this.socialProfileService.save(clone);
		this.socialProfileService.flush();
		Assert.isTrue(this.socialProfileService.findOwnSocialProfiles().contains(sp));
	}	
	//Log in as customer and try to edit the one from another actor
		@Test
		public void testEditSocialProfileMal(){
			super.authenticate("customer");
			SocialProfile sp = this.socialProfileService.findOne(super.getEntityId("SocialProfileAd1"));
			SocialProfile clone = (SocialProfile) sp.clone();
			clone.setNick("Hackeador");
			this.exception.expect(IllegalArgumentException.class);
			this.socialProfileService.save(clone);
			this.socialProfileService.flush();
			Assert.isTrue(this.socialProfileService.findOwnSocialProfiles().contains(sp));
		}
	//Test to try to create a SocialProfile with errors in some fields
	@Test
	public void testCreateSocialProfileMalCampos(){
		super.authenticate("customer");
		SocialProfile sp, nuevo;
		sp = this.socialProfileService.create();
		sp.setLink("www.switches.com");
		this.exception.expect(ConstraintViolationException.class);
		nuevo = this.socialProfileService.save(sp);
		Assert.isTrue(nuevo.getId()==0);
		super.unauthenticate();
	}
}
