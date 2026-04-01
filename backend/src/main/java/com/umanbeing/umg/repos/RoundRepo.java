package com.umanbeing.umg.repos;

import com.umanbeing.umg.models.Round;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoundRepo extends JpaRepository<Round, Long> {

    List<Round> findByGame_GameId(Long gameId);

}
