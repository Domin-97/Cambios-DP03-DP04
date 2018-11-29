
package services;

import javax.transaction.Transactional;

import org.joda.time.DateTime;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import utilities.AbstractTest;
import domain.FixUpTask;
import domain.Phase;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class PhaseServiceTest extends AbstractTest {

	@Autowired
	private PhaseService			phaseService;

	@Rule
	public final ExpectedException	exception	= ExpectedException.none();


	//Test testFindAll()
	//Expected the return of all entities
	@Test
	public void testFindAllPhases() {
		Assert.isTrue(this.phaseService.findAll().size() >= 1);

	}
	//Test FindOne()
	//Expected the return of the phase
	@Test
	public void testFindOnePhase() {
		Phase c;
		final int id = super.getEntityId("phase1");
		c = this.phaseService.findOne(id);
		Assert.notNull(c);
	}
	//Test findOne()2
	//Expected the return of null because no entity has this id
	@Test
	public void testFindOnePhase2() {
		Phase c;
		final int id = -1;
		c = this.phaseService.findOne(id);
		Assert.isNull(c);
	}

	//Create with a handyWorker authenticated
	//Expected The creation of the object in memory
	@Test
	public void testCreate() {
		super.authenticate("handyWorker");
		final Phase p = this.phaseService.create();
		Assert.notNull(p);
		super.unauthenticate();
	}

	//Create with no user authenticated
	//Expected an IAE
	@Test
	public void testCreateNoUser() {
		this.exception.expect(IllegalArgumentException.class);
		this.phaseService.create();
	}

	//Save a phase without auth
	//Expected an IAE
	@Test
	public void testSaveWarrantyFailAuth() {
		Phase old;
		final int id = super.getEntityId("phase1");
		old = this.phaseService.findOne(id);
		this.exception.expect(IllegalArgumentException.class);
		this.phaseService.save(old);

	}

	//Save a phase with correct handyWorker
	//Expected that the entity was saved
	@Test
	public void testSavePhase() {
		Phase old, nuevo, clon;
		super.authenticate("handyWorker2");
		final int id = super.getEntityId("phase2");
		old = this.phaseService.findOne(id);
		clon = (Phase) old.clone();
		final String oldDescription = old.getDescription();
		clon.setDescription("changed");
		clon.setNumber(1);

		nuevo = this.phaseService.save(clon);
		Assert.notNull(nuevo);
		Assert.isTrue(!nuevo.getDescription().equals(oldDescription));
		super.unauthenticate();
	}

	//Save a new phase
	//Expected that the entity was saved
	@Test
	public void testSavePhase2() {
		Phase old, nuevo;
		super.authenticate("handyWorker");
		old = this.phaseService.create();
		old.setDescription("A description");
		old.setNumber(1);
		old.setEndTime(DateTime.now().plusDays(5).toDate());
		old.setStartMoment(DateTime.now().plusDays(1).toDate());
		nuevo = this.phaseService.save(old);
		Assert.notNull(nuevo);
		Assert.isTrue(nuevo.getDescription().equals("A description"));
		super.unauthenticate();
	}

	//Save a new phase
	//Expected IAE, invalid number
	@Test
	public void testSavePhase3() {
		Phase old;
		super.authenticate("handyWorker");
		old = this.phaseService.create();
		old.setDescription("A description");
		old.setNumber(0);
		old.setEndTime(DateTime.now().plusDays(5).toDate());
		old.setStartMoment(DateTime.now().plusDays(1).toDate());
		this.exception.expect(IllegalArgumentException.class);
		this.phaseService.save(old);
		super.unauthenticate();
	}

	//Save a new phase
	//Expected IAE, invalid dates
	@Test
	public void testSavePhase4() {
		Phase old;
		super.authenticate("handyWorker");
		old = this.phaseService.create();
		old.setDescription("A description");
		old.setNumber(1);
		old.setEndTime(DateTime.now().plusDays(5).toDate());
		old.setStartMoment(DateTime.now().plusDays(6).toDate());
		this.exception.expect(IllegalArgumentException.class);
		this.phaseService.save(old);
		super.unauthenticate();
	}
	//Save (update) a phase with correct handyWorker
	//Expected IAE, wrong dates
	@Test
	public void testSavePhase5() {
		Phase old, nuevo, clon;
		super.authenticate("handyWorker");
		final int id = super.getEntityId("phase1");
		old = this.phaseService.findOne(id);
		clon = (Phase) old.clone();
		final String oldDescription = old.getDescription();
		clon.setDescription("changed");
		clon.setNumber(1);
		clon.setStartMoment(DateTime.now().minusYears(20).toDate());
		this.exception.expect(IllegalArgumentException.class);
		nuevo = this.phaseService.save(clon);
		Assert.notNull(nuevo);
		Assert.isTrue(!nuevo.getDescription().equals(oldDescription));
		super.unauthenticate();
	}
	//Save (update) a phase with incorrect handyWorker
	//Expected IAE, invalid user
	@Test
	public void testSavePhase6() {
		Phase old, nuevo, clon;
		super.authenticate("handyWorker3");
		final int id = super.getEntityId("phase1");
		old = this.phaseService.findOne(id);
		clon = (Phase) old.clone();
		final String oldDescription = old.getDescription();
		clon.setDescription("changed");
		clon.setNumber(1);
		this.exception.expect(IllegalArgumentException.class);
		nuevo = this.phaseService.save(clon);
		Assert.notNull(nuevo);
		Assert.isTrue(!nuevo.getDescription().equals(oldDescription));
		super.unauthenticate();
	}
	//Delete a phase with incorrect handyWorker
	//Expected IAE, invalid user
	@Test
	public void testDeletePhase() {
		Phase old;
		super.authenticate("handyWorker3");
		final int id = super.getEntityId("phase1");
		old = this.phaseService.findOne(id);
		this.exception.expect(IllegalArgumentException.class);
		this.phaseService.delete(old);
		super.unauthenticate();
	}
	//Delete a phase with no user auth
	//Expected IAE, invalid user
	@Test
	public void testDeletePhase2() {
		Phase old;
		final int id = super.getEntityId("phase1");
		old = this.phaseService.findOne(id);
		this.exception.expect(IllegalArgumentException.class);
		this.phaseService.delete(old);
	}
	//Delete a phase with the correct handyWorker
	//Expected the delete of the entity
	@Test
	public void testDeletePhase3() {
		Phase old;
		super.authenticate("handyWorker2");
		final int id = super.getEntityId("phase1");
		old = this.phaseService.findOne(id);
		this.phaseService.delete(old);
		Assert.isTrue(this.phaseService.findOne(id) == null);
		super.unauthenticate();
	}
	//Test testfindPhasesFromAcceptedApplications()
	//Expected the return of phases
	@Test
	public void testfindPhasesFromAcceptedApplications() {
		Assert.isTrue(this.phaseService.findPhasesFromAcceptedApplications(super.getEntityId("handyworker2")).size() >= 1);

	}
	//Test FindOne()
	//Expected the return of the phase
	@Test
	public void findFixUpTaskfromAPhase() {
		FixUpTask c;
		final int id = super.getEntityId("phase1");
		final int idhw = super.getEntityId("handyworker2");
		c = this.phaseService.findFixUpTaskfromAPhase(idhw, id);
		Assert.notNull(c);
	}
}
