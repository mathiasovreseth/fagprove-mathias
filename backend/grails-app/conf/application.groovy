

// Added by the Spring Security Core plugin:
grails.plugin.springsecurity.userLookup.userDomainClassName = 'fagprove.mathias.Person'
grails.plugin.springsecurity.userLookup.authorityJoinClassName = 'fagprove.mathias.PersonRole'
grails.plugin.springsecurity.authority.className = 'fagprove.mathias.Role'
grails.plugin.springsecurity.userLookup.usernamePropertyName = 'email'

grails.plugin.springsecurity.rest.login.active = false // using AuthController instead
grails.plugin.springsecurity.rest.token.storage.jwt.useSignedJwt = true
grails.plugin.springsecurity.rest.token.storage.jwt.expiration = 36000

grails.plugin.springsecurity.securityConfigType = "Annotation"

grails.plugin.springsecurity.successHandler.defaultTargetUrl = "/"

grails.plugin.springsecurity.roleHierarchy = '''
   ROLE_ADMIN > ROLE_MANAGER
   ROLE_MANAGER > ROLE_USER
'''

grails.plugin.springsecurity.filterChain.chainMap = [
        [pattern: '/auth/**',               	filters: 'none'],
        [
                pattern: '/**',
                filters: 'JOINED_FILTERS,-anonymousAuthenticationFilter,-exceptionTranslationFilter,-authenticationProcessingFilter,-securityContextPersistenceFilter,-rememberMeAuthenticationFilter'
        ]
]

grails.plugin.springsecurity.useSecurityEventListener = true