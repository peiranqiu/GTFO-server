package project.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

import project.models.Friend;

public interface FriendRepository extends CrudRepository<Friend, Integer> {
}
