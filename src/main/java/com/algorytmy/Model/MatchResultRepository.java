package com.algorytmy.Model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Konrad Łyś on 06.11.2017 for usage in judge.
 */
@Repository
public interface MatchResultRepository extends JpaRepository<MatchResult, Long> {
    MatchResult findByWinner_Name(String name);
}
