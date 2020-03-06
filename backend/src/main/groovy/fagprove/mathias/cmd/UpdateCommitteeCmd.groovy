package fagprove.mathias.cmd

import fagprove.mathias.Person
import grails.compiler.GrailsCompileStatic
import grails.validation.Validateable

@GrailsCompileStatic
class UpdateCommitteeCmd implements Validateable {

    Long id
    String name
    Person leader

    static constraints = {
        id nullable: false
        name nullable: false
        leader nullable: true
    }
}
