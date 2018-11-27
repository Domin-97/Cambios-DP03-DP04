package utilities;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.query.dsl.QueryBuilder;

import utilities.internal.ConsoleReader;
import utilities.internal.SchemaPrinter;

import domain.FixUpTask;

public class Search {

	public static void main(String[] args) throws Throwable {
		int option = 0;
		Scanner sc = new Scanner(System.in);
		do{
			System.out.println("\n");
			System.out.println("*********************************");
			System.out.println("*  Please, enter a number:      *");
			System.out.println("* 1: To index entities          *");
			System.out.println("* 2: To search                  *");
			System.out.println("* 3: To exit                    *");
			System.out.println("*********************************");

			try{
			option = sc.nextInt();
			}catch(InputMismatchException a){
				System.out.println("Please, enter a valid number");
				option = 0;
				sc.next();
			}
			switch (option) {
			case 1:
				indexer();
				break;
			
			case 2:
				searcher();
				break;
				
			case 3:
				
				System.out.println("Bye!");
				break;

			default:
				if(option!=0)
					System.out.println("Please enter a valid number");
				break;
			}
			
		}while(option!=3);
		
		sc.close();
		
	}
	
	private static void indexer(){
		EntityManagerFactory factory = Persistence.createEntityManagerFactory("Acme-HandyWorker");
		EntityManager manager = factory.createEntityManager();


		FullTextEntityManager fullTextEntityManager =
		    org.hibernate.search.jpa.Search.getFullTextEntityManager(manager);
		
		try {
			fullTextEntityManager.createIndexer().startAndWait();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		factory.close();
	}
	
	
	private static void searcher() throws Throwable{
		final EntityManagerFactory factory = Persistence.createEntityManagerFactory("Acme-HandyWorker");
		
		final EntityManager manager = factory.createEntityManager();
		
		final FullTextEntityManager fullTextEntityManager =
		    org.hibernate.search.jpa.Search.getFullTextEntityManager(manager);
				
		manager.getTransaction().begin();

		// create native Lucene query unsing the query DSL
		
		final QueryBuilder qb = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(FixUpTask.class)
				.get();
		
		
		ConsoleReader cr = new ConsoleReader();
		
		System.out.println("\n");
		System.out.println("enter exit to close the searcher.");
		System.out.println("\n");

		
		while(true){
		
		try{
			String match = cr.readLine();
			
			if(match!=null && match.equals("exit"))break;
				
		final org.apache.lucene.search.Query luceneQuery = qb.keyword()
				.onFields("description","ticker","address").matching(match).createQuery();

		// wrap Lucene query in a javax.persistence.Query
		
		final javax.persistence.Query jpaQuery =
		    fullTextEntityManager.createFullTextQuery(luceneQuery, FixUpTask.class);

		// execute search
		
		@SuppressWarnings("unchecked")
		List<FixUpTask> result = jpaQuery.getResultList();
		
		if(result!=null && result.size()>0)
			SchemaPrinter.print(result);
		else
			System.out.println("The term has not been found");
		
		
		}catch (Exception e) {
			System.out.println("Enter a valid string");
					}
		}
		
	//Finishing the application
		
	manager.getTransaction().commit();
	manager.close();
	factory.close();
	

	}
	
}