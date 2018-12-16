package project.repositories;

import org.springframework.data.repository.CrudRepository;

import project.models.Message;

public interface MessageRepository extends CrudRepository<Message, Integer> {
}
