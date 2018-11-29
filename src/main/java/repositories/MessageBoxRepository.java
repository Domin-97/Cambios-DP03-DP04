package repositories;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.MessageBox;

@Repository
public interface MessageBoxRepository extends JpaRepository<MessageBox, Integer> {
	
	@Query("select a.messageBoxes from Actor a where a.id=?1")
	Collection<MessageBox> findMessageBoxesByActorId(int actorId);
	
	@Query("select mb from MessageBox mb join mb.messages m where m.id=?1")
	Collection<MessageBox> findByMessageId(int messageId);

	@Query("select mb from Actor a join a.messageBoxes mb where mb.name like ?1 AND a.id=?2" )
	MessageBox findMessageBoxByNameAndActorId(String name, int actorId);
	
}
