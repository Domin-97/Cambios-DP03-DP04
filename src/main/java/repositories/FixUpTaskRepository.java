
package repositories;

import java.util.Collection;
import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.FixUpTask;

@Repository
public interface FixUpTaskRepository extends JpaRepository<FixUpTask, Integer> {

	@Query("select fut from FixUpTask fut where fut.ticker like ?1 OR fut.description like ?1 OR fut.address like ?1")
	Collection<FixUpTask> findByKeyWork(String key);

	@Query("select fut from FixUpTask fut join fut.warranty w where w.id=?1")
	Collection<FixUpTask> findByWarranty(int warrantyId);

	//TODO est� mal asi
	@Query("select fut from FixUpTask fut where fut.startDate=?1 AND fut.endDate=?2")
	Collection<FixUpTask> findByDates(Date start, Date end);

	@Query("select fut from FixUpTask fut join fut.maxPrice mp where mp.amount>?1 AND mp.amount<?2")
	Collection<FixUpTask> findByMoney(int amount1, int amount2);

	@Query("select fut from FixUpTask fut join fut.customer c where c.id=?1")
	Collection<FixUpTask> findByCustomer(int id);
}