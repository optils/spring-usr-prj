package de.tp.projects.usr.converter;

import de.tp.projects.usr.controller.model.UserDto;
import de.tp.projects.usr.jpa.entitites.User;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class UsrDtoEntityConverter implements Function<UserDto, User> {

    @Override
    public User apply(UserDto dto) {
        User user = new User();
        user.setId(dto.getId());
        user.setName(dto.getName());
        user.setVorname(dto.getVorname());
        user.setEmail(dto.getEmail());
        return user;
    }
}
