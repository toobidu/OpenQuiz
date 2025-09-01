package com.example.quizizz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import com.example.quizizz.model.entity.User;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsernameOrEmail(String username, String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);

    @Query("SELECT p.permissionName from Permission p " +
            "JOIN RolePermission rp ON p.id = rp.permissionId  " +
            "JOIN UserRole ur ON rp.roleId = ur.roleId " +
            "WHERE ur.userId = :userId")
    Set<String> findPermissionsByUserId(@Param("userId") Long userId);
}
