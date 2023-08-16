package ru.practicum.server.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "stats", schema = "public")
public class EndpointHit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //айди запроса
    @Column(name = "app")
    private String app;  //id сервиса, для которого записывается информация
    @Column(name = "uri")
    private String uri; // URI для которого был осуществлен запрос
    @Column(name = "ip")
    private String ip; // ip-адрес пользователя, осуществившего запрос
    @Column(name = "timestamp")
    private LocalDateTime timestamp; //когда создан запрос к эндпоинту
}
