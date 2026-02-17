package com.umanbeing.umg.repos;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.umanbeing.umg.models.Round;

public interface RoundRepo extends JpaRepository<Round, Long> {
    List<Round> findByGame_GameId(Long gameId);
}
