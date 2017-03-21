package pl.maciejkizlich.hsbc.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pl.maciejkizlich.hsbc.model.Twatt;


public interface TwattRepository extends JpaRepository<Twatt, Long> {
  
  @Query("select t from Twatt t where t.user.username = :username order by t.timestamp desc")
  List<Twatt> findAllByUsername(@Param("username") String username);

}
