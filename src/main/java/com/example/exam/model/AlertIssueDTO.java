package com.example.exam.model;

import com.example.exam.model.entity.AlertIssue;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
public class AlertIssueDTO {

    private String stationName;
    private AlertLevel alertLevel;
    @JsonFormat(pattern = "yyyy-MM-dd HH", timezone = "Asia/Seoul")
    private Timestamp alertAt;

    public static AlertIssueDTO fromEntity(AlertIssue entity){
        return AlertIssueDTO.builder()
                .stationName(entity.getStationId().getStationName())
                .alertLevel(entity.getAlertLevel())
                .alertAt(entity.getAlertAt())
                .build();
    }

    @Override
    public String toString(){
        return "지역: " + this.stationName + "\n"
                + "경보 단계: " + this.alertLevel + "\n"
                + "경보 발령 시간: " + this.alertAt;
    }
}
