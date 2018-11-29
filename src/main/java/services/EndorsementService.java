
package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import domain.Actor;
import domain.Application;
import domain.Customer;
import domain.Endorsement;
import domain.Endorser;
import domain.FixUpTask;
import domain.HandyWorker;

import repositories.EndorsementRepository;
import security.Authority;
import security.LoginService;
import security.UserAccount;

@Service
@Transactional
public class EndorsementService {

	// Managed repository-----------------------------------

	@Autowired
	private EndorsementRepository	endorsementRepository;

	// Supporting services----------------------------------

	@Autowired
	private ActorService			actorService;

	@Autowired
	private HandyWorkerService		handyWorkerService;

	@Autowired
	private CustomerService			customerService;

	@Autowired
	private ConfigurationService	configurationService;

	@Autowired
	private EndorserService			endorserService;

	// Constructor------------------------------------------

	public EndorsementService() {
		super();
	}

	// CRUD methods-----------------------------------------

	public Endorsement findOne(int id) {
		//Se entiende que las valoraciones son públicas y todo el mundo puede verlas
		Assert.isTrue(id != 0);
		Endorsement result;

		result = this.endorsementRepository.findOne(id);
		Assert.notNull(result);
		return result;
	}

	public Endorsement create() {
		Endorsement result = new Endorsement();

		//El usuario debe estar logeado como customer o handyworker
		UserAccount principal = LoginService.getPrincipal();
		Authority auth = new Authority();
		auth.setAuthority(Authority.HANDYWORKER);
		Authority auth2 = new Authority();
		auth2.setAuthority(Authority.CUSTOMER);
		Assert.isTrue(principal.getAuthorities().contains(auth) || principal.getAuthorities().contains(auth2));

		result.setComment("");
		result.setMoment(new Date(System.currentTimeMillis() - 1000));
		result.setScore(0);

		return result;
	}

