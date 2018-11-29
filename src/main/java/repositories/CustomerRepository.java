
package repositories;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {

	//The listing of costumers who have published at least 10% more fixuptask 
	//than the avg, ordered by number of applications

	@Query("select distinct c from Customer c join c.fixUpTasks fut where c.fixUpTasks.size >(select avg(c.fixUpTasks.size)*1.1 from Customer c) order by fut.applications.size")
	Collection<Customer> getCustomersWithMoreFixUpTask();

	//The top-three customers in terms of complaints.

	@Query("select c from Customer c join c.fixUpTasks fut group by c order by sum(fut.complaints.size) desc")
	Collection<Customer> getCustomersWithMoreComplaints();

}
