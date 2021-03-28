package de.tp.projects.usr.controller;

import de.tp.projects.usr.controller.model.UserDto;
import de.tp.projects.usr.converter.UsrEntityDtoConverter;
import de.tp.projects.usr.exceptions.IdMissmatchException;
import de.tp.projects.usr.jpa.entitites.User;
import de.tp.projects.usr.jpa.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@Validated
public class UserController {

    private final UserService userService;
    private final UsrEntityDtoConverter converter;

    @Autowired
    public UserController(UserService userService, UsrEntityDtoConverter converter) {
        this.userService = userService;
        this.converter = converter;
    }

    @GetMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        List<UserDto> dtos = users.stream().map(converter).collect(Collectors.toList());
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @PostMapping(value = "/users", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDto> postUser(@RequestBody @Valid UserDto dto) {
        User user = userService.createUser(dto);
        UserDto response = converter.apply(user);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping(value = "/users/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        UserDto response = converter.apply(user);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping(value = "/users/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDto> updateUserById(@PathVariable Long id, @Valid @RequestBody UserDto dto) {
        if(dto.getId() != null && !id.equals(dto.getId())) {
            final String msg = String.format("Path and Payload ID not equal. '%s' != '%s'", id, dto.getId());
            throw new IdMissmatchException(msg);
        }
        dto.setId(id);

        dto = userService.updateUser(dto);

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @DeleteMapping(value="/users/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable Long id) {
        userService.deleteUserById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/users/findByVorname/{vorname}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserDto>> findUsersByVorname(@PathVariable @NotEmpty String vorname) {
        List<User> users = userService.findUsersByVorname(vorname);
        List<UserDto> dtos = users.stream().map(converter).collect(Collectors.toList());
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

}
