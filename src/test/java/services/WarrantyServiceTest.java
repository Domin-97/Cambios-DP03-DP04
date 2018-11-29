
package services;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import utilities.AbstractTest;
import utilities.internal.SchemaPrinter;
import domain.Warranty;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class WarrantyServiceTest extends AbstractTest {

	@Autowired
	private WarrantyService			warrantyService;

	@Rule
	public final ExpectedException	exception	= ExpectedException.none();


	//Test findAll()
	//Expected all the warranties returned.
	@Test
	public void testFindAll() {
		Assert.isTrue(this.warrantyService.findAll().size() >= 1);
	}
	//Test FindOne()
	//Expected the return of the entity
	@Test
	public void testFindOneWarranty() {
		Warranty w;
		final int id = super.getEntityId("warranty1");
		w = this.warrantyService.findOne(id);
		Assert.notNull(w);
	}
	//Test findOne()2
	//Expected the return of null because no entity has this id
	@Test
	public void testFindOneWarranty2() {
		Warranty w;
		final int id = -1;
		w = this.warrantyService.findOne(id);
		Assert.isNull(w);
	}
	//Test CreateWarraty()
	//Expected that the method returns a valid warranty
	@Test
	public void testCreateWarranty() {
		super.authenticate("admin");
		Warranty w;

		w = this.warrantyService.create();
		Assert.notNull(w);
		Assert.isTrue(!w.getFinalMode());
		super.unauthenticate();
	}
	//Test CreateWarraty()2
	//Expected that the Assert in the method CreateWarranty throws a IAE because we are not authenticated at admin
	@Test
	public void testCreateWarranty2() {
		super.unauthenticate();
		this.exception.expect(IllegalArgumentException.class);
		Warranty w;
		w = this.warrantyService.create();
		SchemaPrinter.print(w);

	}
	//Test SaveWarrantyFinalMode1()
	//Expected an IAE because the warranty was in finalMode, the new one is in draft mode
	@Test
	public void testSaveWarrantyFinalMode1() {
		Warranty old, nuevo, clon;
		final int id = super.getEntityId("warranty1");
		old = this.warrantyService.findOne(id);
		clon = (Warranty) old.clone();
		super.authenticate("admin");
		final String applicableLawsOld = old.getApplicableLaws();
		clon.setApplicableLaws("changed");
		clon.setFinalMode(false);
		this.exception.expect(IllegalArgumentException.class);
		nuevo = this.warrantyService.save(clon);

		Assert.isTrue(!applicableLawsOld.equals(nuevo.getApplicableLaws()));

		super.unauthenticate();
	}
	//Test SaveWarrantyFinalMode2()
	//Expected an IAE because the warranty is in finalMode, the new one in final mode too
	@Test
	public void testSaveFinalModeWarranty2() {
		Warranty old, nuevo;
		final int id = super.getEntityId("warranty1");
		old = this.warrantyService.findOne(id);
		super.authenticate("admin");
		final String applicableLaws = old.getApplicableLaws();
		old.setApplicableLaws("changed");
		old.setFinalMode(true);
		this.exception.expect(IllegalArgumentException.class);
		nuevo = this.warrantyService.save(old);

		Assert.isTrue(!applicableLaws.equals(nuevo.getApplicableLaws()));

		super.unauthenticate();
	}
	//Test SaveWarrantyFinalMode2()
	//Expected the update of the entity because the warranty is in draftMode
	@Test
	public void testSaveDraftModeWarranty2() {
		Warranty old, nuevo;
		final int id = super.getEntityId("warranty2");
		old = this.warrantyService.findOne(id);
		super.authenticate("admin");
		final String applicableLaws = old.getApplicableLaws();
		old.setApplicableLaws("changed");
		old.setTitle("changed");
		old.setTerms("changed");
		nuevo = this.warrantyService.save(old);
		Assert.isTrue(!applicableLaws.equals(nuevo.getApplicableLaws()));

		super.unauthenticate();
	}
	//Test SaveWarranty2()
	//Saving a new Warranty
	@Test
	public void testSaveWarranty2() {
		Warranty w;
		super.authenticate("admin");
		w = this.warrantyService.create();
		final int a = this.warrantyService.findAll().size();
		w.setApplicableLaws("Laws");
		w.setTerms("Terms");
		w.setTitle("Title");
		this.warrantyService.save(w);
		final int b = this.warrantyService.findAll().size();
		Assert.isTrue(a < b);

		super.unauthenticate();
	}
	//Test SaveWarranty3()
	//Expected a ConstraintViolationException because of null attributes
	@Test
	public void testSaveWarranty3() {
		Warranty w;
		super.authenticate("admin");
		w = this.warrantyService.create();
		final int a = this.warrantyService.findAll().size();
		this.exception.expect(ConstraintViolationException.class);
		this.warrantyService.save(w);
		final int b = this.warrantyService.findAll().size();
		Assert.isTrue(a == b);

		super.unauthenticate();
	}
	//Test SaveWarranty4()
	//Expected a IAE because of no authenticate
	@Test
	public void testSaveWarranty4() {
		super.unauthenticate();
		this.exception.expect(IllegalArgumentException.class);
		final int id = super.getEntityId("warranty2");
		this.warrantyService.save(this.warrantyService.findOne(id));

	}
	//Test DeleteWarranty()
	//Expected that the warranty was delete, warranty in DraftMode
	@Test
	public void testDeleteWarranty() {
		Warranty toDelete;
		super.authenticate("admin");
		final int id = super.getEntityId("warranty2");
		toDelete = this.warrantyService.findOne(id);
		this.warrantyService.delete(toDelete);
		Assert.isNull(this.warrantyService.findOne(id));
		super.unauthenticate();

	}
	//Test DeleteWarranty2()
	//Expected IAE, No user authen.
	@Test
	public void testDeleteWarranty2() {
		Warranty toDelete;
		super.unauthenticate();
		final int id = super.getEntityId("warranty2");
		this.exception.expect(IllegalArgumentException.class);
		toDelete = this.warrantyService.findOne(id);
		this.warrantyService.delete(toDelete);
		Assert.isNull(this.warrantyService.findOne(id));

	}
	//Test DeleteWarranty3()
	//Expected IAE, warranty in FinalMode
	@Test
	public void testDeleteWarranty3() {
		Warranty toDelete;
		super.authenticate("admin");
		final int id = super.getEntityId("warranty1");
		toDelete = this.warrantyService.findOne(id);
		this.exception.expect(IllegalArgumentException.class);
		this.warrantyService.delete(toDelete);
		Assert.isNull(this.warrantyService.findOne(id));
		super.unauthenticate();

	}

}
