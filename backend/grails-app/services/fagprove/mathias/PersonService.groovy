package fagprove.mathias

import grails.gorm.transactions.Transactional

@Transactional
class PersonService {

    def setBusy(Person person, Date from, Date to) {
        Calendar c = Calendar.getInstance()
        c.setTime(from)

        while(c.getTime() <= to) {
            new BusyDay(
                    person: person,
                    day: c.getTime()
            ).save()
            c.add(Calendar.DATE, 1)
        }
    }
}
