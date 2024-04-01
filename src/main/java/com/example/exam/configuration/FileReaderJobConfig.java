package com.example.exam.configuration;

import com.example.exam.model.MeasurementDTO;
import com.example.exam.model.entity.Measurement;
import com.example.exam.repository.MeasurementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class FileReaderJobConfig {

    private final MeasurementRepository measurementRepository;

    @Bean
    public Job fileReaderJob(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) {
        return new JobBuilder("fileReadJob", jobRepository)
                .start(fileReadStep(jobRepository, platformTransactionManager))
                .build();
    }

    @JobScope
    @Bean
    public Step fileReadStep(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager){
        return new StepBuilder("fileReadStep", jobRepository)
                .<MeasurementDTO, Measurement>chunk(100, platformTransactionManager)
                .reader(itemReader())
                .processor(itemProcessor())
                .writer(itemWriter())
                .build();
    }

    @StepScope
    @Bean
    public FlatFileItemReader<MeasurementDTO> itemReader(){
        return new FlatFileItemReaderBuilder<MeasurementDTO>()
                .name("itemRead")
                .resource(new FileSystemResource("src/main/resources/2023년3월_서울시_미세먼지.csv"))
                .encoding("UTF-8")
                .lineTokenizer(new DelimitedLineTokenizer())
                .linesToSkip(1)
                .fieldSetMapper(new MeasurementDTO())
                .build();
    }

    @StepScope
    @Bean
    public ItemProcessor<MeasurementDTO,Measurement> itemProcessor(){
        return new ItemProcessor<MeasurementDTO, Measurement>() {
            @Override
            public Measurement process(MeasurementDTO item) throws Exception {
                return Measurement.of(item);
            }
        };
    }

    @StepScope
    @Bean
    public ItemWriter<Measurement> itemWriter(){
        return entities -> measurementRepository.saveAll(entities);
    }

}
