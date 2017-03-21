package pl.maciejkizlich.hsbc.service;

import static com.google.common.truth.Truth.assertThat;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
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
import pl.maciejkizlich.hsbc.model.Twatt;
import pl.maciejkizlich.hsbc.model.User;
import pl.maciejkizlich.hsbc.repository.TwattRepository;


@RunWith(MockitoJUnitRunner.class)
public class TwattServiceTest {

  @Rule
  public ExpectedException ex = ExpectedException.none();

  @Mock
  private TwattRepository twattRepository;

  @Mock 
  private UserService userService;
  
  private TwattService twattService;
  
  @Test
  public void shouldPostNewTwatt_nullContent() throws ServiceException {
    twattService = new TwattService(twattRepository, userService);
    
    ex.expect(ServiceException.class);
    ex.expectMessage("Content must not be null.");
    
    twattService.addNewTwatt(null, "user");
  }

  @Test
  public void shouldPostNewTwatt_nullUsername() throws ServiceException {
    twattService = new TwattService(twattRepository, userService);
    
    ex.expect(ServiceException.class);
    ex.expectMessage("Username must not be null.");
    
    twattService.addNewTwatt("content", null);
  }
  
  @Test
  public void shouldPostNewTwatt_newUser() throws ServiceException {
    final String username = "notexistinguser";
    final String content = "content";

    twattService = new TwattService(twattRepository, userService);

    twattService.addNewTwatt(content, username);

    verify(userService).registerNewUser(username);
    verify(twattRepository).save(new Twatt(content, new User(username)));
  }
  
  @Test
  public void shouldPostNewTwatt_existingUser() throws ServiceException {
    final String username = "existinguser";
    final String content = "content";
    
    when(userService.findUserByUsername(any(String.class))).thenAnswer(new Answer<User>() {
      @Override
      public User answer(InvocationOnMock invocation) throws Throwable {
        return new User((String) invocation.getArguments()[0]);
      }
    });

    twattService = new TwattService(twattRepository, userService);

    twattService.addNewTwatt(content, username);

    verify(userService, never()).registerNewUser(username);
    verify(twattRepository).save(new Twatt(content, new User(username)));
  }
  
  @Test
  public void shouldGetAllTwattsByUser() {
    final String username = "username";
    
    twattService = new TwattService(twattRepository, userService);
    
    twattService.getAllTwattsByUser(username);
    
    verify(twattRepository).findAllByUsername(username);
  }
  
  @Test
  public void shouldGetFolloweesTwatts() {
    User follower = new User("follower");
    User followee = new User("followee");
    User followee2 = new User("followee2");
   
    Twatt expectedTwatt = new Twatt("test", followee);
    followee.getTwatts().add(expectedTwatt);
    follower.follow(followee);
    
    // dirty hack to ensure determinism and avoid injecting Clock into LDT.now()
    expectedTwatt.timestamp = expectedTwatt.getTimestamp().minusHours(1);

    Twatt expectedTwatt2 = new Twatt("test2", followee2);
    followee2.getTwatts().add(expectedTwatt2);
    follower.follow(followee2);
    
    when(userService.getUserFollowees(follower.getUsername())).thenReturn(ImmutableList.of(followee, followee2));

    twattService = new TwattService(twattRepository, userService);

    ImmutableList<Twatt> followeesTwatts = twattService.getFolloweesTwatts(follower.getUsername());

    assertThat(followeesTwatts).isEqualTo(ImmutableList.of(expectedTwatt2, expectedTwatt));
  }

}
