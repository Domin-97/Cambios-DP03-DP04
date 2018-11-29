
package services;

import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import repositories.NoteRepository;
import security.Authority;
import security.LoginService;
import security.UserAccount;

import domain.Note;

@Service
@Transactional
public class NoteService {

	// Manage repository----------------------------------------------------------------------------
	@Autowired
	private NoteRepository	noteRepository;


	//Suporting services -------------------------------------------------------------------------------

	@Autowired
	private ReportService reportService;
	
	//constructors --------------------------------------------------------------------------------
	public NoteService() {
		super();
	}
	//Simple crud method------------------------------------------------------------------------------
	public Collection<Note> findAll() {
		Collection<Note> result;
		result = this.noteRepository.findAll();
		return result;
	}
	public Note findOne(int noteId) {
		Assert.isTrue(noteId != 0);
		Note result;
		result = this.noteRepository.findOne(noteId);
		return result;
	}
	public Note create() {

		UserAccount principal = LoginService.getPrincipal();
		Assert.notNull(principal);

		Authority auth = new Authority();
		auth.setAuthority(Authority.HANDYWORKER);
		Authority auth2 = new Authority();
		auth2.setAuthority(Authority.CUSTOMER);
		Authority auth3 = new Authority();
		auth3.setAuthority(Authority.REFEREE);
		Assert.isTrue(principal.getAuthorities().contains(auth) || principal.getAuthorities().contains(auth2) || principal.getAuthorities().contains(auth3));

		Note result = new Note();
		result.setMoment(new Date(System.currentTimeMillis()));
		result.setCusComment("");
		result.setHwComment("");
		result.setRefComment("");

		return result;
	}
	public Note save(Note note) {
		Assert.notNull(note);
		UserAccount principal = LoginService.getPrincipal();
		Assert.notNull(principal);

		Authority authHw = new Authority();
		authHw.setAuthority(Authority.HANDYWORKER);

		Authority authCus = new Authority();
		authCus.setAuthority(Authority.CUSTOMER);

		Authority authRef = new Authority();
		authRef.setAuthority(Authority.REFEREE);
		Assert.isTrue((principal.getAuthorities().contains(authHw) || (principal.getAuthorities().contains(authCus)) || (principal.getAuthorities().contains(authRef))));

		Note result;

		Assert.isTrue((note.getCusComment() != null) || (note.getRefComment() != null) || (note.getHwComment() != null));

		
		//comprobar que los comenatrios estan hecho por el actor que esta registrado 
		if ((principal.getAuthorities().contains(authHw))) {
			Assert.isTrue(note.getCusComment().equals(""));
			Assert.isTrue(note.getRefComment().equals(""));
			result = this.noteRepository.save(note);
			
		}

		else if ((principal.getAuthorities().contains(authCus))) {
			Assert.isTrue(note.getHwComment().equals(""));
			Assert.isTrue(note.getRefComment().equals(""));
			result = this.noteRepository.save(note);
			

		} else{
			Assert.isTrue(note.getCusComment().equals(""));
			Assert.isTrue(note.getHwComment().equals(""));
			result = this.noteRepository.save(note);
		}

		return result;
	}
	public Collection<Note> findNotesByRefereeId(int refId) {
		Collection<Note> result;
		result = this.noteRepository.findNotesByRefereeId(refId);
		return result;
	}
	public Collection<Note> findNotesByCustomerId(int cusId) {

		Collection<Note> result;
		result = this.noteRepository.findNotesByRefereeId(cusId);
		return result;
	}
	public Collection<Note> findNotesByHandyworkerId(int hwId) {

		Collection<Note> result;
		result = this.noteRepository.findNotesByRefereeId(hwId);
		return result;
	}

}
