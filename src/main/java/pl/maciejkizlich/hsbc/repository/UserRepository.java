package pl.maciejkizlich.hsbc.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import pl.maciejkizlich.hsbc.model.User;


public interface UserRepository extends JpaRepository<User, Long> {
  
  User findUserByUsername(String username);
  
}
