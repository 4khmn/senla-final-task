package com.project.velo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ad_id", nullable = false)
    private Advertisement advertisement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id",  nullable = false)
    private User author;

    @NotBlank(message = "Комментарий не может быть пустым")
    @Size(max = 2000, message = "Комментарий слишком длинный (макс. 2000 символов)")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;


    @Builder.Default
    @Column(name = "is_pinned", nullable = false)
    private boolean isPinned = false;

}
