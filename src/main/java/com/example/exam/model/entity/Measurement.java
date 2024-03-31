package com.example.exam.model.entity;

import com.example.exam.model.MeasurementDTO;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@Table(name = "measurement")
public class Measurement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "station_id")
    private Station stationId;

    @Column(name = "measured_at")
    private Timestamp measuredAt;

    private Integer pm10;

    private Integer pm25;

    public static Measurement of(MeasurementDTO dto){
        Integer pm10 = Objects.requireNonNullElse(dto.getPm10(),0);
        Integer pm25 = Objects.requireNonNullElse(dto.getPm25(),0);

        return Measurement.builder()
                .stationId(new Station(dto.getStationId(), dto.getStationName()))
                .measuredAt(dto.getMeasuredAt())
                .pm10(pm10)
                .pm25(pm25)
                .build();
    }
}
