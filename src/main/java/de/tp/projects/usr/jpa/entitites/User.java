package de.tp.projects.usr.jpa.entitites;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import static de.tp.projects.usr.jpa.entitites.User.UNIQUE_USER_CONSTRAINT;

@Entity
@Data
@Table(uniqueConstraints = {
        @UniqueConstraint(name = UNIQUE_USER_CONSTRAINT, columnNames = {"name", "vorname", "email"})
})
public class User {

    public static final String UNIQUE_USER_CONSTRAINT = "UNIQUE_USER";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private  String name;

    @NotNull
    private String vorname;

    @NotNull
    private String email;

}
