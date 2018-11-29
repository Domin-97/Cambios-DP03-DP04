
package repositories;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.Customer;
import domain.HandyWorker;
import domain.Referee;
import domain.Report;

@Repository
public interface ReportRepository extends JpaRepository<Report, Integer> {
	@Query("select c from Customer c join c.fixUpTasks fix join fix.complaints com join com.report g where g.id = ?1")
	Customer findCustomerByReport(int reportId);
	@Query("select c from Referee c join c.reportsWritten co where co.id = ?1")
	Collection<Referee> findRefereeByReport(int reportId);
	@Query("select h from HandyWorker h join h.applications ap join ap.fixUpTask f join f.complaints com join com.report re where re.id = ?1")
	Collection<HandyWorker> findHandyWorkerByReport(int reportId);
	@Query("select r from Referee ref join ref.reportsWritten r join r.notes no where no.id= ?1 ")
	Report findReportByNoteId(int noteId);
}
