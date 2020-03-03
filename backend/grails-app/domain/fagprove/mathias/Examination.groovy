package fagprove.mathias

import grails.compiler.GrailsCompileStatic

@GrailsCompileStatic
class Examination {

    Person candidate
    Person responsibleExaminator
    Person secondaryExaminator

    Date startDate
    Date endDate

    Date dateCreated
    Date lastUpdated

    static belongsTo = [
            Person
    ]

    static constraints = {
        candidate nullable: false
        responsibleExaminator nullable: false
        secondaryExaminator nullable: false
        startDate nullable: false
        endDate nullable: false
    }

    static mapping = {
        id generator:'sequence', params:[sequence:'exam_seq']
        candidate index: 'exam_candidate_idx'
        responsibleExaminator index: 'exam_responsible_examinator_idx'
        secondaryExaminator index: 'exam_secondary_examinator_idx'
        startDate index: 'exam_start_date_idx'
        endDate index: 'exam_end_date_idx'
    }
}
