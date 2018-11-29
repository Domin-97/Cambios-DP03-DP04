
package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import domain.Application;
import domain.Curriculum;
import domain.Endorsement;
import domain.Finder;
import domain.HandyWorker;
import domain.MessageBox;
import domain.SocialProfile;
import domain.Tutorial;

import repositories.HandyWorkerRepository;
import security.Authority;
import security.LoginService;

import security.UserAccount;

@Service
@Transactional
public class HandyWorkerService {

	// Managed repository-----------------------------------

	@Autowired
	private HandyWorkerRepository	handyWorkerRepository;

	// Supporting services----------------------------------

	@Autowired
	private ActorService			actorService;

	@Autowired
	private FinderService			finderService;

	@Autowired
	private MessageBoxService		messageBoxService;


	// Constructor------------------------------------------

	public HandyWorkerService() {
		super();
	}

	// CRUD methods-----------------------------------------

	public HandyWorker create() {
		//NO PUEDE HABER NINGÚN USUARIO LOGEADO
		Authentication authentication;
		SecurityContext context;
		context = SecurityContextHolder.getContext();
		Assert.notNull(context);
		authentication = context.getAuthentication();
		Assert.isNull(authentication);

		HandyWorker result = new HandyWorker();

		//COSAS COMUNES DE ACTOR
		result.setName("");
		result.setMiddleName("");
		result.setSurname("");
		result.setPhoto("");
		result.setEmail("");
		result.setPhoneNumber("");
		result.setAddress("");
		result.setBanned(false);
		result.setSuspicious(false);
		result.setSocialProfiles(new ArrayList<SocialProfile>());
		List<MessageBox> systemBoxes = new ArrayList<MessageBox>(this.messageBoxService.createSystemMessageBoxes());
		result.setMessageBoxes(systemBoxes);

		//CUENTA
		UserAccount ua = new UserAccount();
		Authority auth = new Authority();
		auth.setAuthority(Authority.HANDYWORKER);
		ua.addAuthority(auth);
		result.setUserAccount(ua);

		//COSAS DE ENDORSER
		result.setEndorsementsByMe(new ArrayList<Endorsement>());
		result.setEndorsementsByOther(new ArrayList<Endorsement>());
		result.setScore(0.);

		//COSAS DE HANDYWORKER
		result.setMake("");
		result.setCurricula(new ArrayList<Curriculum>());
		result.setTutorials(new ArrayList<Tutorial>());
		result.setApplications(new ArrayList<Application>());
		result.setFinder(finderService.create());

		return result;
	}

	public Collection<HandyWorker> findAll() {
		Collection<HandyWorker> result;
		result = handyWorkerRepository.findAll();
		Assert.notNull(result);

		return result;
	}

	public HandyWorker findOne(int id) {
		Assert.isTrue(id != 0);

		HandyWorker result;
		result = handyWorkerRepository.findOne(id);
		Assert.notNull(result);

		return result;
	}

