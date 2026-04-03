package com.project.velo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "ad_images")
public class AdImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "ad_id")
    private Advertisement advertisement;

    @NotBlank
    @Size(min = 1, max = 255, message = "Путь до фотографии должен быть не более 255 символов")
    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Builder.Default
    @Column(name = "is_primary")
    private boolean isPrimary = false;

}
