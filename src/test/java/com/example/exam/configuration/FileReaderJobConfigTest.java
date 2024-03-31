package com.example.exam.configuration;

import com.example.exam.model.MeasurementDTO;
import com.example.exam.model.entity.Measurement;
import com.example.exam.repository.MeasurementRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@SpringBatchTest
@SpringBootTest(classes = {FileReaderJobConfig.class, TestBatchConfig.class})
public class FileReaderJobConfigTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private FileReaderJobConfig fileReaderJobConfig;

    @MockBean
    private MeasurementRepository measurementRepository;

    @Test
    @DisplayName("JobLauncher 실행 확인")
    void jobLauncher_test() throws Exception{
        // given
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("name", "test name")
                .addDate("date", new Date())
                .toJobParameters();

        // when
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

        // then
        Assertions.assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
        Assertions.assertThat(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
    }

    @Test
    @DisplayName("Step 실행 확인")
    void step_test() throws Exception{
        // given
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("name", "test name")
                .addDate("date", new Date())
                .toJobParameters();

        // when
        JobExecution jobExecution = jobLauncherTestUtils.launchStep("fileReadStep");
        StepExecution stepExecution = (StepExecution) ((List<?>)jobExecution.getStepExecutions()).get(0);

        // then
        Assertions.assertThat(stepExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
        Assertions.assertThat(stepExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
    }

    @Test
    @DisplayName("ItemReader 실행 확인")
    void itemReader_test() throws Exception {
        // given
        FlatFileItemReader<MeasurementDTO> itemReader = fileReaderJobConfig.itemReader();
        itemReader.open(new ExecutionContext());
        List<MeasurementDTO> itemReaderList = new ArrayList<>();
        MeasurementDTO dto = createDTO();

        // when
        for(int i=0; i<4; i++){
            itemReaderList.add(itemReader.read());
        }

        // then
        Assertions.assertThat(itemReaderList.size()).isEqualTo(4);
        Assertions.assertThat(itemReaderList.get(0)).isEqualTo(dto);
    }

    @Test
    @DisplayName("ItemProcessor 실행 확인")
    void itemProcessor_test() throws Exception {
        // given
        ItemProcessor<MeasurementDTO, Measurement> itemProcessor = fileReaderJobConfig.itemProcessor();
        MeasurementDTO dto = createDTO();
        Measurement entity = Measurement.of(dto);

        // when
        Measurement result = itemProcessor.process(dto);

        // then
        Assertions.assertThat(entity.getClass()).isEqualTo(Objects.requireNonNull(result).getClass());
        Assertions.assertThat(entity.getMeasuredAt()).isEqualTo(result.getMeasuredAt());
        Assertions.assertThat(entity.getStationId().getStationName()).isEqualTo(result.getStationId().getStationName());
        Assertions.assertThat(entity.getPm10()).isEqualTo(result.getPm10());
        Assertions.assertThat(entity.getPm25()).isEqualTo(result.getPm25());
    }


    public MeasurementDTO createDTO(){
        return MeasurementDTO.builder().measuredAt(Timestamp.valueOf("2023-03-01 01:00:00.0"))
                .stationId(111121).stationName("중구")
                .pm10(27).pm25(22)
                .build();
    }
}
