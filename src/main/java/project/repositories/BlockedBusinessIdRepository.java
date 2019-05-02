package project.repositories;

import org.springframework.data.repository.CrudRepository;

import project.models.BlockedBusinessId;

public interface BlockedBusinessIdRepository extends CrudRepository<BlockedBusinessId, Integer> {
}