	public Endorsement save(Endorsement endorsement, Endorser receptor) {
		Assert.notNull(endorsement);
		//El receptor puede ser nulo si se va a editar el endorsement

		//El usuario debe estar logeado como customer o handyworker
		UserAccount principal = LoginService.getPrincipal();
		Authority auth = new Authority();
		auth.setAuthority(Authority.HANDYWORKER);
		Authority auth2 = new Authority();
		auth2.setAuthority(Authority.CUSTOMER);
		Assert.isTrue(principal.getAuthorities().contains(auth) || principal.getAuthorities().contains(auth2));

		Endorsement result = new Endorsement();

		//Si está guardando un nuevo endorsement
		if (endorsement.getId() == 0) {
			//En este caso el receptor no puede ser nulo
			Assert.notNull(receptor);

			if (principal.getAuthorities().contains(auth)) {
				//En este caso es el handyworker el que está creando el endorsement
				//y el otro usuario debe ser un customer
				Assert.isTrue(receptor.getUserAccount().getAuthorities().contains(auth2));
				HandyWorker handyCreator = this.handyWorkerService.findOne(this.actorService.findByUserAccountId(principal.getId()).getId());
				Assert.notNull(handyCreator);
				Customer cusReceptor = this.customerService.findOne(receptor.getId());
				Assert.notNull(cusReceptor);

				//Tenemos que ver en cuántas fixuptask han colaborado
				int contTasks = 0;
				for (FixUpTask fut : cusReceptor.getFixUpTasks()) {
					for (Application a : fut.getApplications()) {
						if (handyCreator.getApplications().contains(a) && a.getStatus().equals("ACCEPTED")) {
							contTasks++;
						}
					}
				}

				//Si no tienen ninguna en común, no se puede crear el endorsement
				Assert.isTrue(contTasks > 0);

				//Contamos cuántos endorsements ha creado el handyworker sobre el customer
				int contEnd = 0;
				for (Endorsement e : handyCreator.getEndorsementsByMe()) {
					if (cusReceptor.getEndorsementsByOther().contains(e)) {
						contEnd++;
					}
				}
				//Debe poder hacer tantos endorsements como fixuptasks tengan en común
				Assert.isTrue(contTasks > contEnd);

				Assert.isTrue(endorsement.getComment() != null && !endorsement.getComment().equals(""));
				result.setComment(endorsement.getComment());
				result.setMoment(new Date(System.currentTimeMillis() - 1000));
				result.setScore(this.computeScore(result.getComment()));

				result = this.endorsementRepository.save(result);
				Assert.notNull(result);

				//Añadimos el endorsement a sus usuarios
				this.handyWorkerService.updateCreatedEndorsements(result, handyCreator);
				this.customerService.updateObtainedEndorsements(result, cusReceptor);

			} else {
				//Y en este es el customer el que la crea
				//teniendo el receptor que ser un handyworker
				Assert.isTrue(receptor.getUserAccount().getAuthorities().contains(auth));
				Customer cusCreator = this.customerService.findOne(this.actorService.findByUserAccountId(principal.getId()).getId());
				Assert.notNull(cusCreator);
				HandyWorker handyReceptor = this.handyWorkerService.findOne(receptor.getId());
				Assert.notNull(handyReceptor);

				//Tenemos que ver en cuántas fixuptask han colaborado
				int contTasks = 0;
				for (FixUpTask fut : cusCreator.getFixUpTasks()) {
					for (Application a : fut.getApplications()) {
						if (handyReceptor.getApplications().contains(a) && a.getStatus().equals("ACCEPTED")) {
							contTasks++;
						}
					}
				}

				//Si no tienen ninguna en común, no se puede crear el endorsement
				Assert.isTrue(contTasks > 0);

				//Contamos cuántos endorsements ha creado el handyworker sobre el customer
				int contEnd = 0;
				for (Endorsement e : handyReceptor.getEndorsementsByMe()) {
					if (cusCreator.getEndorsementsByOther().contains(e)) {
						contEnd++;
					}
				}
				//Debe poder hacer tantos endorsements como fixuptasks tengan en común
				Assert.isTrue(contTasks > contEnd);

				Assert.isTrue(endorsement.getComment() != null && !endorsement.getComment().equals(""));
				result.setComment(endorsement.getComment());
				result.setMoment(new Date(System.currentTimeMillis() - 1000));
				result.setScore(this.computeScore(result.getComment()));

				result = this.endorsementRepository.save(result);
				Assert.notNull(result);

				//Añadimos el endorsement a sus usuarios
				this.handyWorkerService.updateCreatedEndorsements(result, handyReceptor);
				this.customerService.updateObtainedEndorsements(result, cusCreator);
			}
		} else {
			//En este caso va a modificarse un endorsement
			//Un endorser solo puede modificar los endorsements que él mismo ha creado,
			//no los que han creado sobre él

			//El receptor debe ser nulo en este caso
			Assert.isNull(receptor);

			Endorsement old = this.endorsementRepository.findOne(endorsement.getId());

			//Vemos si el usuario es de tipo handyworker o customer
			if (principal.getAuthorities().contains(auth)) {
				HandyWorker user = this.handyWorkerService.findOne(this.actorService.findByUserAccountId(principal.getId()).getId());

				//El usuario debe tener el endorsement a modificar en su lista de endorsements creados
				Assert.isTrue(user.getEndorsementsByMe().contains(old));

				//Tan solo puede modificar su comentario
				Assert.isTrue((endorsement.getMoment() != null) && (endorsement.getMoment().equals(old.getMoment())));
				Assert.isTrue(endorsement.getScore() == old.getScore());
				Assert.isTrue(endorsement.getComment() != null);

				endorsement.setScore(this.computeScore(endorsement.getComment()));

				result = this.endorsementRepository.save(endorsement);
				Assert.notNull(result);

			} else {
				Customer user = this.customerService.findOne(this.actorService.findByUserAccountId(principal.getId()).getId());

				//El usuario debe tener el endorsement a modificar en su lista de endorsements creados
				Assert.isTrue(user.getEndorsementsByMe().contains(old));

				//Tan solo puede modificar su comentario
				Assert.isTrue((endorsement.getMoment() != null) && (endorsement.getMoment().equals(old.getMoment())));
				Assert.isTrue(endorsement.getScore() == old.getScore());
				Assert.isTrue(endorsement.getComment() != null);

				endorsement.setScore(this.computeScore(endorsement.getComment()));

				result = this.endorsementRepository.save(endorsement);
				Assert.notNull(result);
			}

		}
		return result;
	}

