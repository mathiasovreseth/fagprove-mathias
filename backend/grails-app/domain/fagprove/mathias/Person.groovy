package fagprove.mathias

import enums.PersonType
import grails.util.Holders
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.springframework.security.crypto.bcrypt.BCrypt

@EqualsAndHashCode(includes='email')
@ToString(includes='email', includeNames=true, includePackage=false)
class Person implements Serializable {

    private static final long serialVersionUID = 1

    transient springSecurityService

    String email
    String name
    String description
    String jobRole
    String phoneNumber
    String company
    String region

    Boolean registrationReceived

    String password

    Date dateCreated
    Date lastUpdated

    PersonType personType

    static mappedBy = [
            committees: "members"
    ]

    static belongsTo = Committee

    static hasMany = [
            committees: Committee
    ]

    Set<Role> getAuthorities() {
        PersonRole.findAllByPerson(this)*.role
    }

    static transients = ['springSecurityService']

    static constraints = {
        email email: true, unique: true
        name blank:false, nullable: false
        description nullable: true, blank: true
        password blank: false, nullable: true
        personType nullable: false
        jobRole nullable: true
        phoneNumber nullable: true
        company nullable: true
        region nullable: true
        registrationReceived nullable: true
    }

    static mapping = {
        id generator:'sequence', params:[sequence:'person_seq']
        email index:'person_email_idx'
        personType index:'person_type_idx'
        description type:'text'
    }

    def beforeInsert() {
        this.email = email.toLowerCase()
        encodePassword()
    }

    def beforeUpdate() {
        this.email = email.toLowerCase()
        if(isDirty('password')){
            encodePassword()
        }
    }

    protected void encodePassword() {
        if(this.password) {
            this.password = encode(this.password)
        }
    }

    boolean validateLogin(String password) {
        if(this.enabled) {
            if(this.password &&
                    validate(password, this.password)){
                return true
            }
        }
        false
    }

    String getPassword() {
        return password
    }

    boolean getEnabled() {
        return true
    }

    boolean getAccountExpired() {
        return false
    }

    boolean getAccountLocked() {
        return false
    }

    boolean getPasswordExpired() {
        return false
    }

    private static final int STRENGTH =
            Integer.valueOf(Holders.getConfig().getProperty("bcrypt.strength"));

    static String encode(String pw){
        return BCrypt.hashpw(pw, BCrypt.gensalt(STRENGTH));
    }

    static boolean validate(String candidate, String encodedHash){
        return BCrypt.checkpw(candidate, encodedHash);
    }
}

