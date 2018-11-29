package repositories;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.Referee;
import domain.Report;

@Repository
public interface RefereeRepository extends JpaRepository<Referee, Integer> {

	@Query("select c.reportsWritten from Referee c ")
	Collection<Report> getReportsWritten();
	@Query("select c from Referee c join c.reportsWritten fut where fut.id= ?1 ")
	Collection<Referee> findRefereeByReport(int reportId);

}
