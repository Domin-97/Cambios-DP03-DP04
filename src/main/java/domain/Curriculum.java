
package domain;

import java.util.Collection;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

@Entity
@Access(AccessType.PROPERTY)
public class Curriculum extends DomainEntity {

	private String							ticker;
	private String							fullname;
	private String							photo;
	private String							email;
	private String							phoneNumber;
	private String							linkedInProfile;
	private Collection<EducationRecord>		eduRecord;
	private Collection<ProfessionalRecord>	proRecord;
	private Collection<MiscellaneousRecord>	misRecord;
	private Collection<EndorserRecord>		endRecord;


	@NotBlank
	@Column(unique = true)
	public String getTicker() {
		return this.ticker;
	}

	public void setTicker(final String ticker) {
		this.ticker = ticker;
	}
	@NotBlank
	public String getFullname() {
		return this.fullname;
	}

	public void setFullname(final String fullname) {
		this.fullname = fullname;
	}
	@NotBlank
	@URL
	public String getPhoto() {
		return this.photo;
	}

	public void setPhoto(final String photo) {
		this.photo = photo;
	}
	@NotBlank
	@Email
	public String getEmail() {
		return this.email;
	}

	public void setEmail(final String email) {
		this.email = email;
	}
	@NotBlank
	public String getPhoneNumber() {
		return this.phoneNumber;
	}

	public void setPhoneNumber(final String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	@NotBlank
	@URL
	public String getlinkedInProfile() {
		return this.linkedInProfile;
	}

	public void setlinkedInProfile(final String linkedInProfile) {
		this.linkedInProfile = linkedInProfile;
	}
	@NotNull
	@OneToMany(cascade = CascadeType.ALL)
	public Collection<EducationRecord> getEduRecord() {
		return this.eduRecord;
	}

	public void setEduRecord(final Collection<EducationRecord> eduRecord) {
		this.eduRecord = eduRecord;
	}
	@NotNull
	@OneToMany(cascade = CascadeType.ALL)
	public Collection<ProfessionalRecord> getProRecord() {
		return this.proRecord;
	}

	public void setProRecord(final Collection<ProfessionalRecord> proRecord) {
		this.proRecord = proRecord;
	}
	@NotNull
	@OneToMany(cascade = CascadeType.ALL)
	public Collection<MiscellaneousRecord> getMisRecord() {
		return this.misRecord;
	}

	public void setMisRecord(final Collection<MiscellaneousRecord> misRecord) {
		this.misRecord = misRecord;
	}
	@NotNull
	@OneToMany(cascade = CascadeType.ALL)
	public Collection<EndorserRecord> getEndRecord() {
		return this.endRecord;
	}

	public void setEndRecord(final Collection<EndorserRecord> endRecord) {
		this.endRecord = endRecord;
	}

}
