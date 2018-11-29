
package domain;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Embeddable;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;

import org.hibernate.validator.constraints.NotBlank;

@Embeddable
@Access(AccessType.PROPERTY)
public class Money {

	private double	amount;
	private String	currency;
	private double	vatTax;


	public Money() {
		super();

	}

	@Digits(fraction = 2, integer = 9)
	@Min(0)
	public double getAmount() {
		return this.amount;
	}

	public void setAmount(final double amount) {
		this.amount = amount;
	}
	@NotBlank
	public String getCurrency() {
		return this.currency;
	}

	public void setCurrency(final String currency) {
		this.currency = currency;
	}

	public double getVatTax() {
		return this.vatTax;
	}

	public void setVatTax(final double vatTax) {
		this.vatTax = vatTax;
	}

}
