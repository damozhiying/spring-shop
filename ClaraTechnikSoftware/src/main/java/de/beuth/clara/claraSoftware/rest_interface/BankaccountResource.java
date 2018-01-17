package de.beuth.clara.claraSoftware.rest_interface;

import de.beuth.clara.claraSoftware.domain.Bankaccount;

/**Data about a bankaccount of an user of CLARA. Usable as Data Transfer Object.
 * @author Ahmad Kasbah
 */
public class BankaccountResource {
	
	public Long id;
	
	public String iban;
	public String bic; 
	
	public BankaccountResource() {}
	
	public BankaccountResource(final Long userId, final Bankaccount bankaccount) {
		this.id = userId;
		this.iban = bankaccount.getIban();
		this.bic = bankaccount.getBic();
	}

}
