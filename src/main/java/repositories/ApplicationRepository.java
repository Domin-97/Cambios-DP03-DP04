package repositories;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.Application;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Integer>{
	
	//Applications de una FixUpTask
	@Query("select a from Application a join a.fixUpTask fut where fut.id=?1")
	Collection<Application> findFixUpTaskApplications(int id);
	
	//Aplications de un HandyWorker
	@Query("select a from Application a join a.handyWorker hw where hw.id=?1")
	Collection<Application> findHandyWorkerApplications(int id);

}
