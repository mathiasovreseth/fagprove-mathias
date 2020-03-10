package fagprove.mathias

import fagprove.mathias.cmd.CreateExaminationCmd
import fagprove.mathias.cmd.UpdateExaminationCmd
import grails.compiler.GrailsCompileStatic
import grails.gorm.transactions.Transactional

@GrailsCompileStatic
@Transactional
class ExaminationService {

    Examination create(CreateExaminationCmd form) {

        // Check if candidate already has an examination
        Examination existingExamination = Examination.findByCandidateAndActive(
                form.candidate, true
        )

        if(existingExamination) {
            log.error("Candidate $form.candidate.name already has an active examination!")
            return null
        }

        // Check for conflicts
        if(checkExaminationConflict(
                null,
                form.responsibleExaminator,
                form.secondaryExaminator,
                form.startDate,
                form.endDate
        )) {
            log.error("Conflict on examination dates!")
            return null
        }

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

        // Check for conflicts
        if(checkExaminationConflict(
                examination,
                form.responsibleExaminator,
                form.secondaryExaminator,
                form.startDate,
                form.endDate
        )) {
            log.error("Conflict on examination dates!")
            return null
        }

        examination.candidate = form.candidate
        examination.responsibleExaminator = form.responsibleExaminator
        examination.secondaryExaminator = form.secondaryExaminator
        examination.startDate = form.startDate
        examination.endDate = form.endDate
        examination.active = form.active ? form.active : true

        examination.save(flush:true, failOnError:true)

        log.info("Examination for candidate $examination.candidate.name updated")

        return examination
    }

    boolean checkExaminationConflict(
            Examination examination,
            Person responsibleExaminator,
            Person secondaryExaminator,
            Date startDate,
            Date endDate
    ) {
        boolean conflict = false

        List<Examination> examinations1 = Examination.findAllByResponsibleExaminatorOrSecondaryExaminator(
                responsibleExaminator, responsibleExaminator
        )

        for(Examination e in examinations1) {
            if(examination && e.id == examination.id) {
                continue
            }
            if(e.startDate <= endDate &&
                    e.endDate >= startDate) {
                log.error("$responsibleExaminator.name already has an examination on this date!")
                conflict = true
                break
            }
        }

        if(conflict) {
            return true
        }

        List<Examination> examinations2 = Examination.findAllByResponsibleExaminatorOrSecondaryExaminator(
                secondaryExaminator, secondaryExaminator
        )

        for(Examination e in examinations2) {
            if(examination && e.id == examination.id) {
                continue
            }
            if(e.startDate <= endDate &&
                    e.endDate >= startDate) {
                log.error("$secondaryExaminator.name already has an examination on this date!")
                conflict = true
                break
            }
        }

        return conflict
    }
}
