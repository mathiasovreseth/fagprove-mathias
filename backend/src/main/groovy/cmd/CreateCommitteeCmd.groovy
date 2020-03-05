package cmd

import grails.compiler.GrailsCompileStatic
import grails.validation.Validateable

@GrailsCompileStatic
class CreateCommitteeCmd implements Validateable {

    String name

    static constraints = {
        name nullable: false
    }
}
