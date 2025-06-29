package com.sarmo.authservice.initializer;

import com.sarmo.authservice.entity.Role;
import com.sarmo.authservice.enums.RoleName;
import com.sarmo.authservice.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class RoleInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    public RoleInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        for (RoleName roleName : RoleName.values()) {
            if (roleRepository.findByName(roleName.name()).isEmpty()) {
                Role role = new Role();
                role.setName(roleName.name());
                roleRepository.save(role);
            }
        }
    }
}