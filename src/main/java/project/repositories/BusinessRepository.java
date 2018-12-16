package project.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

import project.models.Business;

public interface BusinessRepository extends CrudRepository<Business, Integer> {
  @Query("SELECT b FROM Business b WHERE b.yelpId=:yelpId")
  Optional<Business> findByYelpId(
          @Param("yelpId") String yelpId);
}
