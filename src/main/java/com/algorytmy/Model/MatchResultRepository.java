package com.algorytmy.Model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by Konrad Łyś on 06.11.2017 for usage in judge.
 */
@Repository
public interface MatchResultRepository extends JpaRepository<MatchResult, Long> {
    MatchResult findByWinner_Name(String name);

    @Query(value = "TRUNCATE TABLE games", nativeQuery = true)
    void truncate();

}
