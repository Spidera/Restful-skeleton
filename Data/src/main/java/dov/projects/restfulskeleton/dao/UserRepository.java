package dov.projects.restfulskeleton.dao;

import dov.projects.restfulskeleton.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}