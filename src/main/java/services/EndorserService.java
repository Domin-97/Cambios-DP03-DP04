package services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import domain.Endorser;

import repositories.EndorserRepository;

@Service
@Transactional
public class EndorserService {

	// Managed repository-----------------------------------

	@Autowired
	private EndorserRepository	endorserRepository;


	// Supporting services----------------------------------

	// Constructor------------------------------------------

	public EndorserService() {
		super();
	}

	// CRUD methods-----------------------------------------

	public Endorser findOne(int id) {
		//Cualquiera puede llamar a este método
		Assert.isTrue(id != 0);

		Endorser result;
		result = this.endorserRepository.findOne(id);
		Assert.notNull(result);
		return result;
	}

	//Other business Methods---------------------------------------------------

	public Endorser findCreatorOfEndorsement(int endorsementId) {
		Assert.isTrue(endorsementId != 0);
		//No hace falta estar logeado
		Endorser result;
		result = this.endorserRepository.findCreatorOfEndorsement(endorsementId);
		Assert.notNull(result);
		return result;
	}
	
	public Endorser findReceptorOfEndorsement(int endorsementId) {
		Assert.isTrue(endorsementId != 0);
		//No hace falta estar logeado
		Endorser result;
		result = this.endorserRepository.findReceptorOfEndorsement(endorsementId);
		Assert.notNull(result);
		return result;
	}

}