	public void delete(Endorsement endorsement) {
		Assert.notNull(endorsement);

		//Comprobamos que exista el endorsement
		Endorsement end = this.endorsementRepository.findOne(endorsement.getId());
		Assert.notNull(end);

		//Los endorsements solo pueden ser borrados por un endorser
		UserAccount principal = LoginService.getPrincipal();
		Authority auth = new Authority();
		auth.setAuthority(Authority.HANDYWORKER);
		Authority auth2 = new Authority();
		auth2.setAuthority(Authority.CUSTOMER);
		Assert.isTrue(principal.getAuthorities().contains(auth) || principal.getAuthorities().contains(auth2));

		if (principal.getAuthorities().contains(auth)) {
			//En este caso es el handyworker el que quiere borrar un endorsement			
			HandyWorker handy = this.handyWorkerService.findOne(this.actorService.findByUserAccountId(principal.getId()).getId());

			//Comprobamos que el endorsement a borrar fue creado por él
			Assert.isTrue(handy.getEndorsementsByMe().contains(end));

			Endorser endorser = this.endorserService.findReceptorOfEndorsement(end.getId());
			Assert.notNull(endorser);
			Customer cus = this.customerService.findOne(endorser.getId());
			Assert.notNull(cus);

			this.handyWorkerService.deleteCreatedEndorsements(end, handy);
			this.customerService.deleteObtainedEndorsements(end, cus);
	
			//Borramos el endorsement
			this.endorsementRepository.delete(end);
		} else {
			//Un customer quiere borrar un endorsement
			Customer customer = this.customerService.findOne(this.actorService.findByUserAccountId(principal.getId()).getId());

			//Comprobamos que el endorsement a borrar fue creado por él
			Assert.isTrue(customer.getEndorsementsByMe().contains(end));
			
			Endorser endorser = this.endorserService.findReceptorOfEndorsement(end.getId());
			Assert.notNull(endorser);
			HandyWorker handy = this.handyWorkerService.findOne(endorser.getId());
			Assert.notNull(handy);
			
			this.customerService.deleteCreatedEndorsements(end, customer);
			this.handyWorkerService.deleteObtainedEndorsements(end, handy);

			//Borramos el endorsement
			this.endorsementRepository.delete(end);
		}
	}


	public void flush() {
		this.endorsementRepository.flush();
	}

	//Other business Methods---------------------------------------------------

	public Collection<Endorsement> findEndorsementsCreated(int endorserId) {
		//Los endorsements creados por un endorser son públicos
		Assert.isTrue(endorserId != 0);

		//Hay que comprobar que el id pasado corresponde a un actor existente
		Actor a = this.actorService.findOne(endorserId);
		Assert.notNull(a);

		//Hay que comprobar que el actor es un endorser (handyworker o customer
		Authority auth = new Authority();
		auth.setAuthority(Authority.HANDYWORKER);
		Authority auth2 = new Authority();
		auth2.setAuthority(Authority.CUSTOMER);
		Assert.isTrue(a.getUserAccount().getAuthorities().contains(auth) || a.getUserAccount().getAuthorities().contains(auth2));

		Collection<Endorsement> result;
		result = this.endorsementRepository.findEndorsementsCreated(endorserId);
		Assert.notNull(result);
		return result;
	}

	public Collection<Endorsement> findEndorsementsObtained(int endorserId) {
		//Los endorsements obtenidos por un endorser son públicos
		Assert.isTrue(endorserId != 0);

		//Hay que comprobar que el id pasado corresponde a un actor existente
		Actor a = this.actorService.findOne(endorserId);
		Assert.notNull(a);

		//Hay que comprobar que el actor es un endorser (handyworker o customer
		Authority auth = new Authority();
		auth.setAuthority(Authority.HANDYWORKER);
		Authority auth2 = new Authority();
		auth2.setAuthority(Authority.CUSTOMER);
		Assert.isTrue(a.getUserAccount().getAuthorities().contains(auth) || a.getUserAccount().getAuthorities().contains(auth2));

		Collection<Endorsement> result;
		result = this.endorsementRepository.findEndorsementsObtained(endorserId);
		Assert.notNull(result);
		return result;
	}

	private double computeScore(String comment) {
		List<String> negativeWords = new ArrayList<String>(this.configurationService.findNegativeWords());
		List<String> positiveWords = new ArrayList<String>(this.configurationService.findPositiveWords());

		double result = 0;
		comment = comment.toLowerCase();

		double pBuenas = 0;
		double pMalas = 0;
		for (String s : positiveWords)
			if (comment.contains(s))
				pBuenas++;
		for (String s : negativeWords)
			if (comment.contains(s))
				pMalas++;
		if ((pBuenas + pMalas) != 0)
			result = (pBuenas - pMalas) / (pBuenas + pMalas);

		return result;
	}
}
