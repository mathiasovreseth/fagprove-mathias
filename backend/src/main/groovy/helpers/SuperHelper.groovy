package helpers

import fagprove.mathias.*
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
                personType: person.personType.name(),
                roles: renderPersonRole(PersonRole.findAllByPerson(person))
        ]
    }

    static Map renderExaminator(Person person) {
        Set<Committee> committees = person.committees

        def c = []
        for(Committee committee in committees) {
            c.add(renderCommittee(committee))
        }

        return [
                id: person.id,
                email: person.email,
                name: person.name,
                dateCreated: person.dateCreated,
                lastUpdated: person.lastUpdated,
                description: person.description,
                phoneNumber: person.phoneNumber,
                jobRole: person.jobRole,
                committees: c
        ]
    }

    static Map renderCandidate(Person person) {
        Examination examination = Examination.findByCandidateAndActive(
                person, true
        )

        return [
                id: person.id,
                email: person.email,
                name: person.name,
                dateCreated: person.dateCreated,
                lastUpdated: person.lastUpdated,
                description: person.description,
                company: person.company,
                phoneNumber: person.phoneNumber,
                region: person.region,
                registrationReceived: person.registrationReceived,
                examinationPlanned: examination != null,
                examinationStartDate: examination.startDate,
                examinationEndDate: examination.endDate,
                examinationResponsible: renderExaminator(examination.responsibleExaminator),
                examinationSecondary: renderExaminator(examination.secondaryExaminator),
        ]
    }

    static Map renderCommittee(Committee committee) {
        return [
                id: committee.id,
                name: committee.name
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
                candidate: renderCandidate(examination.candidate),
                responsibleExaminator: renderExaminator(examination.responsibleExaminator),
                secondaryExaminator: renderExaminator(examination.secondaryExaminator),
                startDate: examination.startDate,
                endDate: examination.endDate
        ]
    }

    static boolean isAdmin(Person person) {
        List<PersonRole> personRoles = PersonRole.findAllByPerson(person)
        for(PersonRole role in personRoles) {
            if(role.role.authority.equals("ROLE_ADMIN")) {
                return true
            }
        }
        return false
    }
}
