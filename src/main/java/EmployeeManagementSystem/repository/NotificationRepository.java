package EmployeeManagementSystem.repository;

import EmployeeManagementSystem.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByEmployeeIdOrderByCreatedAtDesc(Long employeeId);

    boolean existsByRecipientUserIdAndMessage(String recipientUserId, String message);

    /**
     * Supports both V8.5+ recipient_user_id notifications and legacy rows that
     * only point at the Employee entity.
     */
    @Query("""
        select n from Notification n
        left join n.employee e
        left join e.registerEmployee account
        where n.recipientUserId = :userId
           or account.userId = :userId
        order by n.createdAt desc
    """)
    List<Notification> findForUser(@Param("userId") String userId);

    @Query("""
        select count(n) from Notification n
        left join n.employee e
        left join e.registerEmployee account
        where n.isRead = false
          and (n.recipientUserId = :userId or account.userId = :userId)
    """)
    long countUnreadForUser(@Param("userId") String userId);

    @Query("select count(n) from Notification n where n.isRead = false")
    long countAllUnread();
}
