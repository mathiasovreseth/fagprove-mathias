package fagprove.mathias.cmd

import fagprove.mathias.Person
import grails.compiler.GrailsCompileStatic
import grails.validation.Validateable

@GrailsCompileStatic
class UpdateExaminationCmd implements Validateable {

    Long id

    Person candidate
    Person responsibleExaminator
    Person secondaryExaminator

    Date startDate
    Date endDate

    Boolean active

    static constraints = {
        id nullable: false
        candidate nullable: false
        responsibleExaminator nullable: false
        secondaryExaminator nullable: false
        startDate nullable: false
        endDate nullable: false
        active nullable: true
    }
}