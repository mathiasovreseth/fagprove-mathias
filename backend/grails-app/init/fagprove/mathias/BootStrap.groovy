package fagprove.mathias

class BootStrap {

    InitService initService

    def init = { servletContext ->
        initService.init()
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
        Locale.setDefault(new Locale("nn-NO"))
    }
    def destroy = {
    }
}
