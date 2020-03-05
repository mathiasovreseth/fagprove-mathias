package cmd

import enums.PersonType
import grails.compiler.GrailsCompileStatic
import grails.databinding.BindUsing
import grails.databinding.SimpleMapDataBindingSource
import grails.validation.Validateable

@GrailsCompileStatic
class CreatePersonCmd implements Validateable {

    @BindUsing( { CreatePersonCmd obj, SimpleMapDataBindingSource source ->
        String email = source['email'] as String
        return email.toLowerCase()
    })
    String email
    String name
    String password
    String role
    String jobRole
    String phoneNumber
    String company
    String region
    Boolean registrationReceived

    PersonType personType

    List<Long> committees

    static constraints = {
        email email: true, nullable: false, blank: false
        name nullable: false, blank: false
        password nullable: false, blank: false
        role nullable: false
        personType nullable: false
        jobRole nullable: true
        phoneNumber nullable: true
        company nullable: true
        region nullable: true
        registrationReceived nullable: true
        committees nullable: true
    }
}
