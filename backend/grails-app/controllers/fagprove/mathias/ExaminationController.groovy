package fagprove.mathias

import fagprove.mathias.cmd.CalendarListCmd
import fagprove.mathias.cmd.CreateExaminationCmd
import fagprove.mathias.cmd.UpdateExaminationCmd
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
class ExaminationController {
	static responseFormats = ['json']

    ExaminationService examinationService
    SpringSecurityService springSecurityService

    def index() { }

    @Secured('ROLE_USER')
    def myExamination() {
        Person currentUser = (Person)springSecurityService.getCurrentUser()

        if(currentUser.personType != PersonType.CANDIDATE) {
            log.error("Person $currentUser.name is not a candidate")
            render status: HttpStatus.NOT_FOUND
            return
        }

        Examination examination = Examination.findByCandidate(currentUser)

        if(!examination) {
            log.error("Candidate $currentUser.name has no examination")
            render status: HttpStatus.NOT_FOUND
            return
        }

        render SuperHelper.renderExamination(examination) as JSON
    }

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

                m.id = examinator.id
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

    def create(CreateExaminationCmd form) {
        form.validate()

        if(form.hasErrors()) {
            log.error(form.errors.toString())
            render status: HttpStatus.BAD_REQUEST
            return
        }

        if(form.candidate.personType != PersonType.CANDIDATE) {
            log.error("Candidate must be of type CANDIDATE!")
            render status: HttpStatus.BAD_REQUEST
            return
        }

        Person currentUser = (Person)springSecurityService.getCurrentUser()

        if(!SuperHelper.isAdmin(currentUser)) {
            // Check if user is set as an examinator
            if(form.responsibleExaminator.id != currentUser.id &&
                    form.secondaryExaminator.id != currentUser.id) {
                // If not user must be leader of committee
                Committee c = form.candidate.committees[0]
                if(c.leader.id != currentUser.id) {
                    log.error("User is not allowed to create this examination")
                    render status: HttpStatus.FORBIDDEN
                    return
                }
            }
        }

        Examination examination = examinationService.create(form)

        if(!examination) {
            log.error("Could not create examination")
            render status: HttpStatus.CONFLICT
            return
        }

        render SuperHelper.renderExamination(examination) as JSON
    }

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

        Person currentUser = (Person)springSecurityService.getCurrentUser()

        if(!SuperHelper.isAdmin(currentUser)) {
            // Check if user is set as an examinator on saved examination
            if(examination.responsibleExaminator.id != currentUser.id &&
                    examination.secondaryExaminator.id != currentUser.id) {
                // If not user must be leader of committee
                Committee c = examination.candidate.committees[0]
                if(c.leader.id != currentUser.id) {
                    log.error("User is not allowed to update this examination")
                    render status: HttpStatus.FORBIDDEN
                    return
                }
            }

            // Check if user is set as an examinator on updated examination
            if(form.responsibleExaminator.id != currentUser.id &&
                    form.secondaryExaminator.id != currentUser.id) {
                // If not user must be leader of committee
                Committee c = form.candidate.committees[0]
                if(c.leader.id != currentUser.id) {
                    log.error("User is not allowed to update this examination")
                    render status: HttpStatus.FORBIDDEN
                    return
                }
            }
        }

        examination = examinationService.update(examination, form)

        if(!examination) {
            log.error("Could not update examination with id $form.id")
            render status: HttpStatus.CONFLICT
            return
        }

        render SuperHelper.renderExamination(examination) as JSON
    }

    def delete(Long id) {
        Examination examination = Examination.findById(id)

        if(!examination) {
            log.error("Examination with id $id not found")
            render status: HttpStatus.NOT_FOUND
            return
        }

        Person currentUser = (Person)springSecurityService.getCurrentUser()

        if(!SuperHelper.isAdmin(currentUser)) {
            // Check if user is set as an examinator on saved examination
            if(examination.responsibleExaminator.id != currentUser.id &&
                    examination.secondaryExaminator.id != currentUser.id) {
                // If not user must be leader of committee
                Committee c = examination.candidate.committees[0]
                if(c.leader.id != currentUser.id) {
                    log.error("User is not allowed to delete this examination")
                    render status: HttpStatus.FORBIDDEN
                    return
                }
            }
        }

        examination.delete(flush:true, failOnError: true)

        log.info("Examination for candidate $examination.candidate.name deleted")

        render status: HttpStatus.OK
    }
}