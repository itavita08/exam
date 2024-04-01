package com.example.exam.model.entity;

import com.example.exam.model.AlertLevel;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "alert_issue")
public class AlertIssue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "station_id")
    private Station stationId;

    @Enumerated(EnumType.STRING)
    private AlertLevel alertLevel;

    @Column(name = "alert_at")
    private Timestamp alertAt;
}
