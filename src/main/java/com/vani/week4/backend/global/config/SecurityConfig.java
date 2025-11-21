package com.vani.week4.backend.global.config;
import com.vani.week4.backend.auth.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


/**
 * PasswordEncoder 빈 등록, 필터 사용 설정
 * @author vani
 * @since 10/13/25
 */
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    @Profile("loadtest")
    public PasswordEncoder loadTestPasswordEncoder() {
        return new PasswordEncoder() {
            private final String FAKE_HASH = "$2a$10$dummyPasswordHashForLoadTest";
            private final String DUMMY_PASSWORD = "dummyPassword1!";

            @Override
            public String encode(CharSequence rawPassword) {
                return new BCryptPasswordEncoder().encode(rawPassword);
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                System.out.println(">>> [DEBUG] Custom Matches Called!");
                System.out.println(">>> Input Password: " + rawPassword);
                System.out.println(">>> DB Hash: " + encodedPassword);

                boolean isFakeHashMatch = encodedPassword.equals(FAKE_HASH);
                boolean isPasswordMatch = rawPassword.toString().equals(DUMMY_PASSWORD);

                System.out.println(">>> Hash Match? " + isFakeHashMatch);
                System.out.println(">>> Password Match? " + isPasswordMatch);

                if (isFakeHashMatch && isPasswordMatch) {
                    System.out.println(">>> LOGIN SUCCESS (Bypass)");
                    return true;
                }

                System.out.println(">>> LOGIN FAILED (or Fallback)");
                return new BCryptPasswordEncoder().matches(rawPassword, encodedPassword);
            }
        };
    }


    @Bean
    @Profile("!loadtest")
    public PasswordEncoder productionPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
