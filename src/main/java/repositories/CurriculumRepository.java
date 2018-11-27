
package repositories;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.Curriculum;

@Repository
public interface CurriculumRepository extends JpaRepository<Curriculum, Integer> {

	//Coger toda la curricula segun los  ids de los handyWorkers
	@Query("select hw.curricula from HandyWorker hw where hw.id=?1")
	Collection<Curriculum> findCurriculaByHandyWorkerId(int handyWorkerId);
	//Conseguir curriculum mediante el ID de los record
	@Query("select c from Curriculum c join c.eduRecord e where e.id = ?1")
	Curriculum findCurriculumByEduRecordId(int eduRecordId);

	@Query("select c from Curriculum c join c.proRecord p where p.id = ?1")
	Curriculum findCurriculumByProRecordId(int proRecordId);

	@Query("select c from Curriculum c join c.misRecord m where m.id = ?1")
	Curriculum findCurriculumByMisRecordId(int misRecordId);

	@Query("select c from Curriculum c join c.endRecord en where en.id = ?1")
	Curriculum findCurriculumByEndRecordId(int endRecordId);
}
