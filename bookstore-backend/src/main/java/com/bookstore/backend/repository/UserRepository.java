// repository/UserRepository.java
package com.bookstore.backend.repository;

import com.bookstore.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByEmail(String email);

	Boolean existsByEmail(String email);

	List<User> findByIsActiveTrue();

	List<User> findByRole(User.Role role);

	@Query("SELECT u FROM User u WHERE u.firstName LIKE %:keyword% OR u.lastName LIKE %:keyword% OR u.email LIKE %:keyword%")
	List<User> searchUsers(@Param("keyword") String keyword);

	@Query("SELECT COUNT(u) FROM User u WHERE u.role = :role AND u.isActive = true")
	Long countActiveUsersByRole(@Param("role") User.Role role);
}