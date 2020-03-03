import grails.util.BuildSettings
import grails.util.Environment

// See http://logback.qos.ch/manual/groovy.html for details on configuration
appender('STDOUT', ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "%level %logger - %msg%n"
    }
}

root(ERROR, ['STDOUT'])

def targetDir = BuildSettings.TARGET_DIR
if ((Environment.isDevelopmentMode() || Environment.TEST) && targetDir) {
    logger('grails.app', DEBUG)
    logger('fagprove.mathias', DEBUG)
    appender("FULL_STACKTRACE", FileAppender) {
        file = "${targetDir}/stacktrace.log"
        append = true
        encoder(PatternLayoutEncoder) {
            pattern = "%level %logger - %msg%n"
        }
    }
    logger("StackTrace", ERROR, ['FULL_STACKTRACE'], false)
}
if(Environment.current == Environment.PRODUCTION) {
    logger('grails.app', ERROR)
    logger('fagprove.mathias', ERROR)
    appender("FILE", FileAppender){

        file = "/tmp/fagprove.log"
        append = true
        encoder(PatternLayoutEncoder) {
            pattern = "%level %logger - %msg%n"
        }
    }
    root(INFO, ["FILE"])
}

// For debugging spring security
//logger("org.springframework.security", DEBUG, ['STDOUT'], false)
//logger("grails.plugin.springsecurity", DEBUG, ['STDOUT'], false)
//logger("org.pac4j", DEBUG, ['STDOUT'], false)