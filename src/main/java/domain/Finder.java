
package domain;

import java.util.Collection;
import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;

@Entity
@Access(AccessType.PROPERTY)
public class Finder extends DomainEntity {

	private String					keyword;
	private String					category;
	private String					warranty;
	private double					minPrice;
	private double					maxPrice;
	private Date					minDate;
	private Date					maxDate;
	private Date					lastUpdate;

	private Collection<FixUpTask>	fixuptaskStored;


	public String getKeyword() {
		return this.keyword;
	}

	public void setKeyword(final String keyword) {
		this.keyword = keyword;
	}

	public String getCategory() {
		return this.category;
	}

	public void setCategory(final String category) {
		this.category = category;
	}

	public String getWarranty() {
		return this.warranty;
	}

	public void setWarranty(final String warranty) {
		this.warranty = warranty;
	}

	@Min(0)
	public double getMinPrice() {
		return this.minPrice;
	}

	public void setMinPrice(final double minPrice) {
		this.minPrice = minPrice;
	}

	public double getMaxPrice() {
		return this.maxPrice;
	}

	public void setMaxPrice(final double maxPrice) {
		this.maxPrice = maxPrice;
	}

	@Temporal(TemporalType.DATE)
	public Date getMinDate() {
		return this.minDate;
	}

	public void setMinDate(final Date minDate) {
		this.minDate = minDate;
	}

	@Temporal(TemporalType.DATE)
	public Date getMaxDate() {
		return this.maxDate;
	}

	public void setMaxDate(final Date maxDate) {
		this.maxDate = maxDate;
	}

	@NotNull
	@Past
	@Temporal(TemporalType.TIMESTAMP)
	public Date getLastUpdate() {
		return this.lastUpdate;
	}

	public void setLastUpdate(final Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	@NotNull
	@ManyToMany
	public Collection<FixUpTask> getFixuptaskStored() {
		return this.fixuptaskStored;
	}

	public void setFixuptaskStored(final Collection<FixUpTask> fixuptaskStored) {
		this.fixuptaskStored = fixuptaskStored;
	}

}
