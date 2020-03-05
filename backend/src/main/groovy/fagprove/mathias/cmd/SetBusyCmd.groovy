package fagprove.mathias.cmd

import grails.compiler.GrailsCompileStatic
import grails.validation.Validateable

@GrailsCompileStatic
class SetBusyCmd implements Validateable {

    Long person
    Date from
    Date to

    static constraints = {
        person nullable: false
        from nullable: false
        to nullable: false
    }
}
