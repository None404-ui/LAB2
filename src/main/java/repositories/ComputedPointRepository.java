package repositories;

import entities.ComputedPoint;
import entities.Function;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import entities.ComputedPointId;
@Repository
public interface ComputedPointRepository extends JpaRepository<ComputedPoint, ComputedPointId> {
    List<ComputedPoint> findByFunction(Function function);
    
    @Query("SELECT cp FROM ComputedPoint cp WHERE cp.function = :function ORDER BY cp.xValue")
    List<ComputedPoint> findByFunctionOrderByXValue(@Param("function") Function function);
    
    @Query("SELECT cp FROM ComputedPoint cp WHERE cp.function = :function ORDER BY cp.yValue")
    List<ComputedPoint> findByFunctionOrderByYValue(@Param("function") Function function);
    
    @Query("SELECT cp FROM ComputedPoint cp WHERE cp.function = :function AND cp.xValue = :xValue")
    Optional<ComputedPoint> findByFunctionAndXValue(@Param("function") Function function, @Param("xValue") Double xValue);
    
    @Query("SELECT cp FROM ComputedPoint cp WHERE cp.function = :function AND cp.xValue BETWEEN :xFrom AND :xTo")
    List<ComputedPoint> findByFunctionAndXValueBetween(@Param("function") Function function, 
                                                       @Param("xFrom") Double xFrom, 
                                                       @Param("xTo") Double xTo);
    
    long countByFunction(Function function);
    
    void deleteByFunction(Function function);
}





