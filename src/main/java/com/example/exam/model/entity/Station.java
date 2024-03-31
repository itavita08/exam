package com.example.exam.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@ToString
@Table(name = "station")
public class Station {

    @Id
    private int id;

    @Column(name = "station_name")
    private String stationName;
}
