package de.beuth.clara.claraSoftware.domain;

import javax.persistence.Embeddable;
/**
 * A class representing a bank account containing IBAN and BIC implemented as an embeddable.
 * @author Ahmad Kasbah
 */
@Embeddable
public class Bankaccount {

	private String iban;
	private String bic;
	
	/**Necessary for Jackson*/
	public Bankaccount() {}

	/**
	 * creates a bank account object 
	 * @param iban - International Bank Account Number
	 * @param bic - Bank Identifier Code
	 */
	public Bankaccount(final String iban, final String bic) {
		this.iban = iban;
		this.bic = bic;
	}

	/**
	 * Getter for IBAN
	 * @return iban
	 */
	public String getIban() {
		return iban;
	}

	/**
	 * Getter for BIC
	 * @return bic
	 */
	public String getBic() {
		return bic;
	}
}
