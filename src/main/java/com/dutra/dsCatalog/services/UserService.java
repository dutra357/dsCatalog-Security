package com.dutra.dsCatalog.services;

import com.dutra.dsCatalog.dtos.RoleDto;
import com.dutra.dsCatalog.dtos.UserDto;
import com.dutra.dsCatalog.dtos.UserInsertDto;
import com.dutra.dsCatalog.entities.Role;
import com.dutra.dsCatalog.entities.User;
import com.dutra.dsCatalog.repositories.RoleRepository;
import com.dutra.dsCatalog.repositories.UserRepository;
import com.dutra.dsCatalog.services.exceptions.DataBaseException;
import com.dutra.dsCatalog.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository repository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository repository, RoleRepository roleRepository, BCryptPasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public Page<UserDto> findAllPaged(Pageable pageable) {
        return repository.findAll(pageable).map(user -> new UserDto(user));
    }

    @Transactional(readOnly = true)
    public UserDto findById(Long id) {
        return new UserDto(repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Category not found!")
        ));
    }

    @Transactional
    public UserDto InsertUser(UserInsertDto userInsertDto) {
        User user = new User();
        user.setPassword(passwordEncoder.encode(userInsertDto.getPassword()));

        copyToEntity(user, userInsertDto);

        return new UserDto(repository.save(user));
    }

    @Transactional
    public UserDto updateUser(Long id, UserDto userDto) {
        try {
            User user = repository.getReferenceById(id);

            copyToEntity(user, userDto);

            return new UserDto(repository.save(user));
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("ID not found!");
        }
    }

    private void copyToEntity(User user, UserDto userDto) {
        user.setFirstName(userDto.getFirstName());
        user.setLastname(userDto.getLastName());
        user.setEmail(userDto.getEmail());

        user.getRoles().clear();
        for (RoleDto roleDto : userDto.getRoles()) {
            Role role = roleRepository.getReferenceById(roleDto.getId());
            user.getRoles().add(role);
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id) {

        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Resource not found!");
        }

        try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DataBaseException("Referential integrity violation.");
        }
    }
}
