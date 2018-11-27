
package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import repositories.ReportRepository;
import security.Authority;
import security.LoginService;
import security.UserAccount;
import domain.Complaint;
import domain.Note;
import domain.Report;

@Service
@Transactional
public class ReportService {

	// Manage repository----------------------------------------------------------------------------
	@Autowired
	private ReportRepository	reportRepository;


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

		final UserAccount principal = LoginService.getPrincipal();

		Assert.isTrue(principal.getAuthorities().contains(Authority.REFEREE));
		final Report result = new Report();

		final Date moment = new Date();
		final String description = "";
		final Collection<String> attachments = new ArrayList<>();
		final boolean isFinal = false;
		final Collection<Note> notes = new ArrayList<>();
		final Complaint complaint = new Complaint();

		result.setAttachments(attachments);
		result.setComplaint(complaint);
		result.setDescription(description);

		result.setIsFinal(isFinal);
		result.setMoment(moment);
		result.setNotes(notes);

		return result;
	}
	public Report save(final Report report) {

		final UserAccount principal = LoginService.getPrincipal();

		Assert.isTrue(principal.getAuthorities().contains(Authority.REFEREE));
		Assert.isTrue(principal.getAuthorities().contains(Authority.CUSTOMER));
		Assert.isTrue(principal.getAuthorities().contains(Authority.HANDYWORKER));
		Assert.notNull(report);

		return this.reportRepository.save(report);
	}

}
