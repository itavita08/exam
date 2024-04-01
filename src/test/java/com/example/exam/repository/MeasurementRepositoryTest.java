package com.example.exam.repository;

import com.example.exam.model.entity.Measurement;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class MeasurementRepositoryTest {

    @Autowired
    MeasurementRepository measurementRepository;

    @Test
    void findAll_measuredAt() throws ParseException {
        // given
        Timestamp testDate = parseTimestamp("2023-03-01 01");

        // when
        List<Measurement> entityList = measurementRepository.findAllByMeasurementAt(testDate);

        // then
        Assertions.assertThat(entityList.size()).isEqualTo(25);
        Assertions.assertThat(entityList.get(0).getClass()).isEqualTo(Measurement.class);
        Assertions.assertThat(entityList.get(0).getMeasuredAt()).isEqualTo(testDate);
    }

    public Timestamp parseTimestamp(String timestampString) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH");
        Date parsedDate = dateFormat.parse(timestampString);
        return new Timestamp(parsedDate.getTime());
    }
}
