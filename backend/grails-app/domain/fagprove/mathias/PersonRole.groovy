package fagprove.mathias

import grails.gorm.DetachedCriteria
import groovy.transform.ToString
import org.apache.commons.lang3.builder.HashCodeBuilder

@ToString(cache=true, includeNames=true, includePackage=false)
class PersonRole implements Serializable {

    private static final long serialVersionUID = 1

    Person person
    Role role

    static belongsTo = [person:Person]

    @Override
    boolean equals(other) {
        if (other instanceof PersonRole) {
            other.personId == person?.id && other.roleId == role?.id
        }
    }

    @Override
    int hashCode() {
        def builder = new HashCodeBuilder()
        if (person) builder.append(person.id)
        if (role) builder.append(role.id)
        builder.toHashCode()
    }

    static PersonRole get(long userId, long roleId) {
        criteriaFor(userId, roleId).get()
    }

    static boolean exists(long userId, long roleId) {
        criteriaFor(userId, roleId).count()
    }

    private static DetachedCriteria criteriaFor(long userId, long roleId) {
        PersonRole.where {
            person == Person.load(userId) &&
                    role == Role.load(roleId)
        }
    }

    static PersonRole create(Person user, Role role) {
        def instance = new PersonRole(user: user, role: role)
        instance.save()
        instance
    }

    static boolean remove(Person u, Role r) {
        if (u != null && r != null) {
            PersonRole.where { person == u && role == r }.deleteAll()
        }
    }

    static int removeAll(Person u) {
        u == null ? 0 : PersonRole.where { person == u }.deleteAll()
    }

    static int removeAll(Role r) {
        r == null ? 0 : PersonRole.where { role == r }.deleteAll()
    }

    static constraints = {
        role validator: { Role r, PersonRole ur ->
            if (ur.person?.id) {
                PersonRole.withNewSession {
                    if (PersonRole.exists(ur.person.id, r.id)) {
                        return ['userRole.exists']
                    }
                }
            }
        }
    }

    static mapping = {
        id composite: ['person', 'role']
        version false
    }
}