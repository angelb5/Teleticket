package pe.edu.pucp.teleticket.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeRequests()
                .antMatchers("/admin", "/admin/**").hasAuthority("administrador")
                .antMatchers("/operador", "/operador/**").hasAuthority("operador")
                .anyRequest().permitAll();

        http.formLogin().loginPage("/login").loginProcessingUrl("/processLogin").
                usernameParameter("correo").passwordParameter("contrasena")
                .defaultSuccessUrl("/redirectPorRol",true);

        http.logout()
                .logoutSuccessUrl("/")
                .deleteCookies("JSESSIONID")
                .invalidateHttpSession(true);
    }

    @Autowired
    DataSource dataSource;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        auth.jdbcAuthentication()
                .dataSource(dataSource)
                .passwordEncoder(new BCryptPasswordEncoder())
                .usersByUsernameQuery("SELECT correo, contrasena, activo FROM usuarios WHERE correo = ?")
                .authoritiesByUsernameQuery("SELECT correo, rol FROM usuarios WHERE correo = ? and activo = 1");

    }
}
