package main.vaadinui.config;

import com.vaadin.flow.spring.security.VaadinWebSecurity;
import main.vaadinui.views.LoginView;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
@Configuration
public class SecurityConfig extends VaadinWebSecurity {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth ->
                auth.requestMatchers(
                        new AntPathRequestMatcher("/images/*.png"),
                        new AntPathRequestMatcher("/line-awesome/**"),
                        new AntPathRequestMatcher("/actuator/**"),
                        new AntPathRequestMatcher("/register")
                ).permitAll()
        );

        super.configure(http);
        setLoginView(http, LoginView.class);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}