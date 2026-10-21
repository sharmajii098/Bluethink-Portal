package EmployeeManagementSystem.repository;
import EmployeeManagementSystem.entity.CalendarOAuthState;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.time.OffsetDateTime;
import java.util.*;
public interface CalendarOAuthStateRepository extends JpaRepository<CalendarOAuthState,String> {
 @Lock(LockModeType.PESSIMISTIC_WRITE) @Query("select s from CalendarOAuthState s where s.stateHash=:hash")
 Optional<CalendarOAuthState> lock(@Param("hash") String hash);
 void deleteByOwnerIdAndProvider(String ownerId,String provider);
 void deleteByExpiresAtBefore(OffsetDateTime before);
}
