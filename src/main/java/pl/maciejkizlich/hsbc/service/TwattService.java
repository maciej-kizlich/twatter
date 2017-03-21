package pl.maciejkizlich.hsbc.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.ImmutableList;

import pl.maciejkizlich.hsbc.exception.ServiceException;
import pl.maciejkizlich.hsbc.model.Twatt;
import pl.maciejkizlich.hsbc.model.User;
import pl.maciejkizlich.hsbc.repository.TwattRepository;


/**
 * Service for handling {@code Twatt} related operations.
 * 
 * @author mkizlich
 */
@Service
public class TwattService {

  private final TwattRepository twattRepository;
  private final UserService userService;

  @Autowired
  TwattService(
      TwattRepository twattRepository, 
      UserService userService) {
    this.twattRepository = twattRepository;
    this.userService = userService;
  }
  
  /**
   * Posts new {@code Twatt} on behalf of {@code User} with given username.
   * If User does not exist, it User account is being created.
   * 
   * @param content content of the Twatt
   * @param username name of the User we want to post Twatt on behalf of
   * @throws ServiceException 
   */
  public void addNewTwatt(String content, String username) throws ServiceException {
    if (content == null) {
      throw new ServiceException("Content must not be null.");
    }

    if (username == null) {
      throw new ServiceException("Username must not be null.");
    }

    User user = userService.findUserByUsername(username);

    if (user == null) {
      user = userService.registerNewUser(username);
    }

    twattRepository.save(new Twatt(content, user));
  }

  /**
   * Gets all {@code Twatt}s from {@code user} with given username.
   * 
   * @param username username of the user we want to get all Twatts from
   */
  public ImmutableList<Twatt> getAllTwattsByUser(String username) {
    return ImmutableList.copyOf(twattRepository.findAllByUsername(username));
  }

  /**
   * Gets all {@code Twatt}s from {@code User} with given username.
   * 
   * @param username username of the user we want to get all followed Users Twatts
   */
  public ImmutableList<Twatt> getFolloweesTwatts(String username) {
    List<Twatt> userTwatts = new ArrayList<>();
    
    for (User followee : userService.getUserFollowees(username)) {
      userTwatts.addAll(followee.getTwatts());
    }

    Collections.sort(userTwatts, Collections.reverseOrder(new Comparator<Twatt>() {
      public int compare(Twatt m1, Twatt m2) {
        return m1.getTimestamp().compareTo(m2.getTimestamp());
      }
    }));
    
    return ImmutableList.copyOf(userTwatts);
  }

}
