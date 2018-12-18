package project.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

import project.models.User;

public interface UserRepository extends CrudRepository<User, Integer> {
  @Query("SELECT u FROM User u WHERE u.name=:name")
  Optional<User> findByUsername(
          @Param("name") String name);

  @Query("SELECT u FROM User u WHERE u.insId=:insId")
  Optional<User> findByInsId(
          @Param("insId") String insId);
}
