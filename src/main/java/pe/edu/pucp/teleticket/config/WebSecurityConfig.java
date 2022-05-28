package pe.edu.pucp.teleticket.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import pe.edu.pucp.teleticket.controller.SesionController;
import pe.edu.pucp.teleticket.repository.ClienteRepository;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeRequests()
                .antMatchers("/admin", "/admin/**").hasAuthority("administrador")
                .antMatchers("/operador", "/operador/**").hasAuthority("operador")
                .antMatchers("/cliente", "/cliente/**", "/carrito", "/carrito/*").access(" isAuthenticated() and not(hasAnyAuthority('operador','administrador')) ")
                .antMatchers("/oauth2", "/oauth2/**").access("not (hasAnyAuthority('cliente', 'operador', 'administrador'))")
                .anyRequest().permitAll();

        http.formLogin().loginPage("/login").loginProcessingUrl("/processLogin").
                usernameParameter("correo").passwordParameter("contrasena")
                .defaultSuccessUrl("/redirectPorRol",true);

        http.oauth2Login().loginPage("/login").defaultSuccessUrl("/oauth2/redirect",true);

        http.logout()
                .logoutSuccessUrl("/")
                .deleteCookies("JSESSIONID")
                .invalidateHttpSession(true);
    }

    @Autowired
    DataSource dataSource;

    @Autowired
    ClienteRepository clienteRepository;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        auth.jdbcAuthentication()
                .dataSource(dataSource)
                .passwordEncoder(new BCryptPasswordEncoder())
                .usersByUsernameQuery("SELECT correo, contrasena, activo FROM usuarios WHERE correo = ?")
                .authoritiesByUsernameQuery("SELECT correo, rol FROM usuarios WHERE correo = ? and activo = 1");

    }

}
