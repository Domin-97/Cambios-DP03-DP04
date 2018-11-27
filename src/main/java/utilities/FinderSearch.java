
package utilities;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.query.dsl.QueryBuilder;

import domain.Finder;
import domain.FixUpTask;

public class FinderSearch {

	//SOLO HABRÍA QUE USARLO SI TUVIÉSEMOS ALGO YA EN LA BASE DE DATOS ANTES DE IMPLEMENTAR EL TINGLADO
	public static void indexer() {
		EntityManagerFactory factory = Persistence.createEntityManagerFactory("Acme-HandyWorker");
		EntityManager manager = factory.createEntityManager();

		FullTextEntityManager fullTextEntityManager = org.hibernate.search.jpa.Search.getFullTextEntityManager(manager);

		try {
			fullTextEntityManager.createIndexer().startAndWait();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		factory.close();
	}

	public static List<FixUpTask> finderSearch(Finder finder) throws Throwable {
		//Sacamos los parámetros
		String keyWord = finder.getKeyword();
		String category = finder.getCategory();

		EntityManagerFactory factory = Persistence.createEntityManagerFactory("Acme-HandyWorker");

		EntityManager manager = factory.createEntityManager();

		FullTextEntityManager fullTextEntityManager = org.hibernate.search.jpa.Search.getFullTextEntityManager(manager);

		manager.getTransaction().begin();

		// create native Lucene query unsing the query DSL

		QueryBuilder qb = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(FixUpTask.class).get();

		final org.apache.lucene.search.Query luceneQuery = qb.bool()
			.should(qb.keyword().onFields("description", "ticker", "address").matching(keyWord).createQuery())
			.should(qb.keyword().onField("category.name").matching(category).createQuery())
			.createQuery();
		
		// wrap Lucene query in a javax.persistence.Query

		final javax.persistence.Query jpaQuery = fullTextEntityManager.createFullTextQuery(luceneQuery, FixUpTask.class);

		// execute search

		@SuppressWarnings("unchecked")
		List<FixUpTask> result = jpaQuery.getResultList();

		return result;
	}

}
