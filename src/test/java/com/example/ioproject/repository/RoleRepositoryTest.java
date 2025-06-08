package com.example.ioproject.repository;

import com.example.ioproject.models.ERole;
import com.example.ioproject.models.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @Test
    void testFindByName() {
        Role role = new Role(ERole.ROLE_USER);
        roleRepository.save(role);

        Optional<Role> found = roleRepository.findByName(ERole.ROLE_USER);

        assertTrue(found.isPresent());
        assertEquals(ERole.ROLE_USER, found.get().getName());
    }

    @Test
    void testFindByNameNotFound() {
        Optional<Role> found = roleRepository.findByName(ERole.ROLE_ADMIN);

        assertFalse(found.isPresent());
    }

    @Test
    void testFindByName_ShouldReturnRoleAdmin() {
        Role role = new Role(ERole.ROLE_ADMIN);
        roleRepository.save(role);

        Optional<Role> foundRole = roleRepository.findByName(ERole.ROLE_ADMIN);

        assertTrue(foundRole.isPresent());
        assertEquals(ERole.ROLE_ADMIN, foundRole.get().getName());
    }

}
