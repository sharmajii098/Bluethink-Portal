package EmployeeManagementSystem.repository;

import EmployeeManagementSystem.entity.Wish;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WishRepository extends JpaRepository<Wish, Long> {
    List<Wish> findByReceiverUserIdOrderByCreatedAtDesc(String receiverUserId, Pageable pageable);
    List<Wish> findBySenderUserIdOrderByCreatedAtDesc(String senderUserId, Pageable pageable);
    boolean existsByWishKey(String wishKey);
}
