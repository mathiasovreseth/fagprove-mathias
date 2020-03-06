package fagprove.mathias

import grails.testing.web.interceptor.InterceptorUnitTest
import spock.lang.Specification

class LogRequestInterceptorSpec extends Specification implements InterceptorUnitTest<LogRequestInterceptor> {

    def setup() {
    }

    def cleanup() {

    }

    void "Test logRequest interceptor matching"() {
        when:"A request matches the interceptor"
        withRequest(controller:"logRequest")

        then:"The interceptor does match"
        interceptor.doesMatch()
    }
}
