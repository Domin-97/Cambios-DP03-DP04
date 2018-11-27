
package services;

import java.util.ArrayList;

import javax.transaction.Transactional;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import domain.Configuration;

import utilities.AbstractTest;
import utilities.internal.SchemaPrinter;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class ConfigurationServiceTest extends AbstractTest {

	@Autowired
	public ConfigurationService configurationService;
	
	@Rule
	public final ExpectedException exception = ExpectedException.none();
	
	@Test
	public void testFindAll() {
		Assert.isTrue(this.configurationService.findAll().size()==1);
		
	}
	@Test
	public void testSaveConfiguration(){

		Configuration old, clon;
		old = this.configurationService.findOne();
		clon = (Configuration) old.clone();
		//SchemaPrinter.print(clon);
		super.authenticate("admin");
		clon.setSystemName("Acme-Corchutequiero");
		//SchemaPrinter.print(this.configurationService.findOne());
		//SchemaPrinter.print(clon);
		old = clon;
		this.configurationService.save(old);
		SchemaPrinter.print(this.configurationService.findOne());

		
		
//		SchemaPrinter.print(old);
//		super.authenticate("admin");
//		String systemName = old.getSystemName();
//		SchemaPrinter.print(this.configurationService.findOne());
//		old.setSystemName("Acme-Corchutequiero");
//		SchemaPrinter.print(this.configurationService.findOne());
//		nuevo = this.configurationService.save(old);
//		SchemaPrinter.print(nuevo);
//		Assert.isTrue(!systemName.equals(nuevo.getSystemName()));
//		
		super.unauthenticate();

	}
	
	@Test
	public void testSaveCustomerConfiguration(){
		super.authenticate("handyworker2");
		Configuration old, saved;
		old = new ArrayList<Configuration>
									(this.configurationService.findAll()).get(0);
		old.setSystemName("Acme-Corchutequiero");
		this.exception.expect(IllegalArgumentException.class);
		saved = this.configurationService.save(old);
		super.unauthenticate();
		Assert.isNull(saved);
	}
	

}
