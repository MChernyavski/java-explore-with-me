package ru.practicum.event.model;

import lombok.*;

import javax.persistence.*;

@Builder
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "locations", schema = "public")
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "lat")
    private float lat; // широта
    @Column(name = "lon")
    private float lon; // долгота
}
