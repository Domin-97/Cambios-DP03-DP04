
package services;

import java.util.Collection;

import javax.transaction.Transactional;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import utilities.AbstractTest;
import utilities.internal.SchemaPrinter;
import domain.Category;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class CategoryServiceTest extends AbstractTest {

	@Autowired
	private CategoryService			categoryService;

	@Rule
	public final ExpectedException	exception	= ExpectedException.none();


	//Test testFindAll()
	//Expected the return of all entities
	@Test
	public void testFindAllCategory() {
		Assert.isTrue(this.categoryService.findAll().size() >= 1);

	}
	//Test FindOne()
	//Expected the return of the category
	@Test
	public void testFindOneCategory() {
		Category c;
		final int id = super.getEntityId("category1");
		c = this.categoryService.findOne(id);
		Assert.notNull(c);
	}
	//Test findOne()2
	//Expected the return of null because no entity has this id
	@Test
	public void testFindOneCategory2() {
		Category c;
		final int id = -1;
		c = this.categoryService.findOne(id);
		Assert.isNull(c);
	}
	//Test CreateCategory()
	//Expected that the method returns a valid warranty
	@Test
	public void testCreateCategory() {
		super.authenticate("admin");
		final Category c;

		c = this.categoryService.create();
		Assert.notNull(c);
		Assert.isTrue(c.getParentCategory().getName().equals("CATEGORY"));
		super.unauthenticate();
	}
	//Test CreateCategory()2
	//Expected that the Assert in the method CreateWarranty throws a IAE because we are not authenticated at admin
	@Test
	public void testCreateCategory2() {
		super.unauthenticate();
		this.exception.expect(IllegalArgumentException.class);
		Category c;
		c = this.categoryService.create();
		SchemaPrinter.print(c);

	}
	//Test SaveCategory
	//Expected that the category was saved
	@Test
	public void testSaveCategory() {
		Category old, nuevo;
		final int id = super.getEntityId("category2");
		old = this.categoryService.findOne(id);
		super.authenticate("admin");
		final String name = old.getName();
		old.setName("changed");
		old.setParentCategory(null);
		nuevo = this.categoryService.save(old);
		Assert.isTrue(nuevo.getParentCategory().getName().equals("CATEGORY"));
		Assert.isTrue(!name.equals(nuevo.getName()));

		super.unauthenticate();

	}
	//Test SaveCategory2
	//Expected IAE, no admin authenticated
	@Test
	public void testSave2Category() {
		Category old;
		final int id = super.getEntityId("category2");
		old = this.categoryService.findOne(id);
		super.authenticate("customer");
		old.setName("Acme-Changed");
		this.exception.expect(IllegalArgumentException.class);
		this.categoryService.save(old);
		super.unauthenticate();

	}
	//Test SaveCategory3
	//Expected IAE, Trying to change the root category
	@Test
	public void testSave3Category() {
		super.authenticate("admin");
		Category old;
		final int id = super.getEntityId("category1");
		old = this.categoryService.findOne(id);
		final Category clon = (Category) old.clone();
		clon.setName("Acme-Changed");
		this.exception.expect(IllegalArgumentException.class);
		this.categoryService.save(clon);
		super.unauthenticate();

	}

	//Test SaveCategory4
	//Expected DataIntegrityViolationE, Trying to replique a name
	@Test
	public void testSave4Category() {
		super.authenticate("admin");
		Category old;
		final int id = super.getEntityId("category3");
		old = this.categoryService.findOne(id);
		old.setName("DOORS");
		this.exception.expect(DataIntegrityViolationException.class);
		this.categoryService.save(old);
		this.categoryService.flush();
		super.unauthenticate();

	}
	//Test DeleteCategory()
	//Expected that the category was delete
	@Test
	public void testDeleteCategory() {
		Category toDelete;
		super.authenticate("admin");
		final int id = super.getEntityId("category2");
		toDelete = this.categoryService.findOne(id);
		final Collection<Category> children = this.categoryService.findChildren(toDelete);
		this.categoryService.delete(toDelete);
		Assert.isNull(this.categoryService.findOne(id));
		for (final Category c : children)
			Assert.isTrue(c.getParentCategory().equals(toDelete.getParentCategory()));
		super.unauthenticate();

	}
	//Test DeleteCategory2()
	//Expected IAE, No user authen.
	@Test
	public void testDeleteCategory2() {
		Category toDelete;
		super.unauthenticate();
		final int id = super.getEntityId("category2");
		this.exception.expect(IllegalArgumentException.class);
		toDelete = this.categoryService.findOne(id);
		this.categoryService.delete(toDelete);
		Assert.isNull(this.categoryService.findOne(id));

	}
	//Test DeleteCategory3()
	//Expected IAE, trying to remove root category
	@Test
	public void testDeleteCategory3() {
		Category toDelete;
		super.authenticate("admin");
		final int id = super.getEntityId("category1");
		this.exception.expect(IllegalArgumentException.class);
		toDelete = this.categoryService.findOne(id);
		this.categoryService.delete(toDelete);
		Assert.isNull(this.categoryService.findOne(id));
		super.unauthenticate();
	}
	//Test testFindChildren()
	//Expected the return of the children of CATEGORY
	@Test
	public void testFindChildrenCategory() {
		Assert.isTrue(this.categoryService.findChildren(this.categoryService.findOne(super.getEntityId("category1"))).size() >= 1);

	}
	//Test FindByName()
	//Expected the return of the category finded
	@Test
	public void testFindByNameCategory() {
		Category c;
		c = this.categoryService.findByName("CATEGORY");
		Assert.notNull(c);
		Assert.isTrue(c.getName().equals("CATEGORY"));
	}

}
