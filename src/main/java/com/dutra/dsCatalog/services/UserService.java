package com.dutra.dsCatalog.services;

import com.dutra.dsCatalog.dtos.UserDto;
import com.dutra.dsCatalog.entities.User;
import com.dutra.dsCatalog.repositories.UserRepository;
import com.dutra.dsCatalog.services.exceptions.DataBaseException;
import com.dutra.dsCatalog.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
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
    public UserDto save(UserDto newUser) {
        User user = new User();
        user.setFirstName(newUser.getFirstName());

        return new UserDto(repository.save(user));
    }

    @Transactional
    public UserDto updateCategory(Long id, UserDto userDto) {
        try {
            User user = repository.getReferenceById(id);

            user.setFirstName(userDto.getFirstName());

            return new UserDto(repository.save(user));
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("ID not found!");
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
