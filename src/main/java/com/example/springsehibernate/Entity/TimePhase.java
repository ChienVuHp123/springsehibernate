package com.example.springsehibernate.Entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "time_phases")
@Data
public class TimePhase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "phase1_start")
    private LocalDate phase1Start;

    @Column(name = "phase1_end")
    private LocalDate phase1End;

    @Column(name = "phase2_start")
    private LocalDate phase2Start;

    @Column(name = "phase2_end")
    private LocalDate phase2End;

    @Column(name = "phase3_start")
    private LocalDate phase3Start;

    @Column(name = "phase3_end")
    private LocalDate phase3End;
}
