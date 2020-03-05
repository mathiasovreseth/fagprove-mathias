package fagprove.mathias


import grails.compiler.GrailsCompileStatic
import grails.converters.JSON
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
class CommitteeController {
	static responseFormats = ['json', 'xml']

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
        render SuperHelper.renderCommittee(Committee.findById(id)) as JSON
    }

    def create(CreateCommitteeCmd form) {
        form.validate()

        if(form.hasErrors()) {
            log.error(form.errors.toString())
            render status: HttpStatus.BAD_REQUEST
            return
        }

        Committee committee = new Committee(
                name: form.name
        )
        committee.save(failOnError:true)

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

        committee.name = form.name

        render SuperHelper.renderCommittee(committee) as JSON
    }

    def delete() {}
}

@GrailsCompileStatic
class CreateCommitteeCmd implements Validateable {

    String name

    static constraints = {
        name nullable: false
    }
}

@GrailsCompileStatic
class UpdateCommitteeCmd implements Validateable {

    Long id
    String name

    static constraints = {
        id nullable: false
        name nullable: false
    }
}