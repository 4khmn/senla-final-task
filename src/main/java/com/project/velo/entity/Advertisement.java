package com.project.velo.entity;

import com.project.velo.entity.enums.AdStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
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
    @Size(max = 255, message = "Название объявления должно быть до 255 символов")
    @Column(nullable = false)
    private String title;

    @NotBlank(message = "Описание объявления не может быть пустым")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @NotNull(message = "Цена должна быть указана")
    @PositiveOrZero(message = "Слишком большая цена или много знаков после запятой")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "is_top")
    private boolean isTop;

    @Enumerated(EnumType.STRING)
    private AdStatus status;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "advertisement", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<AdImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "advertisement", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Comment> comments = new ArrayList<>();
}
