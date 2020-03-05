package fagprove.mathias

import cmd.CreateExaminationCmd
import cmd.UpdateExaminationCmd
import grails.compiler.GrailsCompileStatic
import grails.gorm.transactions.Transactional

@GrailsCompileStatic
@Transactional
class ExaminationService {

    Examination create(CreateExaminationCmd form) {
        Examination examination = new Examination(
                candidate: form.candidate,
                responsibleExaminator: form.responsibleExaminator,
                secondaryExaminator: form.secondaryExaminator,
                startDate: form.startDate,
                endDate: form.endDate
        )

        examination.save(flush:true, failOnError:true)

        log.info("Examination for candidate $examination.candidate.name created")

        return examination
    }

    Examination update(Examination examination, UpdateExaminationCmd form) {
        if(!examination) {
            examination = Examination.findById(form.id)
        }

        examination.candidate = form.candidate
        examination.responsibleExaminator = form.responsibleExaminator
        examination.secondaryExaminator = form.secondaryExaminator
        examination.startDate = form.startDate
        examination.endDate = form.endDate

        examination.save(flush:true, failOnError:true)

        log.info("Examination for candidate $examination.candidate.name updated")

        return examination
    }
}
