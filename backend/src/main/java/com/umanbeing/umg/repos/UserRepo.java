package com.umanbeing.umg.repos;

import com.umanbeing.umg.models.User;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    
}
