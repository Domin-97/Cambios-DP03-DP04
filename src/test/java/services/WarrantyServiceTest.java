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

import domain.Warranty;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class WarrantyServiceTest extends AbstractTest{

	@Autowired 
	public WarrantyService warrantyService;
	
	@Rule
	public final ExpectedException exception = ExpectedException.none();
	
	@Test
	public void testFindAll() {
		Assert.isTrue(this.warrantyService.findAll().size()>=1);
	}
	@Test
	public void testCreateWarranty(){
		super.authenticate("admin");
		Warranty w, saved; 
		Collection<Warranty> warranties;
		
		w = this.warrantyService.create();
		w.setTitle("La muerte");
		w.setTerms("A pellizcos");
		w.setApplicableLaws("ley del derecho a morirme");
		w.setFinalMode(false);
		saved = this.warrantyService.save(w);
		warranties = this.warrantyService.findAll();
		Assert.isTrue(warranties.contains(saved));
		super.unauthenticate();
	}
	@Test
	public void testSaveWarranty(){
		Warranty old, nuevo; 
		old = this.warrantyService.findOne(6243);
		super.authenticate("admin");
		String applicableLaws = old.getApplicableLaws();
		old.setApplicableLaws("La vida es feisima");
		nuevo = this.warrantyService.save(old);
		
		Assert.isTrue(!applicableLaws.equals(nuevo.getApplicableLaws()));
		
		super.unauthenticate();
	}
	@Test
	public void testSaveFinalModeWarranty(){
		Warranty old, nuevo; 
		old = this.warrantyService.findOne(6240);
		super.authenticate("admin");
		String applicableLaws = old.getApplicableLaws();
		old.setApplicableLaws("La vida es feisima");
		this.exception.expect(IllegalArgumentException.class);
		nuevo = this.warrantyService.save(old);
		
		Assert.isTrue(!applicableLaws.equals(nuevo.getApplicableLaws()));
		
		super.unauthenticate();
	}
}
