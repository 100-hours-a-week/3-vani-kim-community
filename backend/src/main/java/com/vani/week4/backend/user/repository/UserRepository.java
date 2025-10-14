package com.vani.week4.backend.user.repository;

import com.vani.week4.backend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author vani
 * @since 10/13/25
 */
@Repository
public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByNickname(String nickname);

    Optional<User> findByEmail(String email);       //null 체크까지 한번에
}
