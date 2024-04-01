package com.example.exam.service;

import com.example.exam.model.AlertIssueDTO;
import com.example.exam.model.AlertLevel;
import com.example.exam.model.entity.AlertIssue;
import com.example.exam.model.entity.Inspection;
import com.example.exam.model.entity.Measurement;
import com.example.exam.repository.AlertIssueRepository;
import com.example.exam.repository.InspectionRepository;
import com.example.exam.repository.MeasurementRepository;
import com.example.exam.utils.TimeUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AlertIssueService {

    private final MeasurementRepository measurementRepository;
    private final InspectionRepository inspectionRepository;
    private final AlertIssueRepository alertIssueRepository;

    @Transactional
    public List<AlertIssueDTO> sendAlert(String date) throws ParseException {

        List<Measurement> measurementList = measurementRepository.findAllByMeasurementAt(TimeUtils.parseTime(date));
        List<AlertIssueDTO> alerts = new ArrayList<>();
        for(Measurement m : measurementList){
            if(m.getPm10() == 0 || m.getPm25() == 0) {
                saveInspection(m);
            } else {
                AlertLevel level = getAlertLevel(m);
                if(level != null){
                    alerts.add(saveAlertIssue(m,level));
                }
            }
        }
        return alerts;
    }

    @Transactional(propagation = Propagation.NESTED)
    public void saveInspection(Measurement m){
        inspectionRepository.saveAndFlush(Inspection.builder()
                .stationId(m.getStationId())
                .inspectionAt(m.getMeasuredAt())
                .build());
    }

    @Transactional(propagation = Propagation.NESTED)
    public AlertIssueDTO saveAlertIssue(Measurement m, AlertLevel level){
        AlertIssue entity = alertIssueRepository.saveAndFlush(AlertIssue.builder()
                        .stationId(m.getStationId())
                        .alertLevel(level)
                        .alertAt(m.getMeasuredAt())
                        .build());
        return AlertIssueDTO.fromEntity(entity);
    }

    @Transactional(readOnly = true)
    public AlertLevel getAlertLevel(Measurement m){
        if(m.getPm25() >= 75 || m.getPm10() >= 150){
            LocalDateTime minusHour = m.getMeasuredAt().toLocalDateTime().minusHours(1);
            LocalDateTime minus2Hour = m.getMeasuredAt().toLocalDateTime().minusHours(2);
            Optional<List<int[]>> measurements = measurementRepository.findByMeasurementAt(Timestamp.valueOf(minus2Hour), Timestamp.valueOf(minusHour), m.getStationId());

            if(measurements.isPresent() && measurements.get().size() >= 2){
                List<int[]> measurement = measurements.get();
                if(m.getPm25() >= 150 && measurement.get(0)[1] >= 150 && measurement.get(1)[1] >= 150) return AlertLevel.LEVEL1;
                else if(m.getPm10() >= 300 && measurement.get(0)[0] >= 300 && measurement.get(1)[0] >= 300) return AlertLevel.LEVEL2;
                else if(m.getPm25() >= 75 && measurement.get(0)[1] >= 75 && measurement.get(1)[1] >= 75) return AlertLevel.LEVEL3;
                else if(m.getPm10() >= 150 && measurement.get(0)[0] >= 150 && measurement.get(1)[0] >= 150) return AlertLevel.LEVEL4;
            }
            return null;
        }
        return null;
    }
}
