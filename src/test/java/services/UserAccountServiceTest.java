
package services;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import security.LoginService;
import security.UserAccount;
import security.UserAccountService;
import utilities.AbstractTest;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class UserAccountServiceTest extends AbstractTest {

	@Autowired
	private UserAccountService	userAccountService;

	//Test findAll functionality
	@Test
	public void testFindAll() {
		List<UserAccount> uas = new ArrayList<UserAccount>(this.userAccountService.findAll());
		Assert.isTrue(uas.size() >= 10);
		//SchemaPrinter.print(uas);
	}
	//Test findOne functionality
	@Test
	public void testFindOne() {
		super.authenticate("customer");
		UserAccount ua, result;
		ua = LoginService.getPrincipal();
		result = this.userAccountService.findOne(ua.getId());
		Assert.notNull(result);
		super.unauthenticate();
	}
	//Test the supporting service findAllUsernames
	@Test
	public void testFindAllUsernames(){
		Assert.notEmpty(this.userAccountService.findAllUsernames());
	}

}
