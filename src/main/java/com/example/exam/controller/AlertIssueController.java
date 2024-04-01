package com.example.exam.controller;

import com.example.exam.model.AlertIssueDTO;
import com.example.exam.service.AlertIssueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/server")
public class AlertIssueController {

    private final AlertIssueService alertIssueService;

    // 2023년 3월 1일 1시부터 데이터가 시작이지만
    // 초기에는 미세먼지가 많이 않아서 경보발령이 안걸려서 전체적으로 미세먼지 심한날 시작으로 설정
//    private static String virtualTime = "2023-03-01 01";
    private static String virtualTime = "2023-03-19 20";

    // 1시간 마다이지만 편의상 1분 마다로 변경
    @Scheduled(cron = "0 * * * * *")
    public void handleAlarm() throws ParseException {
        List<AlertIssueDTO> resultList = alertIssueService.sendAlert(virtualTime);
        virtualTime = plusTime(virtualTime);

        // 클라이언트 서버로 알림 전달
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<List<String>> requestEntity =
                new HttpEntity<>(resultList.stream().map(AlertIssueDTO::toString).toList(), headers);
        String clientServer = "http://localhost:80/client";

        restTemplate.postForObject(clientServer, requestEntity, String.class);
    }

    public String plusTime(String time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");
        LocalDateTime dateTime = LocalDateTime.parse(time, formatter).plusHours(1);
        return formatter.format(dateTime);
    }
}
