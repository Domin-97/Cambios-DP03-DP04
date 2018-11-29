
package repositories;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.Endorsement;

@Repository
public interface EndorsementRepository extends JpaRepository<Endorsement, Integer> {

	//Endorsements realizados por un endorser
	@Query("select e.endorsementsByMe from Endorser e where e.id=?1")
	Collection<Endorsement> findEndorsementsCreated(int id);

	//Endorsements realizados por otros en referencia al endorser
	@Query("select e.endorsementsByOther from Endorser e where e.id=?1")
	Collection<Endorsement> findEndorsementsObtained(int id);
}
