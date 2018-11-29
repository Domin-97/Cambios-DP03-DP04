
package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import repositories.ReportRepository;
import security.Authority;
import security.LoginService;
import security.UserAccount;
import domain.Complaint;
import domain.Customer;
import domain.Finder;
import domain.HandyWorker;
import domain.Note;
import domain.Referee;
import domain.Report;

@Service
@Transactional
public class ReportService {

	// Manage repository----------------------------------------------------------------------------
	@Autowired
	private ReportRepository	reportRepository;
	@Autowired
	private NoteService			noteService;
	@Autowired
	private ActorService		actorService;
	@Autowired
	private HandyWorkerService	handyWorkerService;


	//Suporting services -------------------------------------------------------------------------------

	//constructors --------------------------------------------------------------------------------
	public ReportService() {
		super();
	}
	//Simple crud method------------------------------------------------------------------------------
	public Collection<Report> findAll() {
		Collection<Report> result;
		result = this.reportRepository.findAll();
		return result;
	}

	public Report findOne(final int reportId) {
		Assert.isTrue(reportId != 0);
		Report result;
		result = this.reportRepository.findOne(reportId);
		return result;
	}
	public Report create() {

		UserAccount principal = LoginService.getPrincipal();
		Authority auth = new Authority();
		auth.setAuthority(Authority.REFEREE);
		Assert.isTrue((principal.getAuthorities().contains(auth)));
		Report result = new Report();
		final String description = "";
		final Collection<String> attachments = new ArrayList<>();
		final boolean isFinal = false;
		final Collection<Note> notes = new ArrayList<>();
		final Complaint complaint = new Complaint();

		result.setAttachments(attachments);
		result.setComplaint(complaint);
		result.setDescription(description);

		result.setIsFinal(isFinal);
		result.setMoment(new Date(System.currentTimeMillis() - 1000));
		notes.add(this.noteService.create());
		result.setNotes(notes);

		return result;
	}
	public Report save(Report report) {

		UserAccount principal = LoginService.getPrincipal();
		if (report.getId() == 0) {
			Report old = new Report();

			Assert.notNull(report);
			Assert.notNull(old);

			Authority auth = new Authority();
			auth.setAuthority(Authority.REFEREE);
			Assert.isTrue((principal.getAuthorities().contains(auth)));

			report = this.reportRepository.save(report);
		} else {
			Report old = this.reportRepository.findOne(report.getId());

			Authority auth = new Authority();
			auth.setAuthority(Authority.REFEREE);

			Authority auth2 = new Authority();
			auth2.setAuthority(Authority.CUSTOMER);

			Authority auth3 = new Authority();
			auth3.setAuthority(Authority.HANDYWORKER);
			Assert.isTrue((principal.getAuthorities().contains(auth)) || (principal.getAuthorities().contains(auth2) || (principal.getAuthorities().contains(auth3))));

			Assert.notNull(report);
			Assert.notNull(old);
			//comprobamos que solon actualizan customer handyWorker o Customer
			//solo pueden updatearse las notas , asi que comprobamos que el resto no cambia 

			Assert.isTrue(old.getIsFinal() == report.getIsFinal());
			Assert.isTrue(old.getAttachments().equals(report.getAttachments()));
			Assert.isTrue(old.getComplaint().equals(report.getComplaint()));
			Assert.isTrue(old.getDescription().equals(report.getDescription()));
			Assert.isTrue(old.getMoment().equals(report.getMoment()));
			//actualizamos notas 

			// comprobaciones 
			if (principal.getAuthorities().contains(auth)) {
				Referee ref = (Referee) this.actorService.findByUserAccountId(principal.getId());				
				Assert.isTrue((ref.getReportsWritten().contains(report)));
			}else if (principal.getAuthorities().contains(auth3)) {
				HandyWorker hw = (HandyWorker) this.actorService.findByUserAccountId(principal.getId());
				Assert.isTrue(this.reportRepository.findHandyWorkerByReport(report.getId()).contains(hw));
			}else if (principal.getAuthorities().contains(auth2)){
				
				Customer cus = (Customer) this.actorService.findByUserAccountId(principal.getId());
				Assert.isTrue(this.reportRepository.findCustomerByReport(report.getId()).equals(cus));
			}
			if(!report.getIsFinal())
				Assert.isTrue(old.getNotes().equals(report.getNotes()));
		}
		report = this.reportRepository.save(report);

		return report;
	}
	public Collection<Referee> findRefereeByReport(int reportId) {
		Collection<Referee> result = new ArrayList<>();

		result = this.findRefereeByReport(reportId);

		return result;

	}
	public Customer findCustomerByReport(int reportId) {
		Customer result;

		result = this.findCustomerByReport(reportId);

		return result;

	}
	public Collection<HandyWorker> findHandyWorkerByReport(int reportId) {
		Collection<HandyWorker> result = new ArrayList<>();

		result = this.findHandyWorkerByReport(reportId);

		return result;

	}
	public Report findReportByNoteId(int noteId) {

		return this.findReportByNoteId(noteId);

	}
}
