package de.tp.projects.usr.jpa.repositories;

import de.tp.projects.usr.jpa.entitites.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findUserByVorname(String vorname);

}
