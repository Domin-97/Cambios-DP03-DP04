
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

import domain.Customer;
import domain.Endorser;

import utilities.AbstractTest;
import utilities.internal.SchemaPrinter;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class EndorserServiceTest extends AbstractTest {

	@Autowired
	public EndorserService			endorserService;

	@Autowired
	public EndorsementService		endorsementService;

	@Autowired
	public CustomerService			customerService;

	@Rule
	public final ExpectedException	exception	= ExpectedException.none();


	@Test
	public void findOneTest() {
		//No hace falta estar logeado
		super.unauthenticate();
		Customer customer4 = this.customerService.findOne(super.getEntityId("customer4"));
		Endorser result = this.endorserService.findOne(customer4.getId());
		Assert.notNull(result);
		super.unauthenticate();
	}

	@Test
	public void findOneTestFail() {
		//No hace falta estar logeado
		super.unauthenticate();
		//El usuario solicitado no es un endorser
		this.exception.expect(IllegalArgumentException.class);
		Endorser result = this.endorserService.findOne(super.getEntityId("customer4"));
		Assert.isNull(result);
		super.unauthenticate();
	}

	@Test
	public void findCreatorOfEndorsementTest() {
		//No hace falta estar logeado
		super.unauthenticate();
		Endorser result = this.endorserService.findCreatorOfEndorsement(super.getEntityId("endorsement4"));
		Assert.notNull(result);
		//Vemos que el usuario efectivamente tiene el endorsement en su lista de endorsements creados
		Assert.isTrue(result.getEndorsementsByMe().contains(this.endorsementService.findOne(super.getEntityId("endorsement4"))));
		super.unauthenticate();
	}

	@Test
	public void findReceptorOfEndorsementTest() {
		//No hace falta estar logeado
		super.unauthenticate();
		Endorser result = this.endorserService.findReceptorOfEndorsement(super.getEntityId("endorsement4"));
		Assert.notNull(result);
		//Vemos que el usuario efectivamente tiene el endorsement en su lista de endorsements recibidos
		Assert.isTrue(result.getEndorsementsByOther().contains(this.endorsementService.findOne(super.getEntityId("endorsement4"))));
		super.unauthenticate();
	}

}
