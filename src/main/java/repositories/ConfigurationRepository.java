
package repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.Configuration;

@Repository
public interface ConfigurationRepository extends JpaRepository<Configuration, Integer> {
	
	@Query("select sw from Configuration c join c.spamWords sw")
	List<String> findSpamWords();
	
	@Query("select nw from Configuration c join c.negativeWords nw")
	List<String> findNegativeWords();
	
	@Query("select pw from Configuration c join c.positiveWords pw")
	List<String> findPositiveWords();

	@Query("select c.finderTime from Configuration c")
	int findFinderTime();
}
