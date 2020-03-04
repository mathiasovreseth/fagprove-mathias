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

    def index() { }

    def listCandidates() {
        List<Person> persons = Person.findAllByPersonType(PersonType.CANDIDATE)

        def retVal = []

        for(Person person in persons) {
            retVal.add(SuperHelper.renderPerson(person))
        }

        render retVal as JSON
    }

    def listExaminators() {
        List<Person> persons = Person.findAllByPersonType(PersonType.EXAMINATOR)

        def retVal = []

        for(Person person in persons) {
            retVal.add(SuperHelper.renderPerson(person))
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
                personType: form.personType
        ).save(flush:true, failOnError:true)
        new PersonRole(
                person: person,
                role: role
        ).save(failOnError:true)

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

        render SuperHelper.renderPerson(person) as JSON
    }

    def delete() {}
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

    PersonType personType

    static constraints = {
        email email: true, nullable: false, blank: false
        name nullable: false, blank: false
        password nullable: false, blank: false
        role nullable: false
        personType nullable: false
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

    PersonType personType

    static constraints = {
        id nullable: false
        email email: true, blank: false
        name nullable: false, blank: false
        password blank: false
        role nullable: false
        personType nullable: false
    }
}