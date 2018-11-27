package services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import domain.Finder;
import domain.FixUpTask;
import domain.HandyWorker;

import repositories.FinderRepository;
import security.Authority;
import security.LoginService;
import security.UserAccount;
import utilities.FinderSearch;

@Service
@Transactional
public class FinderService {

	// Managed repository-----------------------------------
	@Autowired
	private FinderRepository		finderRepository;

	// Supporting services----------------------------------

	@Autowired
	private HandyWorkerService		handyWorkerService;

	@Autowired
	private ActorService			actorService;

	@Autowired
	private ConfigurationService	configurationService;


	// Constructor------------------------------------------

	public FinderService() {
		super();
	}

	// CRUD methods-----------------------------------------

	public Finder create() {
		Finder result = new Finder();

		result.setKeyword("");
		result.setCategory("");
		result.setWarranty("");
		result.setMinPrice(0.);
		result.setMaxPrice(0.);
		result.setMinDate(new Date());
		result.setMaxDate(new Date());
		result.setLastUpdate(new Date());
		result.setFixuptaskStored(new ArrayList<FixUpTask>());

		return result;
	}

	public Finder findOne(int id) {
		Assert.isTrue(id != 0);

		UserAccount principal = LoginService.getPrincipal();
		Assert.notNull(principal);

		// Vemos si el que intenta acceder es un handyworker
		Authority auth = new Authority();
		auth.setAuthority(Authority.HANDYWORKER);
		Assert.isTrue(principal.getAuthorities().contains(auth));

		// Comprobamos que no devuelva un finder nulo
		Finder result;
		result = this.finderRepository.findOne(id);
		Assert.notNull(result);

		// Vemos si el finder pertenece al handyworker
		HandyWorker owner = this.handyWorkerService.findOne(this.actorService.findByUserAccountId(principal.getId()).getId());

		Assert.isTrue(result.getId() == owner.getFinder().getId());

		return result;
	}

	public Finder save(Finder finder) {
		Assert.notNull(finder);
		Finder result;

		//SOLO SE USA AL CREAR EL HANDYWORKER
		if (finder.getId() == 0) {
			result = this.finderRepository.save(finder);
		} else {
			Finder old = this.finderRepository.findOne(finder.getId());
			Assert.notNull(old);

			List<String> categoryNames = this.finderRepository.findCategoryNames();
			List<String> warrantyTitles = this.finderRepository.findWarrantyNames();

			//Hay que ver si el nombre de la categoría existe en nuestra base de datos
			if (!categoryNames.contains(finder.getCategory())) {
				finder.setCategory("");
			}
			//Hay que ver si el título de la garantía está en nuestra base de datos
			if (!warrantyTitles.contains(finder.getWarranty())) {
				finder.setWarranty("");
			}

			int horas = this.configurationService.findFinderTime();

			Date dt = old.getLastUpdate();
			Calendar c = Calendar.getInstance();
			c.setTime(dt);
			c.add(Calendar.HOUR, horas);
			dt = c.getTime();

			finder.setLastUpdate(old.getLastUpdate());

			if (old.getCategory() == finder.getCategory() &&
				old.getKeyword() == finder.getKeyword() &&
				old.getMaxDate() == finder.getMaxDate() &&
				old.getMaxPrice() == finder.getMaxPrice() &&
				old.getMinDate() == finder.getMinDate() &&
				old.getMinPrice() == finder.getMinPrice() &&
				old.getWarranty() == finder.getWarranty() &&
				dt.before(new Date(System.currentTimeMillis()))) {

				finder.setFixuptaskStored(old.getFixuptaskStored());
			} else {

				try {
					List<FixUpTask> search = FinderSearch.finderSearch(finder);
					finder.setFixuptaskStored(search);
					finder.setLastUpdate(new Date(System.currentTimeMillis() - 1000));
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					finder.setFixuptaskStored(old.getFixuptaskStored());
					e.printStackTrace();
				}
			}

			result = this.finderRepository.save(finder);
			//HAY QUE VER EL TEMA DEL TIEMPO Y EL NÚMERO DE RESULTADOS
		}

		return result;
	}

	// HAY QUE VER CUÁNDO SE PUEDE BORRAR

	// OTROS MÉTODOS-------------------------------------

	public Finder findFinderByHandyWorkerId(int id) {
		Assert.isTrue(id != 0);

		UserAccount principal = LoginService.getPrincipal();
		Assert.notNull(principal);

		// Vemos si el que intenta acceder es un handyworker
		Authority auth = new Authority();
		auth.setAuthority(Authority.HANDYWORKER);
		Assert.isTrue(principal.getAuthorities().contains(auth));

		// Comprobamos que no devuelva un finder nulo
		Finder result;
		result = this.finderRepository.findFinderByHandyWorkerId(id);
		Assert.notNull(result);

		return result;

	}

}
