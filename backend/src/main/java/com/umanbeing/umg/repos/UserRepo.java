package com.umanbeing.umg.repos;

import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends org.springframework.data.jpa.repository.JpaRepository<com.umanbeing.umg.models.User, Long> {

}
