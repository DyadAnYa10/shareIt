package ru.practicum.server.request.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "requests")
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequest {
    @Id
    @Column(name = "request_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "description")
    private String description;
    @JoinColumn(name = "requestor_id")
    private Long requestorId;
    @Column(name = "created", nullable = false)
    private LocalDateTime created = LocalDateTime.now();
}
