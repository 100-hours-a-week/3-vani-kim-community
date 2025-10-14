package com.vani.week4.backend.user.repository;

import com.vani.week4.backend.user.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author vani
 * @since 10/13/25
 */
@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, String> {
}
