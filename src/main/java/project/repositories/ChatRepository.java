package project.repositories;

import org.springframework.data.repository.CrudRepository;

import project.models.Chat;

public interface ChatRepository extends CrudRepository<Chat, Integer> {
}
