package de.beuth.clara.claraSoftware.domain;

import javax.persistence.Embeddable;
/**
 * A class representing an adress containing information about postcode, city, street and housenumber of a person.
 * Implemented as an embeddable.
 * @author Lena Noerenberg
 */
@Embeddable
public class Address {
    
	
    private Integer postcode;
    private String city;
    private String street;
    private String housenumber;
    
    /**Necessary for Jackson*/
	public Address() {}
    
	/**
	 * Creates an Adress object with postal code, city, street and house number inforamation.
	 * @param postcode Integer - Postal code
	 * @param city String - name of the city
	 * @param street String - name of the street
	 * @param housenumber - house number information
	 */
    public Address(final Integer postcode,final String city,final String street,final String housenumber) {
	this.postcode = postcode;
	this.city = city;
	this.street = street;
	this.housenumber = housenumber;
    }

    /**
     * Getter for postcode
     * @return postcode of this instance
     */
    public Integer getPostcode() {
        return postcode;
    }

    /**
     * Getter for city
     * @return city of this instance
     */
    public String getCity() {
        return city;
    }

    /**
     * Getter for Street
     * @return street of this instance
     */
    public String getStreet() {
        return street;
    }

    /**
     * Getter for housenumber
     * @return house number of this intance
     */
    public String getHousenumber() {
        return housenumber;
    }
}
