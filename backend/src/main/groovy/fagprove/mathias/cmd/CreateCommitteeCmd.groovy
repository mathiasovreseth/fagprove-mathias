package fagprove.mathias.cmd

import fagprove.mathias.Person
import grails.compiler.GrailsCompileStatic
import grails.validation.Validateable

@GrailsCompileStatic
class CreateCommitteeCmd implements Validateable {

    String name
    Person leader

    static constraints = {
        name nullable: false
        leader nullable: true
    }
}
