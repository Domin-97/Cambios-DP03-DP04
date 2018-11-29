
package domain;

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;

import org.hibernate.validator.constraints.NotBlank;

@Entity
@Access(AccessType.PROPERTY)
public class Endorsement extends DomainEntity {

	private Date	moment;
	private String	comment;
	private double	score;


	@NotNull
	@Past
	@Temporal(TemporalType.TIMESTAMP)
	public Date getMoment() {
		return this.moment;
	}

	public void setMoment(final Date moment) {
		this.moment = moment;
	}

	@NotBlank
	public String getComment() {
		return this.comment;
	}

	public void setComment(final String comment) {
		this.comment = comment;
	}

	// We need an insctance of Configuration
	public double getScore() {
		//		double score = 0.0;
		//		double pBuenas = 0;
		//		double pMalas = 0;
		//		for (final String s : Configuration.getPositiveWords())
		//			if (this.comment.toLowerCase().contains(s))
		//				pBuenas++;
		//		for (final String s : Configuration.getNegativeWords())
		//			if (this.comment.toLowerCase().contains(s))
		//				pMalas++;
		//		if (pBuenas + pMalas != 0)
		//			score = (pBuenas - pMalas) / (pBuenas + pMalas);
		return this.score;
	}

	public void setScore(final double score) {
		this.score = score;
	}

}
