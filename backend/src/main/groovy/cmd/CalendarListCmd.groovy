package cmd

import grails.compiler.GrailsCompileStatic
import grails.validation.Validateable

@GrailsCompileStatic
class CalendarListCmd implements Validateable {

    Date startDate
    Date endDate

    static constraints = {
        startDate nullable: false
        endDate nullable: false
    }
}
