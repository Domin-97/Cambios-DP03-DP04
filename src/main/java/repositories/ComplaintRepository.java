
package repositories;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.Complaint;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Integer> {

	// lista de compLaint sin referee asignado 
	@Query("select c from Complaint c where c NOT IN (select c from Complaint c join c.report r)")
	Collection<Complaint> getComplaintWithOutReport();
	//listas de complaint de handy worker 
	@Query("select c from Complaint c join c.fixUpTask fut join  fut.applications b where b.handyWorker = ?1")
	Collection<Complaint> getComplaintOfHandyWorker(int hwId);
	//Lista de complaint asignados a customer 
	@Query("select c from Complaint c join c.customer cus where cus.id=?1")
	Collection<Complaint> getComplaintOfCustomer(int cusId);
	//Complaints de una FixUpTask
	@Query("select c from Complaint c join c.fixUpTask fut where fut.id=?1")
	Collection<Complaint> findFixUpTaskApplications(int id);
}
