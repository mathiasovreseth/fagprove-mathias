package fagprove.mathias

import fagprove.mathias.cmd.CreateCommitteeCmd
import fagprove.mathias.cmd.UpdateCommitteeCmd
import fagprove.mathias.helpers.SuperHelper
import grails.compiler.GrailsCompileStatic
import grails.converters.JSON
import grails.gorm.transactions.Transactional
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

    def index() { }

    def list() {
        List<Committee> committees = Committee.findAll()

        def retVal = []

        for(Committee committee in committees) {
            retVal.add(SuperHelper.renderCommittee(committee))
        }

        render retVal as JSON
    }

    def show(Long id) {
        Committee committee = Committee.findById(id)

        if(!committee) {
            log.error("Committee with id $id not found")
            render status: HttpStatus.NOT_FOUND
            return
        }

        render SuperHelper.renderCommittee(committee) as JSON
    }

    def create(CreateCommitteeCmd form) {
        form.validate()

        if(form.hasErrors()) {
            log.error(form.errors.toString())
            render status: HttpStatus.BAD_REQUEST
            return
        }

        Committee committee = committeeService.create(form)

        render SuperHelper.renderCommittee(committee) as JSON
    }

    def update(UpdateCommitteeCmd form) {
        form.validate()

        if(form.hasErrors()) {
            log.error(form.errors.toString())
            render status: HttpStatus.BAD_REQUEST
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