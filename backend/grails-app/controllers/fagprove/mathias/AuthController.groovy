package fagprove.mathias

import cmd.LoginCommand
import grails.compiler.GrailsCompileStatic
import grails.gorm.transactions.Transactional
import grails.plugin.springsecurity.annotation.Secured
import grails.plugin.springsecurity.rest.token.AccessToken
import grails.plugin.springsecurity.rest.token.generation.TokenGenerator
import grails.plugin.springsecurity.rest.token.rendering.AccessTokenJsonRenderer
import groovy.util.logging.Slf4j
import org.springframework.security.core.userdetails.UserDetailsService

import static org.springframework.http.HttpStatus.BAD_REQUEST
import static org.springframework.http.HttpStatus.UNAUTHORIZED

@GrailsCompileStatic
@Secured('permitAll')
@Slf4j
@Transactional
class AuthController {
	static responseFormats = ['json']

    AuthService authService
    AccessTokenJsonRenderer accessTokenJsonRenderer
    UserDetailsService userDetailsService
    TokenGenerator tokenGenerator

    def login(LoginCommand form) {
        if(form.hasErrors()) {
            render text: '', status: BAD_REQUEST
            return
        }

        Person person = authService.validateLogin(form.email, form.password)

        if(!person) {
            render text: '', status: UNAUTHORIZED
            return
        }

        log.info("User $person.name logged in")

        renderToken(createToken(person))
    }

    private renderToken(AccessToken token) {
        response.addHeader 'Cache-Control', 'no-store'
        response.addHeader 'Pragma', 'no-cache'
        render(
                text: accessTokenJsonRenderer.generateJson(token),
                encoding: 'UTF-8',
                contentType: 'application/json'
        )
    }

    private AccessToken createToken(Person person) {
        AccessToken token = tokenGenerator.generateAccessToken(
                userDetailsService.loadUserByUsername(person.email)
        )
        return token
    }
}