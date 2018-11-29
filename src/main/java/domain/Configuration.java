
package domain;

import java.util.Collection;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;
import org.hibernate.validator.constraints.URL;

@Entity
@Access(AccessType.PROPERTY)
public class Configuration extends DomainEntity {

	private String						systemName;
	private String						banner;
	private String						enWelcomeMessage;
	private String						esWelcomeMessage;
	private Collection<String>			spamWords;
	private double						vat;
	private String						countryCode;
	private Collection<String>			creditCardMakes;
	private int							finderTime;
	private int							finderResult;
	private static Collection<String>	positiveWords;
	private static Collection<String>	negativeWords;


	@NotBlank
	public String getSystemName() {
		return this.systemName;
	}

	public void setSystemName(final String systemName) {
		this.systemName = systemName;
	}
	@NotBlank
	@URL
	public String getBanner() {
		return this.banner;
	}

	public void setBanner(final String banner) {
		this.banner = banner;
	}
	@NotBlank
	public String getEnWelcomeMessage() {
		return this.enWelcomeMessage;
	}

	public void setEnWelcomeMessage(final String enWelcomeMessage) {
		this.enWelcomeMessage = enWelcomeMessage;
	}
	@NotBlank
	public String getEsWelcomeMessage() {
		return this.esWelcomeMessage;
	}

	public void setEsWelcomeMessage(final String esWelcomeMessage) {
		this.esWelcomeMessage = esWelcomeMessage;
	}
	@NotNull
	@ElementCollection
	public Collection<String> getSpamWords() {
		return this.spamWords;
	}

	public void setSpamWords(final Collection<String> spamWords) {
		this.spamWords = spamWords;
	}

	public double getVat() {
		return this.vat;
	}

	public void setVat(final Double vat) {
		this.vat = vat;
	}
	@NotBlank
	public String getCountryCode() {
		return this.countryCode;
	}

	public void setCountryCode(final String countryCode) {
		this.countryCode = countryCode;
	}
	@NotNull
	@ElementCollection
	public Collection<String> getCreditCardMakes() {
		return this.creditCardMakes;
	}

	public void setCreditCardMakes(final Collection<String> creditCardMakes) {
		this.creditCardMakes = creditCardMakes;
	}

	@Range(min = 1, max = 24)
	public int getFinderTime() {
		return this.finderTime;
	}

	public void setFinderTime(final int finderTime) {
		this.finderTime = finderTime;
	}

	@Range(min = 10, max = 100)
	public int getFinderResult() {
		return this.finderResult;
	}

	public void setFinderResult(final int finderResult) {
		this.finderResult = finderResult;
	}
	@NotNull
	@ElementCollection
	public Collection<String> getPositiveWords() {
		return Configuration.positiveWords;
	}

	public void setPositiveWords(final Collection<String> positiveWords) {
		Configuration.positiveWords = positiveWords;
	}
	@NotNull
	@ElementCollection
	public Collection<String> getNegativeWords() {
		return Configuration.negativeWords;
	}

	public void setNegativeWords(final Collection<String> negativeWords) {
		Configuration.negativeWords = negativeWords;
	}

}
