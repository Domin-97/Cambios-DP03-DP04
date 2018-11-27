
package services;

import java.util.ArrayList;
import java.util.Collection;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import repositories.MessageBoxRepository;
import repositories.RefereeRepository;
import security.Authority;
import security.LoginService;
import security.UserAccount;
import domain.Referee;
import domain.Report;

@Service
@Transactional
public class RefereeService {

	// Manage repository----------------------------------------------------------------------------
	@Autowired
	private ActorService			actorService;
	@Autowired
	private RefereeRepository		refereeRepository;
	@Autowired
	private MessageBoxRepository	messageBoxRepository;


	//Suporting services -------------------------------------------------------------------------------

	//constructors --------------------------------------------------------------------------------
	public RefereeService() {
		super();
	}
	//Simple crud method------------------------------------------------------------------------------
	public Collection<Referee> findAll() {
		Collection<Referee> result;
		result = this.refereeRepository.findAll();
		return result;
	}

	public Referee findOne(final int refereeId) {
		Assert.isTrue(refereeId != 0);
		Referee result;
		result = this.refereeRepository.findOne(refereeId);
		return result;
	}
	public Referee create() {

		final UserAccount principal = LoginService.getPrincipal();

		Assert.isTrue(principal.getAuthorities().contains(Authority.ADMIN));
		final Referee result = new Referee();
		final Collection<Report> reportsWritten = new ArrayList<>();

		final UserAccount ua = new UserAccount();
		final Authority auth = new Authority();
		auth.setAuthority(Authority.REFEREE);

		ua.addAuthority(auth);

		result.setUserAccount(ua);
		result.setReportsWritten(reportsWritten);

		return result;
	}
	public Referee save(final Referee referee) {

		final UserAccount principal = LoginService.getPrincipal();

		Assert.isTrue(principal.getAuthorities().contains(Authority.ADMIN));

		return this.refereeRepository.save(referee);
	}

}
