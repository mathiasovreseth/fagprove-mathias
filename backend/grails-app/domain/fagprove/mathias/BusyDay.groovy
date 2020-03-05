package fagprove.mathias

import grails.compiler.GrailsCompileStatic

@GrailsCompileStatic
class BusyDay {

    Person person
    Date day

    static constraints = {
        person nullable: false
        day nullable: false, unique: 'person'
    }

    static mapping = {
        id generator:'sequence', params:[sequence:'busy_day_seq']
        person index: 'busy_day_person_idx'
        day index: 'busy_day_day_idx'
    }
}
