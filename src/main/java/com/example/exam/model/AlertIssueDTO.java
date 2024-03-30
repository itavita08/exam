package com.example.exam.model;

import com.example.exam.model.entity.Station;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class AlertIssueDTO {

    private int id;
    private Station stationId;
    private AlertLevel alertLevel;
    private Timestamp alertAt;
}
