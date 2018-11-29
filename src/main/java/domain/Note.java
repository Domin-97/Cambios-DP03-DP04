
package domain;

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;

@Entity
@Access(AccessType.PROPERTY)
public class Note extends DomainEntity {

	private Date	moment;
	private String	hwComment;
	private String	cusComment;
	private String	refComment;


	@NotNull
	@Past
	@Temporal(TemporalType.TIMESTAMP)
	public Date getMoment() {
		return this.moment;
	}

	public void setMoment(final Date moment) {
		this.moment = moment;
	}

	public String getHwComment() {
		return this.hwComment;
	}

	public void setHwComment(final String hwComment) {
		this.hwComment = hwComment;
	}

	public String getCusComment() {
		return this.cusComment;
	}

	public void setCusComment(final String cusComment) {
		this.cusComment = cusComment;
	}

	public String getRefComment() {
		return this.refComment;
	}

	public void setRefComment(final String refComment) {
		this.refComment = refComment;
	}

}
