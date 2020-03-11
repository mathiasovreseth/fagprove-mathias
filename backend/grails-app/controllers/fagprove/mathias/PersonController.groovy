package fagprove.mathias

import fagprove.mathias.cmd.CreatePersonCmd
import fagprove.mathias.cmd.SetBusyCmd
import fagprove.mathias.cmd.UpdatePersonCmd
import fagprove.mathias.enums.PersonType
import fagprove.mathias.helpers.SuperHelper
import grails.compiler.GrailsCompileStatic
import grails.converters.JSON
import grails.gorm.transactions.Transactional
import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.annotation.Secured
import groovy.util.logging.Slf4j
import org.springframework.http.HttpStatus

@GrailsCompileStatic
@Secured('ROLE_MANAGER')
@Slf4j
@Transactional
class PersonController {
	static responseFormats = ['json']

    SpringSecurityService springSecurityService
    PersonService personService

    def index() { }

    @Secured('ROLE_USER')
    def me() {
        Person currentUser = (Person)springSecurityService.getCurrentUser()

        render SuperHelper.renderPerson(currentUser) as JSON
    }

    def listCandidates() {
        List<Person> persons = Person.findAllByPersonType(PersonType.CANDIDATE)

        Person currentUser = (Person)springSecurityService.getCurrentUser()
        boolean isAdmin = SuperHelper.isAdmin(currentUser)

        def retVal = []

        for(Person person in persons) {
            if(isAdmin) {
                retVal.add(SuperHelper.renderCandidate(person))
                continue
            }

            for(Committee c in person.committees) {
                for(Committee c2 in currentUser.committees) {
                    if(c.id == c2.id) {
                        retVal.add(SuperHelper.renderCandidate(person))
                        break
                    }
                }
            }
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
            String s = form.errors.allErrors.each { log.error "{}", it }
            render text: s, status: HttpStatus.BAD_REQUEST
            return
        }

        Person currentUser = (Person)springSecurityService.getCurrentUser()

        if(form.personType == PersonType.EXAMINATOR &&
                !SuperHelper.isAdmin(currentUser)) {
            log.error("Only admins can create examinators")
            render status: HttpStatus.FORBIDDEN
            return
        }

        if(form.role == 'ROLE_ADMIN' &&
                !SuperHelper.isAdmin(currentUser)) {
            log.error("Only admins can create other admins!")
            render status: HttpStatus.FORBIDDEN
            return
        }

        if(form.personType == PersonType.CANDIDATE &&
                form.role != 'ROLE_USER') {
            log.error("Candidates can only be ROLE_USER!")
            render status: HttpStatus.FORBIDDEN
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
            String s = form.errors.allErrors.each { log.error "{}", it }
            render text: s, status: HttpStatus.BAD_REQUEST
            return
        }

        Person person = Person.findById(form.id)

        if(!person) {
            render status: HttpStatus.NOT_FOUND
            return
        }

        Person currentUser = (Person)springSecurityService.getCurrentUser()

        if(person.personType == PersonType.EXAMINATOR ||
                form.personType == PersonType.EXAMINATOR
        ) {
            if(!SuperHelper.isAdmin(currentUser)) {
                log.error("Only admins can update examinators")
                render status: HttpStatus.FORBIDDEN
                return
            }
        }

        List<PersonRole> personRoles = PersonRole.findAllByPerson(person)
        String personRole = personRoles[0].role.authority

        if(form.role == 'ROLE_ADMIN' || personRole == 'ROLE_ADMIN') {
            if(!SuperHelper.isAdmin(currentUser)) {
                log.error("Only admins can update other admins!")
                render status: HttpStatus.FORBIDDEN
                return
            }
        }

        if(form.personType == PersonType.CANDIDATE &&
                form.role != 'ROLE_USER') {
            log.error("Candidates can only be ROLE_USER!")
            render status: HttpStatus.FORBIDDEN
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

    def setBusy(SetBusyCmd form) {
        form.validate()

        if(form.hasErrors()) {
            String s = form.errors.allErrors.each { log.error "{}", it }
            render text: s, status: HttpStatus.BAD_REQUEST
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