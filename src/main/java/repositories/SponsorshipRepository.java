
package repositories;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.Sponsorship;

@Repository
public interface SponsorshipRepository extends JpaRepository<Sponsorship, Integer> {

	//CONSEGUIR LOS SPONSORSHIP DEL SPONSOR 
	@Query("select sp.sponsorships from Sponsor sp where sp.id=?1")
	Collection<Sponsorship> findSponsorshipsBySponsorId(int sponsorId);
}