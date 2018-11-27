package services;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import security.LoginService;
import security.UserAccount;
import security.UserAccountService;
import utilities.AbstractTest;
import utilities.internal.SchemaPrinter;


@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class UserAccountServiceTest  extends AbstractTest {

	@Autowired
	public UserAccountService userAccountService;
	
	@Test
	public void testFindAll(){
		List<UserAccount> uas = new ArrayList<UserAccount>(this.userAccountService.findAll());
		SchemaPrinter.print(uas);
	}
	@Test
	public void testFindOne(){
		super.authenticate("customer");
		UserAccount ua, result;
		ua = LoginService.getPrincipal();
		result = this.userAccountService.findOne(ua.getId());
		SchemaPrinter.print(result);
		super.unauthenticate();
	}
}