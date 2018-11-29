
package repositories;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.FixUpTask;
import domain.Phase;

@Repository
public interface PhaseRepository extends JpaRepository<Phase, Integer> {

	@Query("select ph from HandyWorker hw join hw.applications ap join ap.fixUpTask fut join fut.workPlan ph where ap.status='ACCEPTED' AND hw.id=?1")
	Collection<Phase> findPhasesFromAcceptedApplications(int hwId);

	@Query("select fut from HandyWorker hw join hw.applications ap join ap.fixUpTask fut join fut.workPlan ph where ap.status='ACCEPTED' AND hw.id=?1 AND ph.id=?2")
	FixUpTask findFixUpTaskfromAPhase(int hwId, int phId);
}
