package fagprove.mathias

import fagprove.mathias.cmd.CalendarListCmd
import fagprove.mathias.cmd.CreateExaminationCmd
import fagprove.mathias.cmd.UpdateExaminationCmd
import fagprove.mathias.enums.PersonType
import fagprove.mathias.helpers.SuperHelper
import grails.compiler.GrailsCompileStatic
import grails.converters.JSON
import grails.gorm.transactions.Transactional
import grails.plugin.springsecurity.annotation.Secured
import groovy.util.logging.Slf4j
import org.springframework.http.HttpStatus

@GrailsCompileStatic
@Secured('ROLE_USER')
@Slf4j
@Transactional
class ExaminationController {
	static responseFormats = ['json']

    ExaminationService examinationService

    def index() { }

    def calendar(CalendarListCmd form) {
        Calendar c = Calendar.getInstance()
        c.setTime(form.startDate)

        List<Person> examinators = Person.findAllByPersonType(PersonType.EXAMINATOR)
        List<Examination> examinations = Examination.findAllByStartDateGreaterThanAndEndDateNotGreaterThan(
                form.startDate, form.endDate
        )
        List<BusyDay> busyDays = BusyDay.findAllByDayBetween(form.startDate, form.endDate)

        def retVal = []

        while(c.getTime() <= form.endDate) {
            def day = [:]

            day.day = c.get(Calendar.DATE)
            day.dayOfWeek = c.get(Calendar.DAY_OF_WEEK)
            day.week = c.get(Calendar.WEEK_OF_YEAR)
            day.month = c.get(Calendar.MONTH) + 1
            day.year = c.get(Calendar.YEAR)

            Long dateSpanStart = c.getTimeInMillis()
            Long dateSpanEnd = c.getTimeInMillis() + 1000 * 60 * 60 * 24

            List<Map> examinatorsInDay = []
            for(Person examinator in examinators) {
                def m = [:]

                m.name = examinator.name

                boolean isBusy = false
                for(BusyDay busyDay in busyDays) {
                    if(busyDay.person != examinator) {
                        continue
                    }
                    if(busyDay.day.getTime() <= dateSpanEnd &&
                            busyDay.day.getTime() >= dateSpanStart) {
                        isBusy = true
                        break
                    }
                }
                m.isBusy = isBusy

                def examinationsInDay = []
                for(Examination examination in examinations) {
                    if(!(examination.responsibleExaminator == examinator ||
                            examination.secondaryExaminator == examinator)
                    ) {
                        continue
                    }
                    if(examination.startDate.getTime() <= dateSpanEnd &&
                            examination.endDate.getTime() >= dateSpanStart) {
                        examinationsInDay.add(
                                SuperHelper.renderExamination(examination)
                        )
                    }
                }
                m.examinations = examinationsInDay

                examinatorsInDay.add(m)
            }
            day.examinators = examinatorsInDay

            retVal.add(day)

            c.add(Calendar.DATE, 1)
        }

        render retVal as JSON
    }

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

    @Secured('ROLE_MANAGER')
    def create(CreateExaminationCmd form) {
        form.validate()

        if(form.hasErrors()) {
            log.error(form.errors.toString())
            render status: HttpStatus.BAD_REQUEST
            return
        }

        Examination examination = examinationService.create(form)

        render SuperHelper.renderExamination(examination) as JSON
    }

    @Secured('ROLE_MANAGER')
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

        render SuperHelper.renderExamination(examination) as JSON
    }

    @Secured('ROLE_MANAGER')
    def delete(Long id) {
        Examination examination = Examination.findById(id)

        if(!examination) {
            log.error("Examination with id $id not found")
            render status: HttpStatus.NOT_FOUND
            return
        }

        examination.delete(flush:true, failOnError: true)

        log.info("Examination for candidate $examination.candidate.name deleted")

        render status: HttpStatus.OK
    }
}