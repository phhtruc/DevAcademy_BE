package com.devacademy.DevAcademy_BE.entity;

import com.devacademy.DevAcademy_BE.enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Where;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "xoa = false")
@Table(name = "NGUOIDUNG")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserEntity extends BaseEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @Column(name = "hoTen")
    String fullName;

    String email;

    @Column(name = "matKhau")
    String password;

    @Column(name = "anhDaiDien")
    String avatar;

    @Column(name = "trangThai")
    @Enumerated(EnumType.STRING)
    UserStatus status;

    @Column(name = "xoa")
    Boolean isDeleted;

    @OneToMany(mappedBy = "userEntity", fetch = FetchType.EAGER)
    private Set<UserHasRoleEntity> userHasRoles = new HashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return userHasRoles.stream()
                .map(role -> new SimpleGrantedAuthority(
                        role.getRoleEntity().getName().toString()))
                .collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserStatus.ACTIVE.equals(status);
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
        return UserStatus.ACTIVE.equals(status);
    }
}
