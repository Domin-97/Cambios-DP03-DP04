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

import domain.Category;

import utilities.AbstractTest;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional

public class CategoryServiceTest extends AbstractTest{

	@Autowired
	public CategoryService categoryService;
	
	@Rule
	public final ExpectedException exception = ExpectedException.none();
	
	@Test
	public void testFindAll() {
		Assert.isTrue(this.categoryService.findAll().size()>=1);
		
	}
	@Test
	public void testSaveCategory(){
		Category old, nuevo;
		old = this.categoryService.findOne(6088);
		super.authenticate("admin");
		String name = old.getName();
		old.setName("Acme-Corchutequiero");
		nuevo = this.categoryService.save(old);
		
		Assert.isTrue(!name.equals(nuevo.getName()));
		
		super.unauthenticate();

	}
	@Test
	public void testSaveMalCategory(){
		Category old, nuevo;
		old = this.categoryService.findOne(6088);
		super.authenticate("customer");
		old.setName("Acme-Corchutequiero");
		this.exception.expect(IllegalArgumentException.class);
		nuevo = this.categoryService.save(old);
		super.unauthenticate();
		Assert.isNull(nuevo);

	}
	
}
