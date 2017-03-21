package pl.maciejkizlich.hsbc.service;

import static com.google.common.truth.Truth.assertThat;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import com.google.common.collect.ImmutableList;

import pl.maciejkizlich.hsbc.exception.ServiceException;
import pl.maciejkizlich.hsbc.model.User;
import pl.maciejkizlich.hsbc.repository.UserRepository;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

  @Rule
  public ExpectedException ex = ExpectedException.none();

  @Mock
  private UserRepository userRepository;

  private UserService userService;

  @Test
  public void shouldRegisterNewUser() {
    userService = new UserService(userRepository);
    String username = "username";

    userService.registerNewUser(username);

    verify(userRepository).save(new User(username));
  }

  @Test
  public void shouldFindUserByUsername() {
    userService = new UserService(userRepository);

    when(userRepository.findUserByUsername(any(String.class)))
        .thenAnswer(new Answer<User>() {
          @Override
          public User answer(InvocationOnMock invocation) throws Throwable {
            return new User((String) invocation.getArguments()[0]);
          }
        });

    String username = "username";

    User user = userService.findUserByUsername(username);

    assertThat(user.getUsername()).isEqualTo(username);
  }

  @Test
  public void shouldFollowUser() throws ServiceException {
    userService = new UserService(userRepository);

    User follower = new User("follower");
    User followee = new User("followee");

    when(userRepository.findUserByUsername("follower")).thenReturn(follower);
    when(userRepository.findUserByUsername("followee")).thenReturn(followee);

    userService.followUser(follower.getUsername(), followee.getUsername());

    assertThat(follower.getFollowedUsers()).contains(followee);
  }

  @Test
  public void shouldFollowUser_followerNotFound() throws ServiceException {
    userService = new UserService(userRepository);

    ex.expect(ServiceException.class);
    ex.expectMessage("Follower not found.");

    userService.followUser("follower", "followee");
  }

  @Test
  public void shouldFollowUser_followeeNotFound() throws ServiceException {
    userService = new UserService(userRepository);

    User follower = new User("follower");

    when(userRepository.findUserByUsername("follower")).thenReturn(follower);

    ex.expect(ServiceException.class);
    ex.expectMessage("Followee not found.");

    userService.followUser(follower.getUsername(), "followee");
  }

  @Test
  public void shouldGetUserFollowees_userNotFound() {
    userService = new UserService(userRepository);

    ImmutableList<User> userFollowees = userService.getUserFollowees(null);

    assertThat(userFollowees).isEmpty();
  }

  @Test
  public void shouldGetUserFollowees() {
    userService = new UserService(userRepository);

    User follower = new User("follower");
    User followee = new User("followee");
    follower.follow(followee);

    when(userRepository.findUserByUsername("follower")).thenReturn(follower);

    ImmutableList<User> userFollowees = userService.getUserFollowees(follower.getUsername());

    assertThat(userFollowees).contains(followee);
  }
}
