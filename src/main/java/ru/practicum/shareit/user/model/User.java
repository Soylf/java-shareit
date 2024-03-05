package ru.practicum.shareit.user.model;

import lombok.*;

import javax.persistence.*;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@NonNull
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "email", unique = true, nullable = false)
    private String email;
}
