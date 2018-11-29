
package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import repositories.WarrantyRepository;
import security.Authority;
import security.LoginService;
import security.UserAccount;
import domain.Warranty;

@Service
@Transactional
public class WarrantyService {

	//Managed repositories

	@Autowired
	private WarrantyRepository	warrantyRepository;


	//Supporting service
	// Constructors

	public WarrantyService() {
		super();
	}

	//Simple CRUD methods

	public Warranty create() {
		//Check that is an administrator
		UserAccount userAccount;
		userAccount = LoginService.getPrincipal();
		Assert.notNull(userAccount);
		final Authority auth = new Authority();
		auth.setAuthority(Authority.ADMIN);
		Assert.isTrue(userAccount.getAuthorities().contains(auth));

		Warranty result;

		result = new Warranty();

		result.setFinalMode(false);

		return result;

	}

	public Warranty save(final Warranty warranty) {
		Assert.notNull(warranty);
		Warranty result;
		//Check that is an administrator
		UserAccount userAccount;
		userAccount = LoginService.getPrincipal();
		final Authority auth = new Authority();
		auth.setAuthority(Authority.ADMIN);
		Assert.isTrue(userAccount.getAuthorities().contains(auth));
		//Check if is new or an update
		if (warranty.getId() != 0) {
			final Warranty old = this.warrantyRepository.findOne(warranty.getId());
			Assert.notNull(old);
			Assert.isTrue(!old.getFinalMode());
			result = this.warrantyRepository.save(warranty);
		} else
			result = this.warrantyRepository.save(warranty);

		return result;
	}

	public Collection<Warranty> findAll() {
		Collection<Warranty> result;
		result = this.warrantyRepository.findAll();
		return result;
	}

	public Warranty findOne(final int warrantyId) {
		Assert.isTrue(warrantyId != 0);
		Warranty result;
		result = this.warrantyRepository.findOne(warrantyId);
		return result;
	}

	public void delete(final Warranty warranty) {
		Assert.notNull(warranty);
		//Check that is an admin
		UserAccount userAccount;
		userAccount = LoginService.getPrincipal();
		final Authority auth = new Authority();
		auth.setAuthority(Authority.ADMIN);
		Assert.isTrue(userAccount.getAuthorities().contains(auth));
		final Warranty old = this.warrantyRepository.findOne(warranty.getId());
		Assert.notNull(old);
		Assert.isTrue(!old.getFinalMode());

		this.warrantyRepository.delete(warranty);

	}

	//Other methods

	public List<Warranty> findWarrantiesFinalMode() {
		final List<Warranty> result = new ArrayList<Warranty>();
		result.addAll(this.warrantyRepository.findWarrantiesFinalMode());
		return result;
	}

	public void flush() {
		this.warrantyRepository.flush();
	}
}
