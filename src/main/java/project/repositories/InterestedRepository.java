package project.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

import project.models.Friend;
import project.models.Interested;

public interface InterestedRepository extends CrudRepository<Interested, Integer> {
}
