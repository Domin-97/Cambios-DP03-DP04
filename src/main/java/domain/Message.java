
package domain;

import java.util.Collection;
import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.validator.constraints.NotBlank;

@Entity
@Access(AccessType.PROPERTY)
public class Message extends DomainEntity {

	private Date				sendDate;
	private String				subject;
	private String				body;
	private String				priority;
	private Collection<String>	tags;
	private Actor				sender;
	private Collection<Actor>	recipients;

	public static final String	HIGH	= "HIGH";
	public static final String	NEUTRAL	= "NEUTRAL";
	public static final String	LOW		= "LOW";

	@NotFound(action = NotFoundAction.IGNORE)
	@Valid
	@ManyToOne(optional = false)
	public Actor getSender() {
		return this.sender;
	}

	public void setSender(final Actor sender) {
		this.sender = sender;
	}

	@NotNull
	@ManyToMany
	public Collection<Actor> getRecipients() {
		return this.recipients;
	}

	public void setRecipients(final Collection<Actor> recipients) {
		this.recipients = recipients;
	}

	@Past
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	public Date getSendDate() {
		return this.sendDate;
	}
	public void setSendDate(final Date sendDate) {
		this.sendDate = sendDate;
	}
	@NotBlank
	public String getSubject() {
		return this.subject;
	}
	public void setSubject(final String subject) {
		this.subject = subject;
	}
	@NotBlank
	public String getBody() {
		return this.body;
	}
	public void setBody(final String body) {
		this.body = body;
	}
	@NotBlank
	@Pattern(regexp = "^" + Message.HIGH + "|" + Message.NEUTRAL + "|" + Message.LOW + "$")
	public String getPriority() {
		return this.priority;
	}
	public void setPriority(final String priority) {
		this.priority = priority;
	}
	@NotNull
	@ElementCollection
	public Collection<String> getTags() {
		return this.tags;
	}

	public void setTags(final Collection<String> tags) {
		this.tags = tags;
	}

}
