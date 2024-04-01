package com.example.exam.repository;

import com.example.exam.model.entity.Measurement;
import com.example.exam.model.entity.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public interface MeasurementRepository extends JpaRepository<Measurement, Long> {

    @Query("SELECT m FROM Measurement m join fetch m.stationId WHERE m.measuredAt =:time")
    List<Measurement> findAllByMeasurementAt(@Param("time") Timestamp measuredAt);

    @Query("SELECT m.pm10, m.pm25 FROM Measurement m join fetch m.stationId WHERE m.stationId =:station AND m.measuredAt BETWEEN :time1 AND :time2 AND (m.pm10 >= 150 OR m.pm25 >= 75)")
    Optional<List<int[]>> findByMeasurementAt(@Param("time1") Timestamp time1, @Param("time2") Timestamp time2, @Param("station") Station stationId);

}
