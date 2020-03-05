package fagprove.mathias

import grails.compiler.GrailsCompileStatic

@GrailsCompileStatic
class Committee {

    String name

    static mappedBy = [
            members: "committees"
    ]

    static hasMany = [
            members: Person
    ]

    static constraints = {
        name nullable: false
    }

    static mapping = {
        id generator:'sequence', params:[sequence:'committee_seq']
    }
}
