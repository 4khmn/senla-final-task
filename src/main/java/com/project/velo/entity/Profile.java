package com.project.velo.entity;

import com.project.velo.util.ValidationConstants;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "profiles")
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;


    @Size(max = 50, message = "Имя не может быть длиннее 50 символов")
    @Column(name = "first_name", length = 50)
    private String firstName;

    @Size(max = 50, message = "Фамилия не может быть длиннее 50 символов")
    @Column(name = "last_name", length = 50)
    private String lastName;

    @Pattern(
            regexp = ValidationConstants.PHONE_REGEX,
            message = "Номер телефона должен быть в формате +7... или 8... (всего 11 цифр)"
    )
    @Column(length = 12, unique = true)
    private String phone;

    @Size(max = 500, message = "О себе: максимум 500 символов")
    @Column(columnDefinition = "TEXT")
    private String bio;

    @Size(max = 255, message = "Ссылка на аватар слишком длинная")
    @Column(name = "avatar_url")
    private String avatarUrl;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Profile profile = (Profile) o;
        return id != null && id.equals(profile.id);
    }

    @Override
    public final int hashCode() {
        return getClass().hashCode();
    }




}
