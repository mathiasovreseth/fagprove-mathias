package fagprove.mathias

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@EqualsAndHashCode(includes='authority')
@ToString(includes='authority', includeNames=true, includePackage=false)
class Role implements Serializable {

    private static final long serialVersionUID = 1

    String authority
    String name
    String description

    static constraints = {
        authority blank: false, unique: true
        name nullable: true
        description nullable: true
    }

    static mapping = {
        cache true
        id generator:'sequence', params:[sequence:'role_seq']
    }
}