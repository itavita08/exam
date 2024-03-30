package com.example.exam.model;

import com.example.exam.model.entity.Station;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class MeasurementDTO {

    private int id;
    private Station stationId;
    private Timestamp measuredAt;
    private Integer pm10;
    private Integer pm25;
}
