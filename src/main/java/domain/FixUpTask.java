
package domain;

import java.util.Collection;
import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;

import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.core.WhitespaceTokenizerFactory;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilterFactory;
import org.apache.lucene.analysis.ngram.EdgeNGramFilterFactory;
import org.apache.lucene.analysis.ngram.NGramFilterFactory;
import org.apache.lucene.analysis.snowball.SnowballPorterFilterFactory;
import org.apache.lucene.analysis.standard.StandardTokenizerFactory;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.AnalyzerDef;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Parameter;
import org.hibernate.search.annotations.Store;
import org.hibernate.search.annotations.TokenFilterDef;
import org.hibernate.search.annotations.TokenizerDef;
import org.hibernate.validator.constraints.NotBlank;

@Indexed
@Entity
@Access(AccessType.PROPERTY)
//@AnalyzerDef(name = "customAnalyzer", tokenizer = @TokenizerDef(factory = WhitespaceTokenizerFactory.class),
//filters = {
////	@TokenFilterDef(factory = NGramFilterFactory.class,
////			params = {
////		@Parameter(name = "minGramSize", value = "1"),
////		@Parameter(name = "maxGramSize", value = "40")
////	}),
//    @TokenFilterDef(factory = ASCIIFoldingFilterFactory.class), // Replace accented characeters by their simpler counterpart (è => e, etc.)
//    @TokenFilterDef(factory = LowerCaseFilterFactory.class)
//    
//})
public class FixUpTask extends DomainEntity {

	private String					ticker;
	private Date					publishedMoment;
	private String					description;
	private String					address;
	private Money					maxPrice;
	private Date					startDate;
	private Date					endDate;

	private Customer				customer;
	private Collection<Complaint>	complaints;
	private Collection<Application>	applications;
	private Collection<Phase>		workPlan;
	private Warranty				warranty;
	private Category				category;


	@Field
//	@Field(index=Index.YES, analyze=Analyze.YES, store=Store.NO,analyzer = @Analyzer(definition = "customAnalyzer"))
	@NotBlank
	@Column(unique = true)
	public String getTicker() {
		return this.ticker;
	}

	public void setTicker(final String ticker) {
		this.ticker = ticker;
	}

	@NotNull
	@Past
	@Temporal(TemporalType.TIMESTAMP)
	public Date getPublishedMoment() {
		return this.publishedMoment;
	}

	public void setPublishedMoment(final Date publishedMoment) {
		this.publishedMoment = publishedMoment;
	}

	@Field
//	@Field(index=Index.YES, analyze=Analyze.YES, store=Store.NO, analyzer = @Analyzer(definition = "customAnalyzer"))
	@NotBlank
	public String getDescription() {
		return this.description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	@Field
//	@Field(index=Index.YES, analyze=Analyze.YES, store=Store.NO, analyzer = @Analyzer(definition = "customAnalyzer"))
	@NotBlank
	public String getAddress() {
		return this.address;
	}

	public void setAddress(final String address) {
		this.address = address;
	}

	@NotNull
	@Valid
	public Money getmaxPrice() {
		return this.maxPrice;
	}

	public void setmaxPrice(final Money maxPrice) {
		this.maxPrice = maxPrice;
	}

	@NotNull
	@Temporal(TemporalType.DATE)
	public Date getStartDate() {
		return this.startDate;
	}

	public void setStartDate(final Date startDate) {
		this.startDate = startDate;
	}

	@NotNull
	@Temporal(TemporalType.DATE)
	public Date getEndDate() {
		return this.endDate;
	}

	public void setEndDate(final Date endDate) {
		this.endDate = endDate;
	}

	@NotNull
	@Valid
	@ManyToOne(optional = false)
	public Customer getCustomer() {
		return this.customer;
	}

	public void setCustomer(final Customer customer) {
		this.customer = customer;
	}

	@Valid
	@OneToMany
	@NotNull
	public Collection<Phase> getWorkPlan() {
		return this.workPlan;
	}

	public void setWorkPlan(final Collection<Phase> workPlan) {
		this.workPlan = workPlan;
	}

	@NotNull
	@Valid
	@ManyToOne(optional = false)
	public Warranty getWarranty() {
		return this.warranty;
	}

	public void setWarranty(final Warranty warranty) {
		this.warranty = warranty;
	}

	@NotNull
	@Valid
	@ManyToOne(optional = false)
	public Category getCategory() {
		return this.category;
	}

	public void setCategory(final Category category) {
		this.category = category;
	}

	@NotNull
	@OneToMany(mappedBy = "fixUpTask")
	public Collection<Application> getApplications() {
		return this.applications;
	}

	public void setApplications(final Collection<Application> applications) {
		this.applications = applications;
	}

	@NotNull
	@OneToMany(mappedBy = "fixUpTask")
	public Collection<Complaint> getComplaints() {
		return this.complaints;
	}

	public void setComplaints(final Collection<Complaint> complaints) {
		this.complaints = complaints;
	}

}
