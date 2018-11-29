
package repositories;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.Tutorial;

@Repository
public interface TutorialRepository extends JpaRepository<Tutorial, Integer> {

	@Query("select t from Tutorial t where t.handyWorker.id= ?1")
	Collection<Tutorial> getHandyWorkerTutorials(int handyWorkerId);

	@Query("select t from Tutorial t join t.sections s where s.id = ?1 ")
	Tutorial findTutorialBySectionId(int sectionId);
}
