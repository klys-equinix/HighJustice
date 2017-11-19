package com.algorytmy.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.BufferedReader;
import java.io.BufferedWriter;


/**
 * Created by Konrad Łyś on 13.11.2017 for usage in judge.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerExecutable {
    private String commandLineExecution;
    private String executablePath;
    private Process process;
    private BufferedWriter writer;
    private BufferedReader reader;
}
