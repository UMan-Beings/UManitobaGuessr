package com.umanbeing.umg.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.umanbeing.umg.models.Guess;

@Repository
public interface GuessRepo extends JpaRepository<Guess, Long> {
}

