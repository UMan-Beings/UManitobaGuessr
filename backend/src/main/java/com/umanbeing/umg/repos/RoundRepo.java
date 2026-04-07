package com.umanbeing.umg.repos;

import com.umanbeing.umg.models.Round;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoundRepo extends JpaRepository<Round, Long> {

  List<Round> findByGame_GameId(Long gameId);
}
