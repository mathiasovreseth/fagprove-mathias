package fagprove.mathias

import cmd.CreatePersonCmd
import cmd.SetBusyCmd
import cmd.UpdatePersonCmd
import enums.PersonType
import grails.compiler.GrailsCompileStatic
import grails.converters.JSON
import grails.gorm.transactions.Transactional
import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.annotation.Secured
import groovy.util.logging.Slf4j
import helpers.SuperHelper
import org.springframework.http.HttpStatus

@GrailsCompileStatic
@Secured('ROLE_ADMIN')
@Slf4j
@Transactional
class PersonController {
	static responseFormats = ['json']

    SpringSecurityService springSecurityService
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

        Person person = personService.create(form)

        if(!person) {
            log.error("Could not create person")
            render status: HttpStatus.INTERNAL_SERVER_ERROR
            return
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

        person = personService.update(person, form)

        if(!person) {
            log.error("Could not update person")
            render status: HttpStatus.INTERNAL_SERVER_ERROR
            return
        }

        render SuperHelper.renderPerson(person) as JSON
    }

    def delete() {}

    @Secured('ROLE_MANAGER')
    def setBusy(SetBusyCmd form) {
        form.validate()

        if(form.hasErrors()) {
            log.error(form.errors.toString())
            render status: HttpStatus.BAD_REQUEST
            return
        }

        Person currentUser = (Person)springSecurityService.getCurrentUser()

        Person person = Person.findById(form.person)

        if(!person) {
            log.error("Person not found")
            render status: HttpStatus.BAD_REQUEST
            return
        }

        if(currentUser != person && !SuperHelper.isAdmin(currentUser)) {
            log.error("Access denied")
            render status: HttpStatus.FORBIDDEN
            return
        }

        personService.setBusy(person, form.from, form.to)

        render text: '', status: HttpStatus.OK
    }
}