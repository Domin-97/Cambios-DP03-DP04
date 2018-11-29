

package domain;

import java.util.Collection;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

@Entity
@Access(AccessType.PROPERTY)
public class Endorser extends Actor {

	private Collection<Endorsement>	endorsementsByMe;
	private Collection<Endorsement>	endorsementsByOther;
	private double					score;


	// We need an insctance of Configuration
	public double getScore() {
		//		double score = 0;
		//		if (this.getEndorsementsByOther() != null && this.getEndorsementsByOther().size() != 0) {
		//			for (final Endorsement e : this.getEndorsementsByOther())
		//				s = s + e.getScore();
		//			s = s / this.getEndorsementsByOther().size();
		//		}
		return this.score;
	}

	public void setScore(final double score) {
		this.score = score;
	}

	@NotNull
	@OneToMany
	public Collection<Endorsement> getEndorsementsByMe() {
		return this.endorsementsByMe;
	}

	public void setEndorsementsByMe(final Collection<Endorsement> endorsementsByMe) {
		this.endorsementsByMe = endorsementsByMe;
	}

	@NotNull
	@OneToMany
	public Collection<Endorsement> getEndorsementsByOther() {
		return this.endorsementsByOther;
	}

	public void setEndorsementsByOther(final Collection<Endorsement> endorsementsByOther) {
		this.endorsementsByOther = endorsementsByOther;
	}

}
