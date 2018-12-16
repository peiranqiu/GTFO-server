package project.repositories;

import org.springframework.data.repository.CrudRepository;

import project.models.Friend;

public interface FriendRepository extends CrudRepository<Friend, Integer> {
}
