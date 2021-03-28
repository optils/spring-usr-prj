package de.tp.projects.usr.converter;

import de.tp.projects.usr.controller.model.UserDto;
import de.tp.projects.usr.jpa.entitites.User;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class UsrEntityDtoConverter implements Function<User, UserDto> {

    @Override
    public UserDto apply(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setVorname(user.getVorname());
        dto.setName(user.getName());
        return dto;
    }

}
