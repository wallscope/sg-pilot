package com.sg.app.config

import org.keycloak.adapters.springsecurity.KeycloakConfiguration
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper
import org.springframework.security.core.session.SessionRegistry
import org.springframework.security.core.session.SessionRegistryImpl
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy

@KeycloakConfiguration
class SecurityConfig : KeycloakWebSecurityConfigurerAdapter() {
    /**
     * Registers the KeycloakAuthenticationProvider with the authentication manager.
     */

    @Autowired
    fun configureGlobal(auth: AuthenticationManagerBuilder) {
        val keycloakAuthenticationProvider = keycloakAuthenticationProvider()
        keycloakAuthenticationProvider.setGrantedAuthoritiesMapper(SimpleAuthorityMapper())
        auth.authenticationProvider(keycloakAuthenticationProvider)
    }

    /**
     * Defines the session authentication strategy.
     */
    @Bean
    override fun sessionAuthenticationStrategy(): SessionAuthenticationStrategy {
        return RegisterSessionAuthenticationStrategy(buildSessionRegistry())
    }

    protected fun buildSessionRegistry(): SessionRegistry {
        return SessionRegistryImpl()
    }

    override fun configure(http: HttpSecurity) {
        super.configure(http)
        http
            .csrf().disable()
            .authorizeRequests()
            // ***Internal***
//            .antMatchers("/api/*").hasRole("default-roles-master")
//            .antMatchers("/api/**").hasRole("default-roles-master")
//            .antMatchers("/api/questionnaire/*").hasRole("default-roles-master")
//            .antMatchers("/api/workgroup/*").hasRole("admin")
//            // ***Organisation***
//            .antMatchers("/api/workgroup/new").hasAnyRole("admin", "lead")
//            .antMatchers(HttpMethod.GET,"/api/workgroup/specific/*").hasAnyRole("lead", "default-roles-master")
//            .antMatchers(HttpMethod.PUT, "/api/workgroup/specific/*").hasRole("lead")
//            .antMatchers(HttpMethod.DELETE, "/api/workgroup/specific/*").hasRole("lead")
            // ***Public***
            .anyRequest().permitAll()
    }
}