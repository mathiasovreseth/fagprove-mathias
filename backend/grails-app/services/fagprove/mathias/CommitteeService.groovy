package fagprove.mathias

import fagprove.mathias.cmd.CreateCommitteeCmd
import fagprove.mathias.cmd.UpdateCommitteeCmd
import grails.compiler.GrailsCompileStatic
import grails.gorm.transactions.Transactional

@GrailsCompileStatic
@Transactional
class CommitteeService {

    Committee create(CreateCommitteeCmd form) {
        Committee committee = new Committee(
                name: form.name
        )
        committee.save(failOnError:true)

        log.info("Committee $committee.name created")

        return committee
    }

    Committee update(Committee committee, UpdateCommitteeCmd form) {
        if(!committee) {
            committee = Committee.findById(form.id)
        }

        committee.name = form.name

        committee.save(failOnError:true)

        log.info("Committee $committee.name updated")

        return committee
    }
}
