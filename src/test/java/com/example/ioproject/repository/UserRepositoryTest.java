package com.example.ioproject.repository;

import com.example.ioproject.auth.model.ERole;
import com.example.ioproject.auth.model.Role;
import com.example.ioproject.auth.model.User;
import com.example.ioproject.auth.repository.RoleRepository;
import com.example.ioproject.auth.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    void testFindByUsername_thenReturnUser() {
        User user = new User("janko", "janko@mail.com", "secret");
        userRepository.save(user);

        Optional<User> found = userRepository.findByUsername("janko");

        assertTrue(found.isPresent());
        assertEquals("janko@mail.com", found.get().getEmail());
    }

    @Test
    void testFindByEmail() {
        User user = new User("kasia", "kasia@example.com", "password456");
        userRepository.save(user);

        Optional<User> found = userRepository.findByEmail("kasia@example.com");

        assertTrue(found.isPresent());
        assertEquals("kasia@example.com", found.get().getEmail());
    }

    @Test
    void testExistsByUsername() {
        User user = new User("marek", "marek@example.com", "pass789");
        userRepository.save(user);

        Boolean exists = userRepository.existsByUsername("marek");

        assertTrue(exists);
    }

    @Test
    void testExistsByEmail() {
        User user = new User("ewa", "ewa@example.com", "pass000");
        userRepository.save(user);

        Boolean exists = userRepository.existsByEmail("ewa@example.com");

        assertTrue(exists);
    }

    @Test
    void testExistsByUsernameFalse() {
        Boolean exists = userRepository.existsByUsername("nieistnieje");

        assertFalse(exists);
    }

    @Test
    void testExistsByEmailFalse() {
        Boolean exists = userRepository.existsByEmail("nieistnieje@example.com");

        assertFalse(exists);
    }

    @Test
    void saveUserWithRole_ShouldBeRetrievedCorrectly() {
        // given: pobieramy istniejącą rolę z bazy
        Role role = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("ROLE_USER not found in DB"));

        // zapisujemy użytkownika z rolą
        User user = new User("userwithrole", "withrole@example.com", "password123");
        user.getRoles().add(role);
        user = userRepository.save(user);

        // when: odczytujemy użytkownika
        Optional<User> foundUserOpt = userRepository.findByUsername("userwithrole");

        // then
        assertTrue(foundUserOpt.isPresent());
        User foundUser = foundUserOpt.get();
        assertEquals("withrole@example.com", foundUser.getEmail());
        assertEquals(1, foundUser.getRoles().size());
        assertEquals(ERole.ROLE_USER, foundUser.getRoles().iterator().next().getName());
    }

}
