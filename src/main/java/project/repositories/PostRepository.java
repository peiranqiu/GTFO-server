package project.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

import project.models.Post;

public interface PostRepository extends CrudRepository<Post, Integer> {
  @Query("SELECT p FROM Post p WHERE p.insId=:insId")
  Optional<Post> findByInsId(
          @Param("insId") String insId);
}
