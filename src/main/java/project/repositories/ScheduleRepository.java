package project.repositories;

import org.springframework.data.repository.CrudRepository;

import project.models.Schedule;

public interface ScheduleRepository extends CrudRepository<Schedule, Integer> {
}
