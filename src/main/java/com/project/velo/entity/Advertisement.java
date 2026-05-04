package com.project.velo.entity;

import com.project.velo.entity.enums.AdStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "advertisements")
public class Advertisement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @NotBlank(message = "Название объявления не может быть пустым")
    @Size(max = 64, message = "Название объявления должно быть до 64 символов")
    @Column(nullable = false)
    private String title;

    @NotBlank(message = "Описание объявления не может быть пустым")
    @Size(max = 2000, message = "Описание объявления должно быть до 2000 символов")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @NotNull(message = "Цена должна быть указана")
    @PositiveOrZero(message = "Цена не может быть отрицательной")
    @Digits(integer = 10, fraction = 2, message = "Цена должна быть числом (до 10 знаков до запятой и 2 после)")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    @NotNull(message = "ID категории обязателен")
    private Category category;

    @Column(name = "is_top")
    private boolean top;


    @Column(name = "top_until")
    private LocalDateTime topUntil;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private AdStatus status = AdStatus.ACTIVE;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "advertisement", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @BatchSize(size = 20)
    private List<AdImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "advertisement", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Comment> comments = new ArrayList<>();


    public void addComment(Comment comment) {
        this.comments.add(comment);
        comment.setAdvertisement(this);
    }

    public boolean isTopActive() {
        return topUntil != null && topUntil.isAfter(LocalDateTime.now());
    }
}
