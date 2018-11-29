
package repositories;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.Actor;
import domain.Administrator;
import domain.Customer;
import domain.HandyWorker;

@Repository
public interface AdministratorRepository extends JpaRepository<Administrator, Integer> {

	@Query("select a from Actor a where a.suspicious = true")
	Collection<Actor> getSuspiciousActors();

	//------ Queries del D03 ------

	//The average, the minimum, the maximum, and the standard deviation of the number of fix-up tasks per user.
	@Query("select avg(c.fixUpTasks.size),max(c.fixUpTasks.size),min(c.fixUpTasks.size),stddev(c.fixUpTasks.size) from Customer c")
	List<Double> getC1();

	//The average, the minimum, the maximum, and the standard deviation of the number of applications per fix-up task.
	@Query("select avg(fut.applications.size),max(fut.applications.size),min(fut.applications.size),stddev(fut.applications.size) from FixUpTask fut")
	List<Double> getC2();

	//The average, the minimum, the maximum, and the standard deviation of the maximum price of the fix-up tasks.
	@Query("select avg(fut.maxPrice.amount),max(fut.maxPrice.amount),min(fut.maxPrice.amount),stddev(fut.maxPrice.amount) from FixUpTask fut")
	List<Double> getC3();

	//The average, the minimum, the maximum, and the standard deviation of the price offered in the applications.
	@Query("select avg(a.offeredPrice.amount),max(a.offeredPrice.amount),min(a.offeredPrice.amount),stddev(a.offeredPrice.amount) from Application a")
	List<Double> getC4();

	//The ratio of pending applications.
	@Query("select (count(a)/(select count(b) from Application b)+0.0) from Application a where a.status='PENDING'")
	Double getC5();

	//The ratio of accepted applications.
	@Query("select (count(a)/(select count(b) from Application b)+0.0) from Application a where a.status='ACCEPTED'")
	Double getC6();

	//The ratio of rejected applications.
	@Query("select (count(a)/(select count(b) from Application b)+0.0) from Application a where a.status='REJECTED'")
	Double getC7();

	//The ratio of pending applications that cannot change its status because their time period’s elapsed.
	@Query("select (count(a)/(select count(b) from Application b)+0.0) from Application a join a.fixUpTask fut where a.status='PENDING' and fut.startDate<current_date")
	Double getC8();

	//The listing of customers who have published at least 10% more fix-up tasks than the average, ordered by number of applications.
	@Query("select distinct c from Customer c join c.fixUpTasks fut where c.fixUpTasks.size >(select avg(c.fixUpTasks.size)*1.1 from Customer c)order by fut.applications.size")
	Collection<Customer> getC9();

	//The listing of handy workers who have got accepted at least 10% more applications than the average, ordered by number of applications.
	@Query("select distinct hw from HandyWorker hw join hw.applications a where (select count(*) from Application a where a.handyWorker=hw and status='ACCEPTED')>(select count(a)/(select count(hw) from HandyWorker hw)*1.1 from Application a where a.status='ACCEPTED')")
	Collection<HandyWorker> getC10();

	//The minimum, the maximum, the average, and the standard deviation of the number of complaints per fix-up task.
	@Query("select avg(fut.complaints.size),max(fut.complaints.size),min(fut.complaints.size),stddev(fut.complaints.size) from FixUpTask fut")
	List<Double> getB1();

	//The minimum, the maximum, the average, and the standard deviation of the number of notes per referee report.
	@Query("select avg(r.notes.size),max(r.notes.size),min(r.notes.size),stddev(r.notes.size) from Report r")
	List<Double> getB2();

	//The ratio of fix-up tasks with a complaint.
	@Query("select (count(futa)/(select count(futb) from FixUpTask futb)+0.0) from FixUpTask futa where futa.complaints.size>0")
	Double getB3();

	//The top-three customers in terms of complaints.
	@Query("select c from Customer c join c.fixUpTasks fut group by c order by sum(fut.complaints.size) desc")
	Collection<Customer> getB4();

	//The top-three handy workers in terms of complaints.
	@Query("select hw from HandyWorker hw join hw.applications a join a.fixUpTask fut where a.status='ACCEPTED' group by hw order by sum(fut.complaints.size) desc")
	Collection<HandyWorker> getB5();

}


