package com.project.velo.entity;

import com.project.velo.entity.enums.Role;
import com.project.velo.util.ValidationConstants;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Username не может быть пустым")
    @Column(nullable = false, unique = true, length = 50)
    @Size(min = 6, max = 50, message = "Username должен быть от 6 до 50 символов")
    private String username;

    @NotBlank(message = "Пароль не может быть пустым")
    @Column(nullable = false)
    @Pattern(
            regexp = ValidationConstants.PASSWORD_REGEX,
            message = "Пароль должен быть не менее 8 символов и содержать: хотя бы одну цифру, одну заглавную букву и один спецсимвол (@#$%^&+=!)"
    )
    private String password;

    @NotBlank(message = "Email не может быть пустым")
    @Email(
            regexp = ValidationConstants.EMAIL_REGEX,
            message = "Некорректный формат email"
    )
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;


    @DecimalMin(value = "0.0", message = "Рейтинг не может быть меньше 0")
    @DecimalMax(value = "5.0", message = "Рейтинг не может быть больше 5")
    @Column(precision = 3, scale = 2)
    private BigDecimal rating;

    @Column(nullable = false)
    private boolean enabled = true;


    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Profile profile;

    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Advertisement> advertisements = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id != null && id.equals(user.id);
    }

    @Override
    public final int hashCode() {
        return getClass().hashCode();
    }

}
