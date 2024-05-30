package com.rtechnologies.soies.config;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// import org.springframework.security.web.firewall.StrictHttpFirewall;
// import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;

// @Configuration
// @EnableWebSecurity
// public class SecurityConfiguration {

//     @Bean
//     public WebSecurityCustomizer webSecurityCustomizer() {
//         StrictHttpFirewall firewall = new StrictHttpFirewall();
//         firewall.setAllowBackSlash(true); // Allowing backslashes
//         return (web) -> web.httpFirewall(firewall);
//     }

//     // Other security configurations can be added here if needed

// }

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.security.web.firewall.HttpFirewall;
// import org.springframework.security.web.firewall.StrictHttpFirewall;

// @Configuration
// public class WebSecurityConfig {

//     @Bean
//     public HttpFirewall getHttpFirewall() {
//         StrictHttpFirewall strictHttpFirewall = new StrictHttpFirewall();
//         strictHttpFirewall.setAllowSemicolon(true); // Allowing semicolons
//         return strictHttpFirewall;
//     }

// }