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
@Secured('ROLE_USER')
@Slf4j
@Transactional
class ExaminationController {
	static responseFormats = ['json', 'xml']

    def index() { }

    def list() {
        List<Examination> examinations = Examination.findAll()

        def retVal = []

        for(Examination examination in examinations) {
            retVal.add(SuperHelper.renderExamination(examination))
        }

        render retVal as JSON
    }

    def show(Long id) {
        Examination examination = Examination.findById(id)

        if(!examination) {
            log.error("Examination with id $id not found")
            render status: HttpStatus.NOT_FOUND
            return
        }

        render SuperHelper.renderExamination(examination) as JSON
    }

    @Secured('ROLE_ADMIN')
    def create(CreateExaminationCmd form) {
        form.validate()

        if(form.hasErrors()) {
            log.error(form.errors.toString())
            render status: HttpStatus.BAD_REQUEST
            return
        }

        Examination examination = new Examination(
                candidate: form.candidate,
                responsibleExaminator: form.responsibleExaminator,
                secondaryExaminator: form.secondaryExaminator,
                startDate: form.startDate,
                endDate: form.endDate
        )

        examination.save(flush:true, failOnError:true)

        log.info("Examination with id $examination.id created")

        render SuperHelper.renderExamination(examination) as JSON
    }

    @Secured('ROLE_ADMIN')
    def update(UpdateExaminationCmd form) {
        form.validate()

        if(form.hasErrors()) {
            log.error(form.errors.toString())
            render status: HttpStatus.BAD_REQUEST
            return
        }

        Examination examination = Examination.findById(form.id)

        if(!examination) {
            log.error("Examination with id $form.id not found")
            render status: HttpStatus.NOT_FOUND
            return
        }

        examination.candidate = form.candidate
        examination.responsibleExaminator = form.responsibleExaminator
        examination.secondaryExaminator = form.secondaryExaminator
        examination.startDate = form.startDate
        examination.endDate = form.endDate

        examination.save(flush:true, failOnError:true)

        log.info("Examination with id $form.id updated")

        render SuperHelper.renderExamination(examination) as JSON
    }

    @Secured('ROLE_ADMIN')
    def delete(Long id) {
        Examination examination = Examination.findById(id)

        if(!examination) {
            log.error("Examination with id $id not found")
            render status: HttpStatus.NOT_FOUND
            return
        }

        examination.delete(flush:true, failOnError: true)

        log.info("Examination with id $id deleted")

        render status: HttpStatus.OK
    }
}

@GrailsCompileStatic
class CreateExaminationCmd implements Validateable {

    Person candidate
    Person responsibleExaminator
    Person secondaryExaminator

    Date startDate
    Date endDate

    static constraints = {
        candidate nullable: false
        responsibleExaminator nullable: false
        secondaryExaminator nullable: false
        startDate nullable: false
        endDate nullable: false
    }
}

@GrailsCompileStatic
class UpdateExaminationCmd implements Validateable {

    Long id

    Person candidate
    Person responsibleExaminator
    Person secondaryExaminator

    Date startDate
    Date endDate

    static constraints = {
        id nullable: false
        candidate nullable: false
        responsibleExaminator nullable: false
        secondaryExaminator nullable: false
        startDate nullable: false
        endDate nullable: false
    }
}