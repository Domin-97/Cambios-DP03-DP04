
package repositories;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

	@Query("select a from Category a where a.name=?1")
	Category getCategoryByName(String name);

	@Query("select c from Category c where c.parentCategory=?1")
	Collection<Category> getChildren(Category parent);
}
