package edu.eci.ahia.service;

import org.springframework.stereotype.Service;
import java.util.List;

import edu.eci.ahia.exception.UserNotFoundException;
import edu.eci.ahia.model.entity.User;
import edu.eci.ahia.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;

  @Transactional
  public User createUser(User entity) {

    User userToCreate = User.builder()
        .name(entity.getName())
        .email(entity.getEmail())
        .phoneNumber(entity.getPhoneNumber())
        .orders(entity.getOrders())
        .build();

    User userToSave = userRepository.save(userToCreate);

    return userToSave;

  }

  @Transactional
  public User getUserById(Long id) {

    User userToGet = userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException("User to get with id " + id + " not found"));

    return userToGet;
  }

  @Transactional
  public List<User> getAllUsers() {
    return userRepository.findAll();
  }

  @Transactional
  public void deleteUser(Long id) {

    User userToBeDeleted = userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException("User to be deleted with id " + id + " not found"));

    userRepository.delete(userToBeDeleted);

  }

  @Transactional
  public User updateUser(Long id, User entity) {

    User userToBeUpdated = userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException("User to be updated with id " + id + " not found"));

    userToBeUpdated.setName(entity.getName());
    userToBeUpdated.setEmail(entity.getEmail());
    userToBeUpdated.setPhoneNumber(entity.getPhoneNumber());

    User userUpdated = userRepository.save(userToBeUpdated);

    return userUpdated;

  }

}
