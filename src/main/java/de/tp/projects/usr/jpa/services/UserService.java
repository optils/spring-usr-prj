package de.tp.projects.usr.jpa.services;

import de.tp.projects.usr.controller.model.UserDto;
import de.tp.projects.usr.converter.UsrDtoEntityConverter;
import de.tp.projects.usr.exceptions.UserAlreadyExistsException;
import de.tp.projects.usr.exceptions.UserNotFoundException;
import de.tp.projects.usr.exceptions.UserServiceException;
import de.tp.projects.usr.jpa.entitites.User;
import de.tp.projects.usr.jpa.repositories.UserRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final UsrDtoEntityConverter converter;

    public UserService(UserRepository userRepository, UsrDtoEntityConverter converter) {
        this.userRepository = userRepository;
        this.converter = converter;
    }

    public void deleteUserById(final Long id) {
        logger.debug("delete user by id {}", id);

        try {
            this.getUserById(id);
            userRepository.deleteById(id);
        } catch(UserNotFoundException e){
            throw e;
        } catch(Exception e) {
            final String msg = "Error deleting User by id " + id;
            throw new UserServiceException(msg, e);
        }
    }

    public User getUserById(final Long id) {
        logger.debug("get user by id {}", id);

        Optional<User> optionalUser;

        try {
            optionalUser = userRepository.findById(id);
        } catch(Exception e) {
            final String msg = String.format("Error querying user by id %s", id);
            throw new UserServiceException(msg, e);
        }

        if(optionalUser.isEmpty()) {
            final String msg = String.format("User with id %s not found.", id);
            throw new UserNotFoundException(msg);
        } else {
            return optionalUser.get();
        }
    }

    public List<User> getAllUsers() {
        logger.debug("get all users");
        try {
            return userRepository.findAll();
        } catch(Exception e) {
            final String msg = "Error querying all users";
            throw new UserServiceException(msg, e);
        }
    }

    public UserDto updateUser(UserDto dto) {
        logger.debug("update user");
        Optional<User> optUser = userRepository.findById(dto.getId());

        if(optUser.isEmpty()) {
            final String msg = String.format("User with id %s not found.", dto.getId());
            throw new UserNotFoundException(msg);
        }

        User user = optUser.get();

        user.setEmail(dto.getEmail());
        user.setVorname(dto.getVorname());
        user.setName(dto.getName());

        try {
            userRepository.save(user);
        } catch(Exception e) {
            final String msg = String.format("error while updating user with id %s", dto.getId());
            throw new UserServiceException(msg, e);
        }

        return dto;
    }

    public User createUser(UserDto dto) {
        logger.debug("create user");
        User user = converter.apply(dto);

        try {
            return userRepository.save(user);

        } catch(DataIntegrityViolationException e) {
            if(ConstraintViolationException.class.equals(e.getCause().getClass())) {
                ConstraintViolationException cve = (ConstraintViolationException) e.getCause();
                if(cve.getConstraintName().toUpperCase(Locale.ROOT).contains(User.UNIQUE_USER_CONSTRAINT)) {
                    final String msg = String.format("User with Name '%s' and Vorname '%s' already exists.", dto.getName(), dto.getVorname());
                    throw new UserAlreadyExistsException(msg);
                }
            }

            final String msg = String.format("error saving user %s", dto);
            throw new UserServiceException(msg, e);
        } catch (Exception e) {
            final String msg = String.format("error saving user %s", dto);
            throw new UserServiceException(msg, e);
        }

    }

    public List<User> findUsersByVorname(final String vorname) {
        try {
            return userRepository.findUserByVorname(vorname);
        } catch(Exception e) {
            final String msg = String.format("error searching users with vorname %s", vorname);
            throw new UserServiceException(msg, e);
        }
    }

}
