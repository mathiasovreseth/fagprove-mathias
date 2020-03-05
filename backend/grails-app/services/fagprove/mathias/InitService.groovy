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

    PersonService personService

    @Selector('gorm:datastoreInitialized')
    def init() {
        if(!Person.count()) {
            Calendar c = Calendar.getInstance()

            Role admin = new Role(
                    authority: 'ROLE_ADMIN',
                    name: 'Administrator',
                    description: 'Administrator'
            )
            admin.save(flush: true, failOnError: true)

            Role user = new Role(
                    authority: 'ROLE_USER',
                    name: 'Bruker',
                    description: 'Bruker'
            )
            user.save(failOnError: true)


            Role manager = new Role(
                    authority: 'ROLE_MANAGER',
                    name: 'Bidragsyter',
                    description: 'Bidragsyter'
            )
            manager.save(failOnError: true)

            Committee dataelektronikar = new Committee(
                    name: "Dataelektronikar"
            )
            dataelektronikar.save(failOnError: true)
            Committee ikt_servicefag = new Committee(
                    name: "IKT-servicefag"
            )
            ikt_servicefag.save(flush:true, failOnError: true)

            Person kristoffer = new Person(
                    email: "kristoffer@munikum.no",
                    name: "Kristoffer",
                    password: "testing",
                    personType: PersonType.ADMIN)
            kristoffer.save(flush: true, failOnError: true)
            new PersonRole(
                    person: kristoffer,
                    role: admin
            ).save(failOnError: true)

            Person mathias = new Person(
                    email: "mathias@munikum.no",
                    name: "Mathias",
                    password: "testing",
                    personType: PersonType.CANDIDATE,
                    phoneNumber: "+4711223344",
                    region: "Stad kommune",
                    company: "Munikum AS",
                    registrationReceived: true
            )
            mathias.save(flush: true, failOnError: true)
            new PersonRole(
                    person: mathias,
                    role: user
            ).save(failOnError: true)

            Person roar = new Person(
                    email: "roar@example.com",
                    name: "Roar Grønmo",
                    password: "testing",
                    personType: PersonType.EXAMINATOR,
                    jobRole: "Medlem",
                    phoneNumber: "+4711223344",
            )
            roar.addToCommittees(dataelektronikar)
            roar.addToCommittees(ikt_servicefag)
            roar.save(flush: true, failOnError: true)
            new PersonRole(
                    person: roar,
                    role: admin
            ).save(failOnError: true)

            c.set(2020, Calendar.FEBRUARY, 3)
            Date roarBusyStart = c.getTime()
            c.set(2020, Calendar.FEBRUARY, 14)
            Date roarBusyEnd = c.getTime()
            personService.setBusy(roar, roarBusyStart, roarBusyEnd)

            Person mads = new Person(
                    email: "mads@example.com",
                    name: "Mads Masdal",
                    password: "testing",
                    personType: PersonType.EXAMINATOR,
                    jobRole: "Medlem",
                    phoneNumber: "+4711223344",
            )
            mads.addToCommittees(dataelektronikar)
            mads.addToCommittees(ikt_servicefag)
            mads.save(flush: true, failOnError: true)
            new PersonRole(
                    person: mads,
                    role: manager
            ).save(failOnError: true)

            Person randi = new Person(
                    email: "randi@example.com",
                    name: "Randi Scheflo",
                    password: "testing",
                    personType: PersonType.EXAMINATOR,
                    jobRole: "Leiar",
                    phoneNumber: "+4711223344",
            )
            randi.addToCommittees(dataelektronikar)
            randi.addToCommittees(ikt_servicefag)
            randi.save(flush: true, failOnError: true)
            new PersonRole(
                    person: randi,
                    role: manager
            ).save(failOnError: true)

            c.set(2020, Calendar.MARCH, 10, 8, 0 ,0)
            Date start = c.getTime()
            c.add(Calendar.DATE, 1)
            Date end = c.getTime()

            new Examination(
                    candidate: mathias,
                    responsibleExaminator: roar,
                    secondaryExaminator: randi,
                    startDate: start,
                    endDate: end
            ).save(flush: true, failOnError: true)
        }
    }
}
