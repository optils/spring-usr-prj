package de.tp.projects.usr.controller.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class UserDto {

    private Long id;

    @NotNull @NotEmpty
    private String name;

    @NotNull @NotEmpty
    private String vorname;

    @NotNull @NotEmpty @Email
    private String email;

}
