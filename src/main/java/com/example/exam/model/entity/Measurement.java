package com.example.exam.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Table(name = "measurement")
public class Measurement {

    @Id
    private int id;

    @ManyToOne
    @JoinColumn(name = "station_id")
    private Station stationId;

    @Column(name = "measured_at")
    private Timestamp measuredAt;

    private Integer pm10;

    private Integer pm25;
}
