package pl.maciejkizlich.hsbc.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.ImmutableList;

import pl.maciejkizlich.hsbc.exception.ServiceException;
import pl.maciejkizlich.hsbc.model.User;
import pl.maciejkizlich.hsbc.repository.UserRepository;


/**
 * Service for handling {@code User} related operations.
 *
 * @author mkizlich
 */
@Service
@Transactional
public class UserService {

  private final UserRepository userRepository;

  @Autowired
  UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  /**
   * Registers new {@code User}.
   * 
   * @param username username of new User.
   */
  public User registerNewUser(String username) {
    return userRepository.save(new User(username));
  }

  public User findUserByUsername(String username) {
    return userRepository.findUserByUsername(username);
  }

  /**
   * Sets following relationship between two {@code User}s.
   * 
   * @param follower User to follow another User.
   * @param followee User to be followed.
   */
  public void followUser(String follower, String followee) throws ServiceException {
    User followerUser = userRepository.findUserByUsername(follower);
    User followeeUser = userRepository.findUserByUsername(followee);
    
    if (followerUser == null) { 
      throw new ServiceException("Follower not found.");
    }
    
    if (followeeUser == null) { 
      throw new ServiceException("Followee not found.");
    }
    
    followerUser.follow(followeeUser);
  }
  
  /**
   * Gets all {@code User}s followed by User with given username.
   * 
   * @param username username of the User to get followees from
   */
  public ImmutableList<User> getUserFollowees(String username) {
    User user = userRepository.findUserByUsername(username);
    
    if (user == null) {
      return ImmutableList.of();
    }

    return ImmutableList.copyOf(user.getFollowedUsers());
  }

}
