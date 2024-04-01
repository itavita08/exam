package com.example.exam.model;

import com.example.exam.model.entity.AlertIssue;
import com.example.exam.model.entity.Station;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
public class AlertIssueDTO {

    private String stationName;
    private AlertLevel alertLevel;
    private Timestamp alertAt;

    public static AlertIssueDTO fromEntity(AlertIssue entity){
        return AlertIssueDTO.builder()
                .stationName(entity.getStationId().getStationName())
                .alertLevel(entity.getAlertLevel())
                .alertAt(entity.getAlertAt())
                .build();
    }
}