	public HandyWorker save(HandyWorker hw) {
		Assert.notNull(hw);
		HandyWorker result;

		if (hw.getId() == 0) {
			//NO PUEDE HABER NINGÚN USUARIO LOGEADO
			Authentication authentication;
			SecurityContext context;
			context = SecurityContextHolder.getContext();
			Assert.notNull(context);
			authentication = context.getAuthentication();
			Assert.isNull(authentication);

			Finder saved = this.finderService.save(hw.getFinder());
			hw.setFinder(saved);
			if (hw.getMake().equals("")) {
				hw.setMake(hw.getName() + hw.getSurname());
			}
			result = this.handyWorkerRepository.save(hw);
		} else {
			//SOLO EL PROPIO USUARIO Y UN ADMIN PUEDEN MODIFICARLO
			UserAccount principal = LoginService.getPrincipal();

			Authority auth = new Authority();
			auth.setAuthority(Authority.HANDYWORKER);
			Authority auth2 = new Authority();
			auth2.setAuthority(Authority.ADMIN);
			Assert.isTrue(principal.getAuthorities().contains(auth) || principal.getAuthorities().contains(auth2));

			//Hay que comprobar que exista el usuario
			HandyWorker old = this.handyWorkerRepository.findOne(hw.getId());
			Assert.notNull(old);

			//Si es el handyworker el que intenta modificar su perfil
			if (principal.getAuthorities().contains(auth)) {
				Assert.isTrue(this.actorService.findByUserAccountId(principal.getId()).getId() == hw.getId());

				//El propio handyworker no puede cambiarse los atributos suspicious y banned
				//Tampoco puede añadirse privilegios
				Assert.isTrue(hw.isSuspicious() == old.isSuspicious());
				Assert.isTrue(hw.isBanned() == old.isBanned());
				Assert.isTrue(hw.getUserAccount().getAuthorities().equals(old.getUserAccount().getAuthorities()));
			} else {
				//En este caso es un admin el que va a modificar al handyworker
				//Tan solo puede cambiar el nombre, la contraseña y los atributos booleanos

				Assert.isTrue((hw.getName() != null) && (hw.getName().equals(old.getName())));
				Assert.isTrue((hw.getName() != null) && (hw.getMiddleName().equals(old.getMiddleName())));
				Assert.isTrue((hw.getName() != null) && (hw.getSurname().equals(old.getSurname())));
				Assert.isTrue((hw.getName() != null) && (hw.getPhoto().equals(old.getPhoto())));
				Assert.isTrue((hw.getName() != null) && (hw.getEmail().equals(old.getEmail())));
				Assert.isTrue((hw.getName() != null) && (hw.getPhoneNumber().equals(old.getPhoneNumber())));
				Assert.isTrue((hw.getName() != null) && (hw.getAddress().equals(old.getAddress())));
				Assert.isTrue((hw.getName() != null) && (hw.getSocialProfiles().equals(old.getSocialProfiles())));
				Assert.isTrue((hw.getName() != null) && (hw.getMessageBoxes().equals(old.getMessageBoxes())));
				Assert.isTrue((hw.getName() != null) && (hw.getName().equals(old.getName())));

				Assert.isTrue((hw.getUserAccount().getAuthorities() != null) && (hw.getUserAccount().getAuthorities().equals(old.getUserAccount().getAuthorities())));

				Assert.isTrue((hw.getEndorsementsByMe() != null) && (hw.getEndorsementsByMe().equals(old.getEndorsementsByMe())));
				Assert.isTrue((hw.getEndorsementsByOther() != null) && (hw.getEndorsementsByOther().equals(old.getEndorsementsByOther())));
				Assert.isTrue(hw.getScore() == old.getScore());

				Assert.isTrue((hw.getMake() != null) && (hw.getMake().equals(old.getMake())));
				Assert.isTrue((hw.getCurricula() != null) && (hw.getCurricula().equals(old.getCurricula())));
				Assert.isTrue(hw.getTutorials() != null && (hw.getTutorials().equals(old.getTutorials())));
				Assert.isTrue((hw.getApplications() != null) && (hw.getApplications().equals(old.getApplications())));
				Assert.isTrue((hw.getFinder() != null) && (hw.getFinder().equals(old.getFinder())));
			}

			result = this.handyWorkerRepository.save(hw);
		}
		return result;
	}

	public void flush() {
		this.handyWorkerRepository.flush();
	}

	//Other business Methods---------------------------------------------------

	public void updateCreatedEndorsements(Endorsement endorsement, HandyWorker handy) {
		Assert.notNull(endorsement);
		Assert.notNull(handy);
		handy.getEndorsementsByMe().add(endorsement);
		this.handyWorkerRepository.save(handy);
	}

	public void updateObtainedEndorsements(Endorsement endorsement, HandyWorker handy) {
		Assert.notNull(endorsement);
		Assert.notNull(handy);
		handy.getEndorsementsByOther().add(endorsement);
		this.handyWorkerRepository.save(handy);
	}

	public void deleteCreatedEndorsements(Endorsement endorsement, HandyWorker handy) {
		Assert.notNull(endorsement);
		Assert.notNull(handy);
		handy.getEndorsementsByMe().remove(endorsement);
		this.handyWorkerRepository.save(handy);
	}

	public void deleteObtainedEndorsements(Endorsement endorsement, HandyWorker handy) {
		Assert.notNull(endorsement);
		Assert.notNull(handy);
		handy.getEndorsementsByOther().remove(endorsement);
		this.handyWorkerRepository.save(handy);
	}
}

