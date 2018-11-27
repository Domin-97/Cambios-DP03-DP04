
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
	private NoteRepository	NoteRepository;


	//Suporting services -------------------------------------------------------------------------------

	//constructors --------------------------------------------------------------------------------
	public NoteService() {
		super();
	}
	//Simple crud method------------------------------------------------------------------------------
	public Collection<Note> findAll() {
		Collection<Note> result;
		result = this.NoteRepository.findAll();
		return result;
	}
	public Note findOne(final int noteId) {
		Assert.isTrue(noteId != 0);
		Note result;
		result = this.NoteRepository.findOne(noteId);
		return result;
	}
	public Note create() {

		final UserAccount principal = LoginService.getPrincipal();

		Assert.isTrue(principal.getAuthorities().contains(Authority.REFEREE));
		Assert.isTrue(principal.getAuthorities().contains(Authority.CUSTOMER));
		Assert.isTrue(principal.getAuthorities().contains(Authority.HANDYWORKER));

		Note result;
		final Date moment = new Date();
		final String hwComment = "";
		final String cusComment = "";
		final String refComment = "";

		result = new Note();
		result.setMoment(moment);
		result.setHwComment(hwComment);
		result.setCusComment(cusComment);
		result.setRefComment(refComment);

		return result;
	}
	public Note save(final Note note) {

		final UserAccount principal = LoginService.getPrincipal();

		Assert.isTrue(principal.getAuthorities().contains(Authority.REFEREE));
		Assert.isTrue(principal.getAuthorities().contains(Authority.CUSTOMER));
		Assert.isTrue(principal.getAuthorities().contains(Authority.HANDYWORKER));
		Assert.notNull(note);
		Assert.isTrue((note.getCusComment() != null) || (note.getRefComment() != null) || (note.getHwComment() != null));

		return this.NoteRepository.save(note);
	}

}
