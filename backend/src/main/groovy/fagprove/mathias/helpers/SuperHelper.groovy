package fagprove.mathias.helpers

import fagprove.mathias.*
import fagprove.mathias.enums.PersonType
import grails.compiler.GrailsCompileStatic

@GrailsCompileStatic
class SuperHelper {
    static Map renderPerson(Person person) {
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
                personType: person.personType.name(),
                roles: renderPersonRole(PersonRole.findAllByPerson(person)),
                committees: c
        ]
    }

    static Map renderPersonLimited(Person person) {
        return [
                id: person.id,
                name: person.name
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
                examinationStartDate: examination ? examination.startDate : null,
                examinationEndDate: examination ? examination.endDate : null,
                examinationResponsible: examination ? renderExaminator(examination.responsibleExaminator) : null,
                examinationSecondary: examination ? renderExaminator(examination.secondaryExaminator) : null,
                committee: person.committees[0] ? renderCommittee(person.committees[0]) : null
        ]
    }

    static Map renderCommittee(Committee committee) {
        def members = []
        def candidates = []
        if(committee.members) {
            for(Person person in committee.members) {
                if(person.personType == PersonType.CANDIDATE) {
                    candidates.add(renderPersonLimited(person))
                } else if(person.personType == PersonType.EXAMINATOR) {
                    members.add(renderPersonLimited(person))
                }
            }
        }

        return [
                id: committee.id,
                name: committee.name,
                members: members,
                leader: committee.leader ? renderPersonLimited(committee.leader) : null,
                candidates: candidates
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
