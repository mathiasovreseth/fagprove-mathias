package cmd

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

    static constraints = {
        id nullable: false
        candidate nullable: false
        responsibleExaminator nullable: false
        secondaryExaminator nullable: false
        startDate nullable: false
        endDate nullable: false
    }
}