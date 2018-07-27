package project.services;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import javax.persistence.CascadeType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonIgnore;

import project.models.Category;
import project.models.Owner;
import project.models.Review;
import project.models.Schedule;
import project.models.Truck;
import project.models.User;
import project.repositories.OwnerRepository;
import project.repositories.ScheduleRepository;
import project.repositories.TruckRepository;
import project.repositories.UserRepository;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class ScheduleService {
	@Autowired
	ScheduleRepository scheduleRepository;
	@Autowired
	TruckRepository truckRepository;

	@GetMapping("/api/truck/{truckId}/schedule")
	public List<Schedule> findAllSchedulesForTruck(
			@PathVariable("truckId") int truckId) {
		Optional<Truck> data = truckRepository.findById(truckId);
		if(data.isPresent()) {
			Truck truck = data.get();
			return truck.getSchedules();
		}
		return null;		
	}
	
	@PostMapping("/api/truck/{truckId}/schedule")
	public Schedule createSchedule(@PathVariable("truckId") int truckId, @RequestBody Schedule newSchedule) {
		Optional<Truck> data = truckRepository.findById(truckId);
		if (data.isPresent()) {
			Truck truck = data.get();
			newSchedule.setTruck(truck);
			return scheduleRepository.save(newSchedule);
		}
		return null;
	}
	

	@DeleteMapping("/api/schedue/{scheduleId}")
	public void deleteSchedule(@PathVariable("scheduleId") int scheduleId)
	{
		scheduleRepository.deleteById(scheduleId);
	}
	
	@GetMapping("/api/schedule")
	public List<Schedule> findAllSchedules()
	{
		return (List<Schedule>) scheduleRepository.findAll();
	}
	
	@GetMapping("/api/schedule/{scheduleId}")
	public Schedule findScheduleById(@PathVariable("scheduleId") int scheduleId) {
		Optional<Schedule> data = scheduleRepository.findById(scheduleId);
		if (data.isPresent()) {
			return data.get();
		} else {
			return null;
		}
	}
	
	@PutMapping("/api/schedule/{scheduleId}")
	public Schedule updateSchedule(@PathVariable("scheduleId") int scheduleId, @RequestBody Schedule newSchedule) {
		Optional<Schedule> data = scheduleRepository.findById(scheduleId);
		if (data.isPresent()) {
			Schedule schedule = data.get();
			schedule.setId(newSchedule.getId());
			schedule.setOpen(newSchedule.isOpen());
			schedule.setLatitude(newSchedule.getLatitude());
			schedule.setLongitude(newSchedule.getLongitude());
			schedule.setTime(newSchedule.getTime());
			schedule.setTruck(newSchedule.getTruck());

			scheduleRepository.save(schedule);
			return schedule;
		}
		return null;
	}
}