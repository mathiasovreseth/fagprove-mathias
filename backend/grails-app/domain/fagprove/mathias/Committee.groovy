package fagprove.mathias

import grails.compiler.GrailsCompileStatic

@GrailsCompileStatic
class Committee {

    String name
    Person leader

    static mappedBy = [
            members: "committees"
    ]

    static hasMany = [
            members: Person
    ]

    static constraints = {
        name nullable: false
        leader nullable: true
    }

    static mapping = {
        id generator:'sequence', params:[sequence:'committee_seq']
    }
}
