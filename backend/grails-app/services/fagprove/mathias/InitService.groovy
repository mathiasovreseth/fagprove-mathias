package fagprove.mathias

import enums.PersonType
import grails.compiler.GrailsCompileStatic
import grails.gorm.transactions.Transactional
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component
import reactor.spring.context.annotation.Consumer
import reactor.spring.context.annotation.Selector

@GrailsCompileStatic
@Component
@Consumer
@Slf4j
@Transactional
class InitService {

    @Selector('gorm:datastoreInitialized')
    def init() {
        if(!Role.findByAuthority('ROLE_ADMIN')) {
            new Role(
                    authority: 'ROLE_ADMIN',
                    name: 'Administrator',
                    description: 'Administrator'
            ).save(flush: true, failOnError:true)
        }

        if(!Role.findByAuthority('ROLE_USER')) {
            new Role(
                    authority: 'ROLE_USER',
                    name: 'Bruker',
                    description: 'Bruker'
            ).save(failOnError:true)
        }

        if(!Role.findByAuthority('ROLE_MANAGER')) {
            new Role(
                    authority: 'ROLE_MANAGER',
                    name: 'Bidragsyter',
                    description: 'Bidragsyter'
            ).save(failOnError:true)
        }

        if(!Person.count()) {
            Person person = new Person(
                    email: "kristoffer@munikum.no",
                    name: "Kristoffer",
                    password: "testing",
                    personType: PersonType.EXAMINATOR
            )
            person.save(flush:true, failOnError:true)
            new PersonRole(
                    person: person,
                    role: Role.findByAuthority('ROLE_ADMIN')
            ).save(failOnError:true)

            Person person2 = new Person(
                    email: "mathias@munikum.no",
                    name: "Mathias",
                    password: "testing",
                    personType: PersonType.CANDIDATE
            )
            person2.save(flush:true, failOnError:true)
            new PersonRole(
                    person: person2,
                    role: Role.findByAuthority('ROLE_USER')
            ).save(failOnError:true)
        }

        if(!Examination.count()) {
            new Examination(
                    candidate: Person.load(1),
                    responsibleExaminator: Person.load(1),
                    secondaryExaminator: Person.load(1),
                    startDate: new Date(),
                    endDate: new Date()
            ).save(flush: true, failOnError:true)
        }
    }
}
