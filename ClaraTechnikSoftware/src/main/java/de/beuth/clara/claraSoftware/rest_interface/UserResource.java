package de.beuth.clara.claraSoftware.rest_interface;


import de.beuth.clara.claraSoftware.domain.Address;
import de.beuth.clara.claraSoftware.domain.User;

/**Data about a user of CLARA. Usable as Data Transfer Object.
 * @author Ahmad Kasbah
 */
public class UserResource {

    /**Unique ID of the user.*/
    public Long id;
    
    /**Complete name of the user.*/
    public String username;
    public String password;
    public String honorifics;
    public String name;
    public String firstname;
    public Address address;

    /**Necessary for Jackson*/
	public UserResource() {}

    /**Constructs a UserResource with the data of the passed User entity.
     * @param entity User the user that will be parsed into a Resource 
     */
    public UserResource(final User entity) {
    	this.id = entity.getId();
    	this.honorifics = entity.getHonorifics();
        this.name = entity.getName();
        this.firstname = entity.getFirstname();
        this.username = entity.getUsername();
        this.password = entity.getPassword();
        this.address = entity.getAdress();
    }
}