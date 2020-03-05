package fagprove.mathias.cmd

import grails.compiler.GrailsCompileStatic
import grails.validation.Validateable

@GrailsCompileStatic
class UpdateCommitteeCmd implements Validateable {

    Long id
    String name

    static constraints = {
        id nullable: false
        name nullable: false
    }
}
