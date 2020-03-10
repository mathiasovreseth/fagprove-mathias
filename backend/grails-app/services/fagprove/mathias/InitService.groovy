package fagprove.mathias

import fagprove.mathias.cmd.CreateCommitteeCmd
import fagprove.mathias.cmd.CreateExaminationCmd
import fagprove.mathias.cmd.CreatePersonCmd
import fagprove.mathias.cmd.UpdateCommitteeCmd
import fagprove.mathias.enums.PersonType
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
    ExaminationService examinationService
    CommitteeService committeeService

    @Selector('gorm:datastoreInitialized')
    def init() {
        if(!Person.count()) {
            Role admin = new Role(
                    authority: 'ROLE_ADMIN',
                    name: 'Administrator',
                    description: 'Administrator'
            )
            admin.save(failOnError: true)

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
            manager.save(flush:true, failOnError: true)

            Committee dataelektronikar = committeeService.create(
                    new CreateCommitteeCmd(
                            name: "Dataelektronikar"
                    )
            )
            Committee ikt_servicefag = committeeService.create(
                    new CreateCommitteeCmd(
                            name: "IKT-servicefag"
                    )
            )

            personService.create(new CreatePersonCmd(
                    email: "kristoffer@munikum.no",
                    name: "Kristoffer",
                    password: "testing",
                    personType: PersonType.ADMIN,
                    role: 'ROLE_ADMIN'
            ))

            Person mathias = personService.create(new CreatePersonCmd(
                    email: "mathias@munikum.no",
                    name: "Mathias",
                    password: "testing",
                    personType: PersonType.CANDIDATE,
                    phoneNumber: "+4711223344",
                    region: "Stad kommune",
                    company: "Munikum AS",
                    registrationReceived: true,
                    role: 'ROLE_USER'
            ))

            Person roar = personService.create(new CreatePersonCmd(
                    email: "roar@example.com",
                    name: "Roar Grønmo",
                    password: "testing",
                    personType: PersonType.EXAMINATOR,
                    jobRole: "Medlem",
                    phoneNumber: "+4711223344",
                    role: 'ROLE_ADMIN',
                    committees: [
                            dataelektronikar.id,
                            ikt_servicefag.id
                    ]
            ))

            Calendar c = Calendar.getInstance()

            c.set(2020, Calendar.FEBRUARY, 3)
            Date roarBusyStart = c.getTime()
            c.set(2020, Calendar.FEBRUARY, 14)
            Date roarBusyEnd = c.getTime()
            personService.setBusy(roar, roarBusyStart, roarBusyEnd)

            Person mads = personService.create(new CreatePersonCmd(
                    email: "mads@example.com",
                    name: "Mads Masdal",
                    password: "testing",
                    personType: PersonType.EXAMINATOR,
                    jobRole: "Medlem",
                    phoneNumber: "+4711223344",
                    role: 'ROLE_MANAGER',
                    committees: [
                            dataelektronikar.id,
                            ikt_servicefag.id
                    ]
            ))

            Person randi = personService.create(new CreatePersonCmd(
                    email: "randi@example.com",
                    name: "Randi Scheflo",
                    password: "testing",
                    personType: PersonType.EXAMINATOR,
                    jobRole: "Leiar",
                    phoneNumber: "+4711223344",
                    role: 'ROLE_MANAGER',
                    committees: [
                            dataelektronikar.id
                    ]
            ))

            committeeService.update(dataelektronikar, new UpdateCommitteeCmd(
                    id: dataelektronikar.id,
                    name: dataelektronikar.name,
                    leader: randi
            ))

            committeeService.update(ikt_servicefag, new UpdateCommitteeCmd(
                    id: ikt_servicefag.id,
                    name: ikt_servicefag.name,
                    leader: roar
            ))

            c.set(2020, Calendar.MARCH, 10, 8, 0 ,0)
            Date start = c.getTime()
            c.add(Calendar.DATE, 1)
            Date end = c.getTime()

            examinationService.create(new CreateExaminationCmd(
                    candidate: mathias,
                    responsibleExaminator: roar,
                    secondaryExaminator: mads,
                    startDate: start,
                    endDate: end
            ))
        }
    }
}
