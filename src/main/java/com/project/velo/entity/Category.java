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
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Название категории не может быть пустым")
    @Size(min = 2, max = 100, message = "Название категории должно быть от 2 до 100 символов")
    @Column(length = 100, nullable = false, unique = true)
    private String name;

    @Column(name = "display_name")
    private String displayName;
}
