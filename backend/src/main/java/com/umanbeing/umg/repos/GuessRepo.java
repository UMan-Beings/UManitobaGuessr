package com.umanbeing.umg.repos;

import com.umanbeing.umg.models.Guess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GuessRepo extends JpaRepository<Guess, Long> {}
