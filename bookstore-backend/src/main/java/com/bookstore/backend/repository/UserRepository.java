// repository/UserRepository.java
package com.bookstore.backend.repository;

import com.bookstore.backend.entity.User;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
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

	// Add these methods to your existing UserRepository interface

	Page<User> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
			String firstName, String lastName, String email, Pageable pageable);

	Page<User> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCaseAndStatus(
			String firstName, String lastName, String email, String status, Pageable pageable);

	Page<User> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCaseAndRole(
			String firstName, String lastName, String email, User.Role role, Pageable pageable);

	Page<User> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCaseAndRoleAndStatus(
			String firstName, String lastName, String email, User.Role role, String status, Pageable pageable);

	Page<User> findByStatus(String status, Pageable pageable);

	Page<User> findByRole(User.Role role, Pageable pageable);

	Page<User> findByRoleAndStatus(User.Role role, String status, Pageable pageable);

	Long countByRole(User.Role role);

	Long countByRoleAndStatus(User.Role role, String status);

	// ============ OrderItemRepository.java ============
	// Add this method to your existing OrderItemRepository interface

	boolean existsByBookId(Long bookId);
}