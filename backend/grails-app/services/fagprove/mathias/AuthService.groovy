package fagprove.mathias

import grails.compiler.GrailsCompileStatic
import grails.gorm.transactions.Transactional
import groovy.util.logging.Slf4j

@GrailsCompileStatic
@Slf4j
@Transactional
class AuthService {

    Person validateLogin(String email, String password) {
        Person person = Person.findByEmail(email)
        if(person) {
            if(person.validateLogin(password)) {
                return person
            }
        }
        return null
    }
}
