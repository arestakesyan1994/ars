package com.example.rest.repository;


import com.example.rest.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findAllByUsername(String userUame);
    User findById(int id);
    User findAllByUsernameAndPassword(String username, String password);

}
