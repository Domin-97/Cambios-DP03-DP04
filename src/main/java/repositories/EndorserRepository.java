package repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.Endorser;

@Repository
public interface EndorserRepository extends JpaRepository<Endorser, Integer>{
	
	//Endorser creador de un endorsement
	@Query("select e from Endorser e join e.endorsementsByMe ebm where ebm.id=?1")
	Endorser findCreatorOfEndorsement(int endorsementId);
	
	//Endorser receptor de un endorsement
	@Query("select e from Endorser e join e.endorsementsByOther ebo where ebo.id=?1")
	Endorser findReceptorOfEndorsement(int endorsementId);
}
