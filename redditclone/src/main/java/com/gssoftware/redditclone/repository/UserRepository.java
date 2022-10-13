package com.gssoftware.redditclone.repository;


import com.gssoftware.redditclone.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
