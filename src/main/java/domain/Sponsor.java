
package domain;

import java.util.Collection;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

@Entity
@Access(AccessType.PROPERTY)
public class Sponsor extends Actor {

	private Collection<Sponsorship>	Sponsorships;


	@NotNull
	@OneToMany
	public Collection<Sponsorship> getSponsorships() {
		return this.Sponsorships;
	}

	public void setSponsorships(final Collection<Sponsorship> sponsorships) {
		this.Sponsorships = sponsorships;
	}

}
