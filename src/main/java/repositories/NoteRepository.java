package repositories;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.Note;
@Repository
public interface NoteRepository extends JpaRepository<Note, Integer> {
	@Query("select no from Referee r join r.reportsWritten w join w.notes no where r.id= ?1")
	Collection<Note> findNotesByRefereeId(int refereeId);

	@Query("select no from Customer c  join c.fixUpTasks f join f.complaints com join com.report re join re.notes no  where c.id= ?1")
	Collection<Note> findNotesByCustomerId(int customerId);
	@Query("select no from HandyWorker h  join h.applications ap join ap.fixUpTask  f join f.complaints com join com.report re join re.notes no where h.id=?1")
	Collection<Note> findNotesByHandyworkerId(int handyWorkerId);
}
