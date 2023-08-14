package ru.practicum.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.server.model.EndpointHit;
import ru.practicum.server.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatRepository extends JpaRepository<EndpointHit, Long> {

    @Query("select new ru.practicum.server.model.ViewStats(e.app, e.uri, count(distinct e.ip)) " +
            "from EndpointHit e " +
            "where e.timestamp between :start and :end " +
            "group by e.app, e.uri " +
            "order by count(e.ip) desc")
    List<ViewStats> findAllUniqueStatsByDateBetween(@Param("start") LocalDateTime start,
                                                    @Param("end") LocalDateTime end);

    @Query("select new ru.practicum.server.model.ViewStats(e.app, e.uri, count(e.ip)) " +
            "from EndpointHit e " +
            "where e.timestamp between :start and :end " +
            "group by e.app, e.uri " +
            "order by count(e.ip) desc")
    List<ViewStats> findAllStatsByDateBetween(@Param("start") LocalDateTime start,
                                              @Param("end") LocalDateTime end);


    @Query("select new ru.practicum.server.model.ViewStats(e.app, e.uri, count(distinct e.ip)) " +
            "from EndpointHit e " +
            "where e.timestamp between :start and :end " +
            "and e.uri in :uris " +
            "group by e.app, e.uri " +
            "order by count(e.ip) desc")
    List<ViewStats> findAllUniqueStatsUriByDateBetween(@Param("start") LocalDateTime start,
                                                       @Param("end") LocalDateTime end,
                                                       @Param("uris") List<String> uris);

    @Query("select new ru.practicum.server.model.ViewStats(e.app, e.uri, count(e.ip)) " +
            "from EndpointHit e " +
            "where e.timestamp between :start and :end " +
            "and e.uri in :uris " +
            "group by e.app, e.uri " +
            "order by count(e.ip) desc")
    List<ViewStats> findAllStatsUriByDateBetween(@Param("start") LocalDateTime start,
                                                 @Param("end") LocalDateTime end,
                                                 @Param("uris") List<String> uris);
}
