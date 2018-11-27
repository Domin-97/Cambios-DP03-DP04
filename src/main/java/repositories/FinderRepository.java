
package repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.Category;
import domain.Finder;

@Repository
public interface FinderRepository extends JpaRepository<Finder, Integer> {

	@Query("select hw.finder from HandyWorker hw where hw.id=?1")
	Finder findFinderByHandyWorkerId(int id);

	@Query("select c.name from Category c")
	List<String> findCategoryNames();

	@Query("select w.title from Warranty w")
	List<String> findWarrantyNames();
	
	@Query("select c from Category c where c.name=?1")
	Category findCategoryByName();

}
