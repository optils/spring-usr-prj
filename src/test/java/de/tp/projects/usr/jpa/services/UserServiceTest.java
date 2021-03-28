package de.tp.projects.usr.jpa.services;

import de.tp.projects.usr.controller.model.UserDto;
import de.tp.projects.usr.converter.UsrDtoEntityConverter;
import de.tp.projects.usr.exceptions.UserServiceException;
import de.tp.projects.usr.jpa.entitites.User;
import de.tp.projects.usr.jpa.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {UserService.class, UsrDtoEntityConverter.class})
class UserServiceTest {

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Test
    void testDeleteUserByIdException() {
        // findById(...) is called in deleteUserById(...)
        doThrow(new RuntimeException("test")).when(userRepository).findById(anyLong());
        Throwable throwable = assertThrows(UserServiceException.class, () -> userService.deleteUserById(1L));

        assertFalse(throwable.getMessage().isEmpty());
        assertTrue(throwable.getMessage().contains("Error deleting User by id"));
    }

    @Test
    void testGetUserByIdException() {
        doThrow(new RuntimeException("test")).when(userRepository).findById(anyLong());
        Throwable throwable = assertThrows(UserServiceException.class, () -> userService.getUserById(1L));

        assertFalse(throwable.getMessage().isEmpty());
        assertTrue(throwable.getMessage().contains("Error querying user by id"));
    }

    @Test
    void testGetAllUsersException() {
        doThrow(new RuntimeException("test")).when(userRepository).findAll();
        Throwable throwable = assertThrows(UserServiceException.class, () -> userService.getAllUsers());

        assertFalse(throwable.getMessage().isEmpty());
        assertTrue(throwable.getMessage().contains("Error querying all users"));
    }

    @Test
    void testUpdateUserException() {
        // given
        UserDto dto = new UserDto();
        dto.setId(1L);
        final User user = new User();

        // when
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        doThrow(new RuntimeException("test")).when(userRepository).save(any(User.class));
        Throwable throwable = assertThrows(UserServiceException.class, () -> userService.updateUser(dto));

        // then
        assertFalse(throwable.getMessage().isEmpty());
        assertTrue(throwable.getMessage().contains("error while updating user with id"));
    }

    @Test
    void testCreateUserException() {
        // given
        final UserDto dto = new UserDto();

        doThrow(new RuntimeException("test")).when(userRepository).save(any(User.class));
        Throwable throwable = assertThrows(UserServiceException.class, () -> userService.createUser(dto));

        assertFalse(throwable.getMessage().isEmpty());
        assertTrue(throwable.getMessage().contains("error saving user"));
    }

    @Test
    void testFindUsersByVorname() {
        doThrow(new RuntimeException("test")).when(userRepository).findUserByVorname(anyString());
        Throwable throwable = assertThrows(UserServiceException.class, () -> userService.findUsersByVorname("test"));

        assertFalse(throwable.getMessage().isEmpty());
        assertTrue(throwable.getMessage().contains("error searching users with vorname"));
    }
}
