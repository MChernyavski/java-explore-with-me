package ru.practicum.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.request.model.Request;


import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findAllByEventId(Long eventId);

    Boolean existsRequestByRequesterIdAndEventId(Long userId, Long EventId);

    List<Request> findAllByRequesterId(Long userId);

    List<Request> findRequestByIdInAndEventId(List<Long> ids, Long eventId);

    @Query("select count(r) from Request r where r.event.id = :eventId and r.status = 'CONFIRMED'")
    Long getConfirmedRequestsByEvent(@Param("eventId") Long eventId);

    @Query("select r from Request r where r.id in (:ids)")
    List<Request> findRequestsById(@Param("ids") List<Long> ids);
}
