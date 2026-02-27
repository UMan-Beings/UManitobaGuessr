package com.umanbeing.umg.repos;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface UserRepo extends JpaRepository<com.umanbeing.umg.models.User, Long> {

}
