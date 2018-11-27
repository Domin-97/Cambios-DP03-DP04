
package services;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.transaction.Transactional;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import domain.Finder;

import security.LoginService;
import security.UserAccount;
import utilities.AbstractTest;
import utilities.internal.SchemaPrinter;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class FinderServiceTest extends AbstractTest {

	@Autowired
	public FinderService			finderService;

	@Rule
	public final ExpectedException	exception	= ExpectedException.none();


	@Test
	public void testFindOne() {
		super.authenticate("handyworker2");
		//El handyworker solicita su finder
		Finder finder = this.finderService.findOne(6123);
		//		SchemaPrinter.print(finder);
		Assert.notNull(finder);
		super.unauthenticate();
	}

	@Test
	public void testFindOne2() {
		super.authenticate("handyworker2");
		//El handyworker solicita un finder que no le pertenece
		this.exception.expect(IllegalArgumentException.class);
		Finder finder = this.finderService.findOne(6122);
		//		SchemaPrinter.print(finder);
		Assert.isNull(finder);
		super.unauthenticate();
	}

	@Test
	public void testFinderByHandyWorkerId() {
		super.authenticate("handyworker2");
		//El handyworker solicita su finder mediante su propio id
		int id = super.getEntityId("handyworker2");
		//		SchemaPrinter.print(id);
		Finder finder = this.finderService.findFinderByHandyWorkerId(id);
		Assert.notNull(finder);
		super.unauthenticate();
	}

	@Test
	public void testFinderByHandyWorkerId2() {
		super.authenticate("customer2");
		//Un customer solicita un finder
		int id = super.getEntityId("customer2");
		//		SchemaPrinter.print(id);
		this.exception.expect(IllegalArgumentException.class);
		Finder finder = this.finderService.findFinderByHandyWorkerId(id);
		Assert.isNull(finder);
		super.unauthenticate();
	}

	//	@Test
	//	public void testCreateFinder(){
	//		
	//	}

	@Test
	public void saveFinder() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

		super.authenticate("handyworker2");
		int id = super.getEntityId("handyworker2");
		Finder old = this.finderService.findFinderByHandyWorkerId(id);

		Finder clon = (Finder) old.clone();

		//		Finder copia = new Finder();
		//		copia.setCategory(old.getCategory());
		//		copia.setFixuptaskStored(old.getFixuptaskStored());
		//		copia.setId(old.getId());
		//		copia.setKeyword(old.getKeyword());
		//		copia.setLastUpdate(old.getLastUpdate());
		//		copia.setMaxDate(old.getMaxDate());
		//		copia.setMaxPrice(old.getMaxPrice());
		//		copia.setMinDate(old.getMinDate());
		//		copia.setMinPrice(old.getMinPrice());
		//		copia.setVersion(old.getVersion());
		//		copia.setWarranty(old.getWarranty());

		SchemaPrinter.print(old);

		clon.setCategory("CATEGORY");
		clon.setKeyword("computer");
		clon.setMinDate(new Date(System.currentTimeMillis()));
		String maxDate = "2018/11/28";

		try {
			clon.setMaxDate(sdf.parse(maxDate));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		clon.setMaxPrice(500);
		clon.setMinPrice(2);
		clon.setWarranty("Garantía 1");

		Finder suuu = this.finderService.findFinderByHandyWorkerId(id);
		SchemaPrinter.print(suuu);

		old = clon;

		Finder saved = this.finderService.save(old);
		Assert.notNull(saved);
		SchemaPrinter.print(saved);
		
		SchemaPrinter.print(this.finderService.findFinderByHandyWorkerId(id));

		super.unauthenticate();
	}

}
