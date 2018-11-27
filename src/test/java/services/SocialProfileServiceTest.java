package services;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import domain.SocialProfile;

import utilities.AbstractTest;
import utilities.internal.SchemaPrinter;


@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class SocialProfileServiceTest  extends AbstractTest {

	@Autowired
	public SocialProfileService socialProfileService;
	
	@Test
	public void testFindAll(){
		super.authenticate("customer");
		List<SocialProfile> sps = new ArrayList<SocialProfile>(this.socialProfileService.findAll());
		SchemaPrinter.print(sps);
	}
	@Test
	public void testFindOne(){
		super.authenticate("admin");
		List<SocialProfile> sps = new ArrayList<SocialProfile>(this.socialProfileService.findAll());
		SocialProfile sp = this.socialProfileService.findOne(sps.get(2).getId());
		SchemaPrinter.print(sp);
	}
	@Test
	public void testCreateSocialProfile(){
		super.authenticate("customer");
		SocialProfile sp, nuevo;
		sp = this.socialProfileService.create();
		sp.setLink("www.switches.com");
//		this.socialProfileService.save(sp);
		
	}
}
