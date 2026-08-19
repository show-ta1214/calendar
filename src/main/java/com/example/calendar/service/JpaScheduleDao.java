package com.example.calendar.service;

import com.example.calendar.entity.Schedule;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class JpaScheduleDao implements ScheduleDao {
    @PersistenceContext private EntityManager entityManager;
    @Override public Schedule save(Schedule schedule) { if (schedule.getId() == null) { entityManager.persist(schedule); return schedule; } return entityManager.merge(schedule); }
    @Override @Transactional(readOnly = true) public Optional<Schedule> findById(Long id) { return Optional.ofNullable(entityManager.find(Schedule.class, id)); }
    @Override @Transactional(readOnly = true) public List<Schedule> findBetween(LocalDate from, LocalDate to) {
        return entityManager.createQuery("select s from Schedule s where s.eventDate between :from and :to order by s.eventDate, s.startTime, s.id", Schedule.class)
                .setParameter("from", from).setParameter("to", to).getResultList();
    }
    @Override @Transactional(readOnly = true) public List<Schedule> findUpcoming(LocalDate from, int limit) {
        return entityManager.createQuery("select s from Schedule s where s.eventDate >= :from order by s.eventDate, s.startTime, s.id", Schedule.class)
                .setParameter("from", from).setMaxResults(limit).getResultList();
    }
    @Override public void deleteById(Long id) { Schedule schedule = entityManager.find(Schedule.class, id); if (schedule != null) entityManager.remove(schedule); }
}
