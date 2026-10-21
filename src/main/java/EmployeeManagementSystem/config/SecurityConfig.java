package EmployeeManagementSystem.config;

import EmployeeManagementSystem.security.jwt.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final EmployeeManagementSystem.security.ChatRateLimitFilter workflowLimiter;
    private final EmployeeManagementSystem.security.AiNoStoreFilter aiNoStoreFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, EmployeeManagementSystem.security.ChatRateLimitFilter workflowLimiter,
                          EmployeeManagementSystem.security.AiNoStoreFilter aiNoStoreFilter) {
        this.aiNoStoreFilter = aiNoStoreFilter;
        this.workflowLimiter = workflowLimiter;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/auth/**", "/api/auth/login", "/api/public/invitations/**", "/api/public/password-reset/**", "/api/error",
                    "/favicon.ico", "/error", "/webjars/**"
                ).permitAll()

                // All React account/session endpoints except login require a valid JWT.
                .requestMatchers("/api/auth/**").authenticated()

                // Recent AI action evidence is private to the authenticated account;
                // service-layer checks also require the CURRENT database-backed role.
                .requestMatchers(HttpMethod.GET, "/api/ai/actions/recent").authenticated()

                // AI diagnostics expose aggregate workflow telemetry and are admin-only.
                .requestMatchers(HttpMethod.GET, "/api/ai/actions/monitor", "/api/ai/actions/diagnostics", "/api/ai/model-health").hasRole("ADMIN")

                // Manager team directory must be matched before the admin/HR employee rule.
                .requestMatchers(HttpMethod.GET, "/api/employees/team").hasRole("MANAGER")

                // Match the collection URI explicitly: /employees/** may not match
                // /employees in every Spring PathPattern configuration.
                .requestMatchers("/api/employees", "/api/departments")
                    .hasAnyRole("ADMIN", "HR")
                // Employee/account administration
                .requestMatchers("/api/employees/**", "/api/departments/**")
                    .hasAnyRole("ADMIN", "HR")

                // Sensitive release modules: broad route checks plus object-level service checks.
                .requestMatchers("/api/payroll-reconciliations/**", "/api/payroll-policies", "/api/payroll-policies/**", "/api/hr-letter-templates/**").hasAnyRole("ADMIN", "HR")
                .requestMatchers("/api/calendar-integrations/**", "/api/meetings/**").authenticated()
                // Attendance
                .requestMatchers(HttpMethod.GET, "/api/attendance")
                    .hasAnyRole("ADMIN", "HR")
                .requestMatchers(HttpMethod.GET, "/api/attendance/team")
                    .hasRole("MANAGER")
                .requestMatchers(HttpMethod.GET, "/api/attendance/my")
                    .authenticated()
                .requestMatchers("/api/attendance/punch-in", "/api/attendance/punch-out")
                    .authenticated()

                // Leave
                .requestMatchers(HttpMethod.GET, "/api/leaves")
                    .hasAnyRole("ADMIN", "HR")
                .requestMatchers(HttpMethod.GET, "/api/leaves/team")
                    .hasRole("MANAGER")
                .requestMatchers(HttpMethod.GET, "/api/leaves/my", "/api/leaves/context")
                    .authenticated()
                .requestMatchers(HttpMethod.POST, "/api/leaves")
                    .authenticated()
                .requestMatchers("/api/leaves/*/status")
                    .hasAnyRole("ADMIN", "HR", "MANAGER")
                .requestMatchers("/api/leaves/*/cancel")
                    .authenticated()

                .requestMatchers(HttpMethod.GET, "/api/leave-balances/my").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/leave-balances/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/employee-hierarchy").hasRole("ADMIN")

                // Leave configuration and holiday calendar
                .requestMatchers(HttpMethod.GET, "/api/leave-types", "/api/leave-types/active", "/api/holidays").hasAnyRole("EMPLOYEE", "MANAGER", "HR", "ADMIN")
                .requestMatchers("/api/leave-types/**", "/api/leave-policies/**", "/api/holidays/**", "/api/leave-balance/**")
                    .hasAnyRole("ADMIN", "HR")

                // Timesheets
                .requestMatchers(HttpMethod.GET, "/api/timesheets")
                    .hasAnyRole("ADMIN", "HR")
                .requestMatchers(HttpMethod.GET, "/api/timesheets/team")
                    .hasRole("MANAGER")
                .requestMatchers(HttpMethod.GET, "/api/timesheets/my")
                    .authenticated()
                .requestMatchers(HttpMethod.POST, "/api/timesheets")
                    .hasAnyRole("EMPLOYEE", "MANAGER", "HR")
                .requestMatchers("/api/timesheets/*/status")
                    .hasAnyRole("ADMIN", "HR", "MANAGER")

                // Performance management: role/ownership rules are enforced in the controller.
                .requestMatchers("/api/performance/**").authenticated()
                .requestMatchers("/api/announcements/**", "/api/policies/**").authenticated()

                // Projects
                // Employee self-service documents; ownership and management checks are enforced by the controller.
                .requestMatchers("/api/documents/**").authenticated()
                .requestMatchers("/api/training/**").authenticated()
                .requestMatchers("/api/assets/**").authenticated()
                .requestMatchers("/api/recruitment/**").authenticated()
                .requestMatchers("/api/expenses/**", "/api/travel/**").authenticated()
                .requestMatchers("/api/workforce/**").authenticated()
                .requestMatchers("/api/separations/**").authenticated()
                .requestMatchers("/api/probation-reviews/**").authenticated()
                .requestMatchers("/api/attendance-regularizations/**").authenticated()
                .requestMatchers("/api/letter-requests/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/overtime").hasAnyRole("ADMIN", "HR", "MANAGER")
                .requestMatchers(HttpMethod.PATCH, "/api/overtime/*/review").hasAnyRole("ADMIN", "HR", "MANAGER")
                .requestMatchers("/api/overtime/**").authenticated()
                .requestMatchers("/api/employee-360-feedback/**").authenticated()
                .requestMatchers("/api/referrals/**").authenticated()
                .requestMatchers("/api/emergency-contacts/**").authenticated()
                .requestMatchers("/api/tax-declarations/**").authenticated()
                .requestMatchers("/api/profile-completion/**").authenticated()
                .requestMatchers("/api/shift-swaps/**").authenticated()
                .requestMatchers("/api/transport-requests/**").authenticated()
                .requestMatchers("/api/asset-requests/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/recognitions").hasAnyRole("ADMIN", "HR")
                .requestMatchers(HttpMethod.PATCH, "/api/recognitions/*/review").hasAnyRole("ADMIN", "HR")
                .requestMatchers("/api/recognitions/my", "/api/recognitions").authenticated()
                .requestMatchers("/api/support-tickets/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/projects").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/projects").hasAnyRole("ADMIN", "HR")
                .requestMatchers(HttpMethod.PUT, "/api/projects/*").hasAnyRole("ADMIN", "HR")
                .requestMatchers(HttpMethod.DELETE, "/api/projects/*").hasAnyRole("ADMIN", "HR")
                .requestMatchers(HttpMethod.POST, "/api/projects/*/assign").hasAnyRole("ADMIN", "HR", "MANAGER")

                // Payroll
                .requestMatchers("/api/salaries")
                    .hasAnyRole("ADMIN", "HR")
                .requestMatchers("/api/salaries/my")
                    .authenticated()
                .requestMatchers(HttpMethod.PATCH, "/api/salaries/*/payment-status").hasAnyRole("ADMIN", "HR")
                .requestMatchers("/api/salaries/generate/**", "/api/salary-structures/**")
                    .hasRole("HR")
                .requestMatchers("/api/salaries/monthly")
                    .hasAnyRole("ADMIN", "HR")

                // Notifications
                .requestMatchers(HttpMethod.GET, "/api/notifications")
                    .hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/notifications/stream", "/api/notifications/my")
                    .authenticated()
                .requestMatchers(HttpMethod.GET, "/api/notifications/unread-count")
                    .authenticated()
                .requestMatchers(HttpMethod.GET, "/api/notifications/diagnostics")
                    .authenticated()
                .requestMatchers(HttpMethod.PATCH, "/api/notifications/read-all")
                    .authenticated()
                .requestMatchers(HttpMethod.PATCH, "/api/notifications/*/read")
                    .authenticated()

                // HR activity/audit feed and detailed activity audit
                .requestMatchers("/api/activity/visible").authenticated()
                .requestMatchers("/api/activity", "/api/activity-feed/**").hasAnyRole("ADMIN", "HR")

                // Reports
                .requestMatchers("/api/reports/**")
                    .hasAnyRole("ADMIN", "HR")

                // Everything else under the REST API requires a valid JWT.
                .requestMatchers("/api/manager-one-on-ones/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/talent-succession/my", "/api/talent-succession/employee/my").authenticated()
                .requestMatchers("/api/talent-succession/**").hasAnyRole("ADMIN", "HR", "MANAGER")
                .requestMatchers("/api/employee-suggestions/**").authenticated()
                .requestMatchers("/api/policy-acknowledgements/**").authenticated()
                .requestMatchers("/api/employee-skills/**").authenticated()
                .requestMatchers("/api/**").authenticated()

                .anyRequest().authenticated()
            )
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"success\":false,\"message\":\"Authentication required\"}");
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"success\":false,\"message\":\"Access denied for this role\"}");
                })
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(aiNoStoreFilter, JwtAuthenticationFilter.class)
            .addFilterAfter(workflowLimiter, JwtAuthenticationFilter.class);

        return http.build();
    }

    // Filter is registered only within the Spring Security chain, not twice as a servlet filter.
    @Bean
    public org.springframework.boot.web.servlet.FilterRegistrationBean<EmployeeManagementSystem.security.AiNoStoreFilter> aiNoStoreRegistration(EmployeeManagementSystem.security.AiNoStoreFilter filter) {
        var registration = new org.springframework.boot.web.servlet.FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }

    @Bean
    public org.springframework.boot.web.servlet.FilterRegistrationBean<EmployeeManagementSystem.security.ChatRateLimitFilter> workflowLimiterRegistration(EmployeeManagementSystem.security.ChatRateLimitFilter filter) {
        var registration = new org.springframework.boot.web.servlet.FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }
}
