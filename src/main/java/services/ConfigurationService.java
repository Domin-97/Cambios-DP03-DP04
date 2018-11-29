
package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import repositories.ConfigurationRepository;
import security.Authority;
import security.LoginService;
import security.UserAccount;
import domain.Configuration;

@Service
@Transactional
public class ConfigurationService {

	// Manage repository----------------------------------------------------------------------------
	@Autowired
	private ConfigurationRepository	configurationRepository;


	//Suporting services -------------------------------------------------------------------------------

	//constructors --------------------------------------------------------------------------------
	public ConfigurationService() {
		super();
	}
	//Simple crud method------------------------------------------------------------------------------
	public Collection<Configuration> findAll() {
		Collection<Configuration> result;
		result = this.configurationRepository.findAll();
		return result;
	}
	public Configuration findOne() {
		Configuration result;
		result = this.configurationRepository.findAll().get(0);
		return result;
	}

	public Configuration save(Configuration configuration) {

		Assert.notNull(configuration);

		UserAccount principal = LoginService.getPrincipal();
		Assert.notNull(principal);

		Authority auth = new Authority();
		auth.setAuthority(Authority.ADMIN);
		Assert.isTrue(principal.getAuthorities().contains(auth));

		return this.configurationRepository.save(configuration);
	}

	public Collection<String> findSpamWords() {
		List<String> result = this.configurationRepository.findSpamWords();
		Assert.notNull(result);

		return result;
	}

	public Collection<String> findPositiveWords() {
		List<String> result = this.configurationRepository.findPositiveWords();
		Assert.notNull(result);

		return result;

	}
	public Collection<String> findNegativeWords() {
		List<String> result = this.configurationRepository.findNegativeWords();
		Assert.notNull(result);

		return result;
	}

	public int findFinderTime() {
		int result = this.configurationRepository.findFinderTime();
		Assert.isTrue(result > 0);

		return result;
	}
	public  Collection<String> findCreditCardMakes() {
		 Collection<String> result = this.configurationRepository.findCreditCardMakes();
		Assert.isNull(result);

		return result;
	}
}
