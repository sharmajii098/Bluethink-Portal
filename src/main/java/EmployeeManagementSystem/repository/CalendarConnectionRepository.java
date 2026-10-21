package EmployeeManagementSystem.repository;
import EmployeeManagementSystem.entity.CalendarConnection;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.*;
public interface CalendarConnectionRepository extends JpaRepository<CalendarConnection,Long> {
 List<CalendarConnection> findByOwnerId(String ownerId);
 Optional<CalendarConnection> findByOwnerIdAndProvider(String ownerId,String provider);
 @Lock(LockModeType.PESSIMISTIC_WRITE) @Query("select c from CalendarConnection c where c.ownerId=:owner and c.provider=:provider")
 Optional<CalendarConnection> lockOwned(@Param("owner") String owner,@Param("provider") String provider);
 @Lock(LockModeType.PESSIMISTIC_WRITE) @Query("select c from CalendarConnection c where c.id=:id")
 Optional<CalendarConnection> lock(@Param("id") Long id);
}
