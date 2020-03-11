package fagprove.mathias

import fagprove.mathias.cmd.CreateExaminationCmd
import fagprove.mathias.cmd.UpdateExaminationCmd
import grails.compiler.GrailsCompileStatic
import grails.gorm.transactions.Transactional

@GrailsCompileStatic
@Transactional
class ExaminationService {

    Examination create(CreateExaminationCmd form) throws ExaminationException {
        if(form.endDate.getTime() - form.startDate.getTime() < 0) {
            log.error("End date can't be before start date!")
            throw new ExaminationException("End date can't be before start date!")
        }

        if(form.endDate.getTime() - form.startDate.getTime() < 1000 * 60 * 60 * 24) {
            log.error("Examination must last at least one day!")
            throw new ExaminationException("Examination must last at least one day!")
        }

        // Check if candidate already has an examination
        Examination existingExamination = Examination.findByCandidateAndActive(
                form.candidate, true
        )
        if(existingExamination) {
            log.error("Candidate $form.candidate.name already has an active examination!")
            throw new ExaminationException("Candidate $form.candidate.name already has an active examination!")
        }

        // Check for conflicts
        try {
            checkExaminationConflict(
                    null,
                    form.responsibleExaminator,
                    form.secondaryExaminator,
                    form.startDate,
                    form.endDate
            )
        } catch(ExaminationException e) {
            throw e
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

    Examination update(Examination examination, UpdateExaminationCmd form) throws ExaminationException {
        if(!examination) {
            examination = Examination.findById(form.id)
        }

        if(form.endDate.getTime() - form.startDate.getTime() < 0) {
            log.error("End date can't be before start date!")
            throw new ExaminationException("End date can't be before start date!")
        }

        if(form.endDate.getTime() - form.startDate.getTime() < 1000 * 60 * 60 * 24) {
            log.error("Examination must last at least one day!")
            throw new ExaminationException("Examination must last at least one day!")
        }

        // Check for conflicts
        try {
            checkExaminationConflict(
                    null,
                    form.responsibleExaminator,
                    form.secondaryExaminator,
                    form.startDate,
                    form.endDate
            )
        } catch(ExaminationException e) {
            throw e
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

    def checkExaminationConflict(
            Examination examination,
            Person responsibleExaminator,
            Person secondaryExaminator,
            Date startDate,
            Date endDate
    ) throws ExaminationException {
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
                throw new ExaminationException("$responsibleExaminator.name already has an examination on this date!")
            }
        }

        List<BusyDay> busyDays1 = BusyDay.findAllByPerson(responsibleExaminator)
        for(BusyDay busyDay in busyDays1) {
            if(!busyDay.day.before(startDate) && !busyDay.day.after(endDate)) {
                log.error("$responsibleExaminator.name is busy on this date!")
                throw new ExaminationException("$responsibleExaminator.name is busy on this date!")
            }
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
                throw new ExaminationException("$secondaryExaminator.name already has an examination on this date!")
            }
        }

        List<BusyDay> busyDays2 = BusyDay.findAllByPerson(secondaryExaminator)
        for(BusyDay busyDay in busyDays2) {
            if(!busyDay.day.before(startDate) && !busyDay.day.after(endDate)) {
                log.error("$secondaryExaminator.name already has an examination on this date!")
                throw new ExaminationException("$secondaryExaminator.name already has an examination on this date!")
            }
        }
    }
}
