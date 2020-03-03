package helpers

import fagprove.mathias.Examination
import fagprove.mathias.Person
import fagprove.mathias.PersonRole
import fagprove.mathias.Role
import grails.compiler.GrailsCompileStatic

@GrailsCompileStatic
class SuperHelper {
    static Map renderPerson(Person person) {
        return [
                id: person.id,
                email: person.email,
                name: person.name,
                dateCreated: person.dateCreated,
                lastUpdated: person.lastUpdated,
                description: person.description,
                roles: renderPersonRole(PersonRole.findAllByPerson(person))
        ]
    }

    static Map renderRole(Role role) {
        return [
                authority: role.authority,
                name: role.name,
                description: role.description
        ]
    }

    static List<Map> renderPersonRole(List<PersonRole> roles) {
        def retVal = []

        for(PersonRole role in roles) {
            retVal.add(renderRole(role.role))
        }

        return retVal
    }

    static Map renderExamination(Examination examination) {
        return [
                id: examination.id,
                candidate: renderPerson(examination.candidate),
                responsibleExaminator: renderPerson(examination.responsibleExaminator),
                secondaryExaminator: renderPerson(examination.secondaryExaminator),
                startDate: examination.startDate,
                endDate: examination.endDate
        ]
    }
}
