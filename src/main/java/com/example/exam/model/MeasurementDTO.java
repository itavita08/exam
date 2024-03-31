package com.example.exam.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeasurementDTO implements FieldSetMapper<MeasurementDTO> {

    private String stationName;
    private int stationId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH")
    private Timestamp measuredAt;
    private Integer pm10;
    private Integer pm25;

    @Override
    public MeasurementDTO mapFieldSet(FieldSet fieldSet) throws BindException {
        Timestamp measuredAt;
        try {
            measuredAt = parseTimestamp(fieldSet.readString(0));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        return MeasurementDTO.builder().measuredAt(measuredAt).stationName(fieldSet.readString(1))
                .stationId(fieldSet.readInt(2)).pm10(fieldSet.readInt(3))
                .pm25(fieldSet.readInt(4)).build();
    }

    private Timestamp parseTimestamp(String timestampString) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH");
        Date parsedDate = dateFormat.parse(timestampString);
        return new Timestamp(parsedDate.getTime());
    }
}
