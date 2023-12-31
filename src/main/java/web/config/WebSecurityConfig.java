package web.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.actuate.context.ShutdownEndpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;
import web.security.SuccessUserHandler;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableWebMvc
@EnableAspectJAutoProxy
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final SuccessUserHandler successUserHandler;
    private final UserDetailsService userDetailsService;
    private final AuthenticationProvider authenticationProvider;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public WebSecurityConfig(SuccessUserHandler successUserHandler,
                             UserDetailsService userDetailsService,
                             AuthenticationProvider authenticationProvider,
                             PasswordEncoder passwordEncoder) {
        this.successUserHandler = successUserHandler;
        this.userDetailsService = userDetailsService;
        this.authenticationProvider = authenticationProvider;
        this.passwordEncoder = passwordEncoder;
        log.info("WebSecurityConfig constructor called");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        log.info("WebSecurityConfig configure called");
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/login").permitAll()
                .antMatchers(HttpMethod.POST, "/api/register").permitAll()
                .antMatchers("/user/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN", "ROLE_MANAGER", "ROLE_MODERATOR")
                .antMatchers("/manager/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_MANAGER")
                .antMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers(EndpointRequest.to(ShutdownEndpoint.class)).hasRole("ADMIN")
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .successHandler(successUserHandler)
                .and()
                .logout()
                .permitAll();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        log.info("Configure AuthenticationManagerBuilder");
        auth.authenticationProvider(authenticationProvider);
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public RouterFunction<ServerResponse> actuatorRoutes(ShutdownEndpoint shutdownEndpoint) {
        return RouterFunctions.route()
                .GET("/actuator/info", request -> ServerResponse.ok().build())
                .GET("/actuator/metrics", request -> ServerResponse.ok().build())
                .GET("/actuator/env", request -> ServerResponse.ok().build())
                .GET("/actuator/shutdown", request -> ServerResponse.async(shutdownEndpoint.shutdown()))
                .build();
    }

    @Bean
    public ShutdownEndpoint shutdownEndpoint() {
        return new ShutdownEndpoint();
    }
}
