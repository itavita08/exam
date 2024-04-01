package com.example.exam.service;

import com.example.exam.model.AlertIssueDTO;
import com.example.exam.model.AlertLevel;
import com.example.exam.model.MeasurementDTO;
import com.example.exam.model.entity.AlertIssue;
import com.example.exam.model.entity.Inspection;
import com.example.exam.model.entity.Measurement;
import com.example.exam.model.entity.Station;
import com.example.exam.repository.AlertIssueRepository;
import com.example.exam.repository.InspectionRepository;
import com.example.exam.repository.MeasurementRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AlertIssueServiceTest {

    @InjectMocks @Spy
    AlertIssueService alertIssueService;
    @Mock AlertIssueRepository alertIssueRepository;
    @Mock InspectionRepository inspectionRepository;
    @Mock MeasurementRepository measurementRepository;

    @Test
    @DisplayName("pm10 또는 pm25가 0일때 측정소 점검")
    void inspection_test() {
        // given
        Measurement measurement = Measurement.of(createDTO(0, 0));

        // when
        alertIssueService.saveInspection(measurement);

        // then
        verify(inspectionRepository).saveAndFlush(any(Inspection.class));
    }

    @Test
    @DisplayName("경보 레벨 1")
    void alertIssue_level1_test(){
        // given
        Measurement measurement = Measurement.of(createDTO(300, 150));
        List<int[]> measurements = new ArrayList<>();
        measurements.add(new int[] {310, 150});
        measurements.add(new int[] {300, 200});
        given(measurementRepository.findByMeasurementAt(any(Timestamp.class), any(Timestamp.class), any(Station.class))).willReturn(Optional.of(measurements));

        // when
        AlertLevel testLevel = alertIssueService.getAlertLevel(measurement);

        // then
        assertThat(testLevel).isEqualTo(AlertLevel.LEVEL1);
    }

    @Test
    @DisplayName("경보 레벨 2")
    void alertIssue_level2_test(){
        // given
        Measurement measurement = Measurement.of(createDTO(300, 150));
        List<int[]> measurements = new ArrayList<>();
        measurements.add(new int[] {310, 150});
        measurements.add(new int[] {300, 100});
        given(measurementRepository.findByMeasurementAt(any(Timestamp.class), any(Timestamp.class), any(Station.class))).willReturn(Optional.of(measurements));

        // when
        AlertLevel testLevel = alertIssueService.getAlertLevel(measurement);

        // then
        assertThat(testLevel).isEqualTo(AlertLevel.LEVEL2);
    }

    @Test
    @DisplayName("경보 레벨 3")
    void alertIssue_level3_test(){
        // given
        Measurement measurement = Measurement.of(createDTO(300, 150));
        List<int[]> measurements = new ArrayList<>();
        measurements.add(new int[] {310, 150});
        measurements.add(new int[] {150, 75});
        given(measurementRepository.findByMeasurementAt(any(Timestamp.class), any(Timestamp.class), any(Station.class))).willReturn(Optional.of(measurements));

        // when
        AlertLevel testLevel = alertIssueService.getAlertLevel(measurement);

        // then
        assertThat(testLevel).isEqualTo(AlertLevel.LEVEL3);
    }

    @Test
    @DisplayName("경보 레벨 4")
    void alertIssue_level4_test(){
        // given
        Measurement measurement = Measurement.of(createDTO(300, 150));
        List<int[]> measurements = new ArrayList<>();
        measurements.add(new int[] {310, 150});
        measurements.add(new int[] {150, 50});
        given(measurementRepository.findByMeasurementAt(any(Timestamp.class), any(Timestamp.class), any(Station.class))).willReturn(Optional.of(measurements));

        // when
        AlertLevel testLevel = alertIssueService.getAlertLevel(measurement);

        // then
        assertThat(testLevel).isEqualTo(AlertLevel.LEVEL4);
    }

    @Test
    @DisplayName("현재시간의 먼지 농도가 기준치에 충족하지 않을 경우 쿼리 실행전에 null 반환")
    void alertIssue_return_null_no_DB(){
        // given
        Measurement measurement = Measurement.of(createDTO(50, 50));

        // when
        AlertLevel testLevel = alertIssueService.getAlertLevel(measurement);

        // then
        assertThat(testLevel).isEqualTo(null);
    }

    @Test
    @DisplayName("이전시간의 먼지 농도가 기준치에 충족하지 않을 경우 null 반환")
    void alertIssue_return_null(){
        // given
        Measurement measurement = Measurement.of(createDTO(150, 75));
        List<int[]> measurements = new ArrayList<>();
        measurements.add(new int[] {310, 150});
        measurements.add(new int[] {50, 50});
        given(measurementRepository.findByMeasurementAt(any(Timestamp.class), any(Timestamp.class), any(Station.class))).willReturn(Optional.of(measurements));

        // when
        AlertLevel testLevel = alertIssueService.getAlertLevel(measurement);

        // then
        assertThat(testLevel).isEqualTo(null);
    }

    @Test
    @DisplayName("경보 발생 후 DB에 저장")
    void alertIssue_save_test(){
        // given
        Measurement measurement = Measurement.of(createDTO(300, 150));
        AlertLevel testLevel = AlertLevel.LEVEL1;
        AlertIssue alertIssue = AlertIssue.builder().stationId(measurement.getStationId()).alertLevel(testLevel).alertAt(measurement.getMeasuredAt()).build();
        given(alertIssueRepository.saveAndFlush(any(AlertIssue.class))).willReturn(alertIssue);

        // when
        AlertIssueDTO resultDto = alertIssueService.saveAlertIssue(measurement, testLevel);

        // then
        verify(alertIssueRepository, times(1)).saveAndFlush(any(AlertIssue.class));
        assertThat(resultDto).isEqualTo(AlertIssueDTO.fromEntity(alertIssue));
    }

    @Test
    @DisplayName("경보 단계 알람 X, 측정소 점검")
    void not_alertIssue_inspection() throws ParseException {
        // given
        String time = "2023-03-01 01:00:00.0";
        Measurement measurement = Measurement.of(createDTOWithStation(111121, "중구", 0, 0));
        given(measurementRepository.findAllByMeasurementAt(any(Timestamp.class))).willReturn(List.of(measurement));

        // when
        alertIssueService.sendAlert(time);

        // then
        verify(alertIssueService).saveInspection(measurement);
    }

    @Test
    @DisplayName("경보 단계 알람 O")
    void alertIssue_test() throws ParseException {
        // given
        String time = "2023-03-01 01:00:00.0";
        Measurement measurement = Measurement.of(createDTOWithStation(111121, "중구", 300, 150));
        AlertIssue alertIssue = AlertIssue.builder().stationId(measurement.getStationId()).alertLevel(AlertLevel.LEVEL1).alertAt(measurement.getMeasuredAt()).build();
        given(measurementRepository.findAllByMeasurementAt(any(Timestamp.class))).willReturn(List.of(measurement));
        given(alertIssueService.getAlertLevel(measurement)).willReturn(AlertLevel.LEVEL1);
        given(alertIssueRepository.saveAndFlush(any(AlertIssue.class))).willReturn(alertIssue);

        // when
        List<AlertIssueDTO> resultList = alertIssueService.sendAlert(time);

        // then
        verify(alertIssueService).saveAlertIssue(measurement, AlertLevel.LEVEL1);
        assertThat(resultList.size()).isEqualTo(1);
        assertThat(resultList.get(0)).isEqualTo(AlertIssueDTO.fromEntity(alertIssue));
    }

    public MeasurementDTO createDTO(Integer pm10, Integer pm25){
        return MeasurementDTO.builder().measuredAt(Timestamp.valueOf("2023-03-01 01:00:00.0"))
                .stationId(111121).stationName("중구")
                .pm10(pm10).pm25(pm25)
                .build();
    }

    public MeasurementDTO createDTOWithStation(int stationId, String stationName, Integer pm10, Integer pm25){
        return MeasurementDTO.builder().measuredAt(Timestamp.valueOf("2023-03-01 01:00:00.0"))
                .stationId(stationId).stationName(stationName)
                .pm10(pm10).pm25(pm25)
                .build();
    }

    public AlertIssueDTO createAlertDTO(String stationName, AlertLevel level, Timestamp time){
        return AlertIssueDTO.builder().stationName(stationName)
                .alertLevel(level)
                .alertAt(time)
                .build();

    }

}
