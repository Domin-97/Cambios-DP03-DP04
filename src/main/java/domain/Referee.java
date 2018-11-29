
package domain;

import java.util.Collection;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

@Entity
@Access(AccessType.PROPERTY)
public class Referee extends Actor {

	private Collection<Report>	reportsWritten;


	@NotNull
	@OneToMany
	public Collection<Report> getReportsWritten() {
		return this.reportsWritten;
	}

	public void setReportsWritten(final Collection<Report> reportsWritten) {
		this.reportsWritten = reportsWritten;
	}

}
