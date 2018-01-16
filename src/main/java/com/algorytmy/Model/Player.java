package com.algorytmy.Model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by Konrad Łyś on 06.11.2017 for usage in judge.
 */
@Entity
@Data
@NoArgsConstructor
@Table(name = "players")
public class Player {

    @NotNull
    private Integer scoreDefault = 0;

    @NotNull
    private Integer scoreError = 0;

    @NotNull
    private Integer lostFromErrors = 0;

    @NotNull
    private Integer lostFromDefault = 0;

    @Id
    @Column(unique = true)
    private String name;

    @Transient
    PlayerExecutable playerExecutable;

    @Transient
    Match.FIELD_VALUE playerSignature;

    public void addToDefaultScore() {
        this.scoreDefault += 1;
    }

    public void addToErrorScore() {
        this.scoreError += 1;
    }

    public void addToLostFromErrors() { this.lostFromErrors += 1; }

    public void addToLostFromDefault() { this.lostFromDefault += 1; }

}
