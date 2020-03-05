package fagprove.mathias.cmd

import grails.compiler.GrailsCompileStatic
import grails.databinding.BindUsing
import grails.databinding.SimpleMapDataBindingSource
import grails.validation.Validateable

@GrailsCompileStatic
class LoginCommand implements Validateable {

    @BindUsing( { LoginCommand obj, SimpleMapDataBindingSource source ->
        String email = source['email'] as String
        return email.toLowerCase()
    })
    String email

    String password

    static constraints = {
        email email: true, blank: false
        password blank: false
    }
}
