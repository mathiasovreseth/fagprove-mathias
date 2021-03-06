package fagprove.mathias

import fagprove.mathias.cmd.CreateCommitteeCmd
import fagprove.mathias.cmd.UpdateCommitteeCmd
import fagprove.mathias.helpers.SuperHelper
import grails.compiler.GrailsCompileStatic
import grails.converters.JSON
import grails.gorm.transactions.Transactional
import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.annotation.Secured
import groovy.util.logging.Slf4j
import org.springframework.http.HttpStatus

@GrailsCompileStatic
@Secured('ROLE_ADMIN')
@Slf4j
@Transactional
class CommitteeController {
	static responseFormats = ['json']

    CommitteeService committeeService
    SpringSecurityService springSecurityService

    def index() { }

    @Secured('ROLE_MANAGER')
    def list() {
        List<Committee> committees = Committee.findAll()

        def retVal = []

        Person currentUser = (Person)springSecurityService.getCurrentUser()

        boolean isAdmin = SuperHelper.isAdmin(currentUser)

        for(Committee c in committees) {
            if(isAdmin) {
                retVal.add(SuperHelper.renderCommittee(c))
                continue
            }

            for(Committee c2 in currentUser.committees) {
                if(c.id == c2.id) {
                    retVal.add(SuperHelper.renderCommittee(c))
                    break
                }
            }
        }

        render retVal as JSON
    }

    @Secured('ROLE_MANAGER')
    def show(Long id) {
        Committee committee = Committee.findById(id)

        if(!committee) {
            log.error("Committee with id $id not found")
            render status: HttpStatus.NOT_FOUND
            return
        }

        Person currentUser = (Person)springSecurityService.getCurrentUser()

        if(SuperHelper.isAdmin(currentUser)) {
            render SuperHelper.renderCommittee(committee) as JSON
            return
        }

        for(Committee c2 in currentUser.committees) {
            if(committee.id == c2.id) {
                render SuperHelper.renderCommittee(committee) as JSON
                return
            }
        }

        render text: 'Access denied', status: HttpStatus.FORBIDDEN
    }

    def create(CreateCommitteeCmd form) {
        form.validate()

        if(form.hasErrors()) {
            String s = form.errors.allErrors.each { log.error "{}", it }
            render text: s, status: HttpStatus.BAD_REQUEST
            return
        }

        Committee committee = committeeService.create(form)

        render SuperHelper.renderCommittee(committee) as JSON
    }

    def update(UpdateCommitteeCmd form) {
        form.validate()

        if(form.hasErrors()) {
            String s = form.errors.allErrors.each { log.error "{}", it }
            render text: s, status: HttpStatus.BAD_REQUEST
            return
        }

        Committee committee = Committee.findById(form.id)

        if(!committee) {
            render status: HttpStatus.NOT_FOUND
            return
        }

        committee = committeeService.update(committee, form)

        render SuperHelper.renderCommittee(committee) as JSON
    }

    def delete(Long id) {
        Committee committee = Committee.findById(id)

        if(!committee) {
            log.error("Committee with id $id not found")
            render status: HttpStatus.NOT_FOUND
            return
        }

        committee.delete(flush:true, failOnError: true)

        log.info("Committee $committee.name deleted")

        render status: HttpStatus.OK
    }
}