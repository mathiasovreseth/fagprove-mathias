package fagprove.mathias

import enums.PersonType
import grails.compiler.GrailsCompileStatic
import grails.converters.JSON
import grails.databinding.BindUsing
import grails.databinding.SimpleMapDataBindingSource
import grails.gorm.transactions.Transactional
import grails.plugin.springsecurity.annotation.Secured
import grails.validation.Validateable
import groovy.util.logging.Slf4j
import helpers.SuperHelper
import org.springframework.http.HttpStatus

@GrailsCompileStatic
@Secured('ROLE_ADMIN')
@Slf4j
@Transactional
class PersonController {
	static responseFormats = ['json', 'xml']

    PersonService personService

    def index() { }

    def listCandidates() {
        List<Person> persons = Person.findAllByPersonType(PersonType.CANDIDATE)

        def retVal = []

        for(Person person in persons) {
            retVal.add(SuperHelper.renderCandidate(person))
        }

        render retVal as JSON
    }

    def listExaminators() {
        List<Person> persons = Person.findAllByPersonType(PersonType.EXAMINATOR)

        def retVal = []

        for(Person person in persons) {
            retVal.add(SuperHelper.renderExaminator(person))
        }

        render retVal as JSON
    }

    def show(Long id) {
        render SuperHelper.renderPerson(Person.findById(id)) as JSON
    }

    def create(CreatePersonCmd form) {
        form.validate()

        if(form.hasErrors()) {
            log.error(form.errors.toString())
            render status: HttpStatus.BAD_REQUEST
            return
        }

        Role role = Role.findByAuthority(form.role)

        if(!role) {
            render status: HttpStatus.BAD_REQUEST
            return
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

        render SuperHelper.renderPerson(person) as JSON
    }

    def update(UpdatePersonCmd form) {
        form.validate()

        if(form.hasErrors()) {
            log.error(form.errors.toString())
            render status: HttpStatus.BAD_REQUEST
            return
        }

        Person person = Person.findById(form.id)

        if(!person) {
            render status: HttpStatus.NOT_FOUND
            return
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

        render SuperHelper.renderPerson(person) as JSON
    }

    def delete() {}

    def setBusy(SetBusyCmd form) {
        form.validate()

        if(form.hasErrors()) {
            log.error(form.errors.toString())
            render status: HttpStatus.BAD_REQUEST
            return
        }

        Person person = Person.findById(form.person)

        if(!person) {
            log.error("Person not found")
            render status: HttpStatus.BAD_REQUEST
            return
        }

        personService.setBusy(person, form.from, form.to)

        render text: '', status: HttpStatus.OK
    }
}

@GrailsCompileStatic
class CreatePersonCmd implements Validateable {

    @BindUsing( { CreatePersonCmd obj, SimpleMapDataBindingSource source ->
        String email = source['email'] as String
        return email.toLowerCase()
    })
    String email
    String name
    String password
    String role
    String jobRole
    String phoneNumber
    String company
    String region
    Boolean registrationReceived

    PersonType personType

    List<Long> committees

    static constraints = {
        email email: true, nullable: false, blank: false
        name nullable: false, blank: false
        password nullable: false, blank: false
        role nullable: false
        personType nullable: false
        jobRole nullable: true
        phoneNumber nullable: true
        company nullable: true
        region nullable: true
        registrationReceived nullable: true
        committees nullable: true
    }
}

@GrailsCompileStatic
class UpdatePersonCmd implements Validateable {

    Long id

    @BindUsing( { UpdatePersonCmd obj, SimpleMapDataBindingSource source ->
        String email = source['email'] as String
        return email.toLowerCase()
    })
    String email
    String name
    String password
    String role
    String jobRole
    String phoneNumber
    String company
    String region
    Boolean registrationReceived

    PersonType personType

    List<Long> committees

    static constraints = {
        id nullable: false
        email email: true, blank: false
        name nullable: false, blank: false
        password blank: false
        role nullable: false
        personType nullable: false
        jobRole nullable: true
        phoneNumber nullable: true
        company nullable: true
        region nullable: true
        registrationReceived nullable: true
        committees nullable: true
    }
}

@GrailsCompileStatic
class SetBusyCmd implements Validateable {

    Long person
    Date from
    Date to

    static constraints = {
        person nullable: false
        from nullable: false
        to nullable: false
    }
}