
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
public class EducationRecord extends DomainEntity implements Cloneable{

	private String				eduTitle;
	private Date				eduStartDate;
	private Date				eduEndDate;
	private String				institution;
	private String				eduAttachment;
	private Collection<String>	comments;

	@Override
	public Object clone() {
		Object o = null;
		try {
			o = super.clone();
		} catch (CloneNotSupportedException ex) {
		}
		return o;
	}
	
	@NotBlank
	public String getEduTitle() {
		return this.eduTitle;
	}

	public void setEduTitle(final String eduTitle) {
		this.eduTitle = eduTitle;
	}

	@NotNull
	@Past
	@Temporal(TemporalType.DATE)
	public Date getEduStartDate() {
		return this.eduStartDate;
	}

	public void setEduStartDate(final Date eduStartDate) {
		this.eduStartDate = eduStartDate;
	}

	@Temporal(TemporalType.DATE)
	public Date getEduEndDate() {
		return this.eduEndDate;
	}

	public void setEduEndDate(final Date eduEndDate) {
		this.eduEndDate = eduEndDate;
	}
	@NotBlank
	public String getInstitution() {
		return this.institution;
	}

	public void setInstitution(final String institution) {
		this.institution = institution;
	}
	@URL
	public String getEduAttachment() {
		return this.eduAttachment;
	}

	public void setEduAttachment(final String eduAttachment) {
		this.eduAttachment = eduAttachment;
	}
	@NotNull
	@ElementCollection
	public Collection<String> getComments() {
		return this.comments;
	}

	public void setComments(final Collection<String> comments) {
		this.comments = comments;
	}

}
