package fagprove.mathias

import fagprove.mathias.cmd.CreatePersonCmd
import fagprove.mathias.cmd.UpdatePersonCmd
import fagprove.mathias.enums.PersonType
import grails.compiler.GrailsCompileStatic
import grails.gorm.transactions.Transactional

@GrailsCompileStatic
@Transactional
class PersonService {

    Person create(CreatePersonCmd form) throws ExaminationException {
        if(Person.findByEmail(form.email)) {
            log.error("A person with this email already exists!")
            throw new ExaminationException("A person with this email already exists!")
        }

        if(form.personType == PersonType.CANDIDATE) {
            if(!form.committees || form.committees.size() == 0) {
                log.error("A candidate must have a committee!")
                throw new ExaminationException("A candidate must have a committee!")
            }
            if(form.committees.size() > 1) {
                log.error("A candidate can only have one committee!")
                throw new ExaminationException("A candidate can only have one committee!")
            }
        }

        if(form.personType == PersonType.CANDIDATE &&
                form.role != 'ROLE_USER') {
            log.error("Candidates can only be ROLE_USER!")
            throw new ExaminationException("Candidates can only be ROLE_USER!")
        }

        Role role = Role.findByAuthority(form.role)

        if(!role) {
            log.error("Role $form.role does not exists!")
            throw new ExaminationException("Role $form.role does not exists!")
        }

        Person person = new Person(
                email: form.email,
                name: form.name,
                password: form.password,
                personType: form.personType,
                jobRole: form.jobRole,
                phoneNumber: form.phoneNumber,
                company: form.company,
                region: form.region,
                registrationReceived: form.registrationReceived
        ).save(flush:true, failOnError:true)
        new PersonRole(
                person: person,
                role: role
        ).save(flush: true, failOnError:true)

        if(form.committees) {
            for(Long c in form.committees) {
                person.addToCommittees(Committee.load(c))
            }
            person.save()
        }

        log.info("Person $person.name created")

        return person
    }

    Person update(Person person, UpdatePersonCmd form) throws ExaminationException {
        if(Person.findByEmail(form.email) &&
                Person.findByEmail(form.email) != person
        ) {
            log.error("A person with this email already exists!")
            throw new ExaminationException("A person with this email already exists!")
        }

        if(form.personType == PersonType.CANDIDATE) {
            if(!form.committees || form.committees.size() == 0) {
                log.error("A candidate must have a committee!")
                throw new ExaminationException("A candidate must have a committee!")
            }
            if(form.committees.size() > 1) {
                log.error("A candidate can only have one committee!")
                throw new ExaminationException("A candidate can only have one committee!")
            }
        }

        if(form.personType == PersonType.CANDIDATE &&
                form.role != 'ROLE_USER') {
            log.error("Candidates can only be ROLE_USER!")
            throw new ExaminationException("Candidates can only be ROLE_USER!")
        }

        Role role = Role.findByAuthority(form.role)

        if(!role) {
            log.error("Role $form.role does not exists!")
            throw new ExaminationException("Role $form.role does not exists!")
        }

        person.email = form.email
        person.name = form.name
        person.password = form.password
        person.personType = form.personType
        person.jobRole = form.jobRole
        person.phoneNumber = form.phoneNumber
        person.company = form.company
        person.region = form.region
        person.registrationReceived = form.registrationReceived

        List<PersonRole> personRoles = PersonRole.findAllByPerson(person)

        PersonRole personRoleToDelete = null

        for(PersonRole personRole in personRoles) {
            if(personRole.role.authority != form.role) {
                personRoleToDelete = personRole
                new PersonRole(
                        person: person,
                        role: Role.findByAuthority(form.role)
                ).save(failOnError:true)
            }
        }

        if(personRoleToDelete) {
            personRoleToDelete.delete(flush: true, failOnError: true)
        }

        if(form.committees != null) {
            List<Committee> committeesToDelete = []
            for(Committee c in person.committees) {
                boolean shouldDelete = true
                for(Long c2 in form.committees) {
                    if(c.id == c2) {
                        shouldDelete = false
                        break
                    }
                }
                if(shouldDelete) {
                    committeesToDelete.add(c)
                }
            }
            for(Committee c in committeesToDelete) {
                person.removeFromCommittees(c)
            }

            for(Long c in form.committees) {
                person.addToCommittees(Committee.load(c))
            }
            person.save()
        }

        log.info("Person $person.name updated")

        return person
    }

    def setBusy(Person person, Date from, Date to) {
        Calendar c = Calendar.getInstance()
        c.setTime(from)

        while(c.getTime() <= to) {
            new BusyDay(
                    person: person,
                    day: c.getTime()
            ).save()
            c.add(Calendar.DATE, 1)
        }

        log.info("$person.name set as busy from $from to $to")
    }
}
