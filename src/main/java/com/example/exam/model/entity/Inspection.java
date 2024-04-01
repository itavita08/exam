package com.example.exam.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Entity
@Builder
@Table(name = "inspection_station")
@NoArgsConstructor
@AllArgsConstructor
public class Inspection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "station_id")
    private Station stationId;

    @Column(name = "measured_at")
    private Timestamp inspectionAt;
}
