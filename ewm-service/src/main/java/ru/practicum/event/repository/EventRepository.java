package ru.practicum.event.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.category.model.Category;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.enums.EventState;

import javax.swing.plaf.nimbus.State;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    Boolean existsByCategory(Category category);

    List<Event> findAllByInitiatorId(long userId, PageRequest pageRequest);

    Event findByIdAndInitiatorId(Long userId, Long eventId);

    @Query("select e from Event e " +
            "where (:users is null or e.initiator.id in :users) " +
            "and (:states is null or e.state in :states) " +
            "and (:categories is null or e.category.id in :categories) " +
            "and (cast(:rangeStart as timestamp) is null or e.eventDate >= :rangeStart) " +
            "and (cast(:rangeEnd as timestamp) is null or e.eventDate <= :rangeEnd)")
    List<Event> getEventsByParam(@Param("users") List<Long> users,
                                 @Param("states") List<EventState> states,
                                 @Param("categories") List<Long> categories,
                                 @Param("rangeStart") LocalDateTime rangeStart,
                                 @Param("rangeEnd") LocalDateTime rangeEnd, Pageable pageable);

    Set<Event> findAllByIdIn(Set<Long> ids);

      @Query("select e from Event e " +
            "where ((:text is null or upper(e.annotation) like upper(concat('%', :text, '%'))) " +
            "or (:text is null or upper(e.description) like upper(concat('%', :text, '%')))) " +
            "and (:state is null or e.state = :state) " +
            "and (:categories is null or e.category.id in :categories) " +
            "and (:paid is null or e.paid = :paid) " +
            "and (cast(:rangeStart as timestamp) is null or e.eventDate >= :rangeStart) " +
            "and (cast(:rangeEnd as timestamp) is null or e.eventDate <= :rangeEnd) " +
            "and (e.participantLimit > (select count(r) from Request r where e.id = r.event.id and r.status='CONFIRMED'))" +
            "order by e.eventDate desc")
    List<Event> getAvailableEventsWithFilters(@Param("text") String text,
                                              @Param("state") EventState state,
                                              @Param("categories") List<Long> categories,
                                              @Param("paid") Boolean paid,
                                              @Param("rangeStart") LocalDateTime rangeStart,
                                              @Param("rangeEnd") LocalDateTime rangeEnd,
                                              Pageable pageable);

    @Query(value = "select e from Event e where ((:text is null or upper(e.annotation) like upper(concat('%', :text, '%'))) " +
            "or (:text is null or upper(e.description) like upper(concat('%', :text, '%'))))" +
            "and (:state is null or e.state = :state) " +
            "and (:categories is null or e.category.id in :categories)" +
            "and (:paid is null or e.paid = :paid) " +
            "and (cast(:rangeStart as timestamp) is null or e.eventDate >= :rangeStart) " +
            "and (cast(:rangeEnd as timestamp) is null or e.eventDate <= :rangeEnd) " +
            "and (e.participantLimit > (select count(r) from Request r where e.id = r.event.id and r.status='CONFIRMED'))")
    List<Event> getAvailableEventsFiltersNoSort(@Param("text") String text,
                                                @Param("state") EventState state,
                                                @Param("categories") List<Long> categories,
                                                @Param("paid") Boolean paid,
                                                @Param("rangeStart") LocalDateTime rangeStart,
                                                @Param("rangeEnd") LocalDateTime rangeEnd,
                                                Pageable pageable);

    @Query("select e from Event e " +
            "where ((:text is null or upper(e.annotation) like upper(concat('%', :text, '%'))) " +
            "or (:text is null or upper(e.description) like upper(concat('%', :text, '%')))) " +
            "and (:state is null or e.state = :state) " +
            "and (:categories is null or e.category.id in :categories) " +
            "and (:paid is null or e.paid = :paid) " +
            "and (cast(:rangeStart as timestamp) is null or e.eventDate >= :rangeStart) " +
            "and (cast(:rangeEnd as timestamp) is null or e.eventDate <= :rangeEnd) " +
            "order by e.eventDate desc")
    List<Event> getAllEventsWithFilters(@Param("text") String text,
                                        @Param("state") EventState state,
                                        @Param("categories") List<Long> categories,
                                        @Param("paid") Boolean paid,
                                        @Param("rangeStart") LocalDateTime rangeStart,
                                        @Param("rangeEnd") LocalDateTime rangeEnd,
                                        Pageable pageable);

    @Query("select e from Event e" +
            " where ((:text is null or upper(e.annotation) like upper(concat('%', :text, '%'))) " +
            "or (:text is null or upper(e.description) like upper(concat('%', :text, '%')))) " +
            "and (:state is null or e.state = :state) " +
            "and (:categories is null or e.category.id in :categories) " +
            "and (:paid is null or e.paid = :paid) " +
            "and (cast(:rangeStart as timestamp) is null or e.eventDate >= :rangeStart) " +
            "and (cast(:rangeEnd as timestamp) is null or e.eventDate <= :rangeEnd)")
    List<Event> getAllEventsWithFiltersNoSorted(@Param("text") String text,
                                                @Param("state") EventState state,
                                                @Param("categories") List<Long> categories,
                                                @Param("paid") Boolean paid,
                                                @Param("rangeStart") LocalDateTime rangeStart,
                                                @Param("rangeEnd") LocalDateTime rangeEnd,
                                                Pageable pageable);

    List<Event> findAllByEventDateIsAfterAndState(
            LocalDateTime start, EventState state, Pageable pageable);
}
