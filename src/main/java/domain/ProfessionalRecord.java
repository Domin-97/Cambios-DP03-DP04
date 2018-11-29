
package domain;

import java.util.Collection;
import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

@Entity
@Access(AccessType.PROPERTY)
public class ProfessionalRecord extends DomainEntity {

	private String				companyName;
	private Date				proStartDate;
	private Date				proEndDate;
	private String				role;
	private String				proAttachment;
	private Collection<String>	proComments;


	@NotBlank
	public String getCompanyName() {
		return this.companyName;
	}

	public void setCompanyName(final String companyName) {
		this.companyName = companyName;
	}

	@NotNull
	@Past
	@Temporal(TemporalType.DATE)
	public Date getProStartDate() {
		return this.proStartDate;
	}

	public void setProStartDate(final Date proStartDate) {
		this.proStartDate = proStartDate;
	}

	@Temporal(TemporalType.DATE)
	public Date getProEndDate() {
		return this.proEndDate;
	}

	public void setProEndDate(final Date proEndDate) {
		this.proEndDate = proEndDate;
	}
	@NotBlank
	public String getRole() {
		return this.role;
	}

	public void setRole(final String role) {
		this.role = role;
	}

	@URL
	public String getProAttachment() {
		return this.proAttachment;
	}

	public void setProAttachment(final String proAttachment) {
		this.proAttachment = proAttachment;
	}
	@NotNull
	@ElementCollection
	public Collection<String> getProComments() {
		return this.proComments;
	}

	public void setProComments(final Collection<String> proComments) {
		this.proComments = proComments;
	}

}
