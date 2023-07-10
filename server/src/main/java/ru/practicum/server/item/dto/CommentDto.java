package ru.practicum.server.item.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDto {
    private Long id;
    private String text;
    private String authorName;
    private LocalDateTime created;
}