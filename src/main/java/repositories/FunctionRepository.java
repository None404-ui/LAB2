package repositories;

import entities.Function;
import entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FunctionRepository extends JpaRepository<Function, Integer> {
    List<Function> findByUser(User user);
    
    @Query("SELECT f FROM Function f WHERE f.user = :user AND f.name LIKE %:namePattern%")
    List<Function> findByUserAndNameContaining(@Param("user") User user, @Param("namePattern") String namePattern);
    
    @Query("SELECT f FROM Function f WHERE f.user = :user AND f.expression LIKE %:expressionPattern%")
    List<Function> findByUserAndExpressionContaining(@Param("user") User user, @Param("expressionPattern") String expressionPattern);
    
    Optional<Function> findByFunctionIdAndUser(Integer functionId, User user);
    
    long countByUser(User user);
    
    @Query("SELECT f FROM Function f WHERE f.user = :user ORDER BY f.name")
    List<Function> findByUserOrderByName(@Param("user") User user);
    
    @Query("SELECT f FROM Function f WHERE f.user = :user ORDER BY f.functionId")
    List<Function> findByUserOrderByFunctionId(@Param("user") User user);
}



