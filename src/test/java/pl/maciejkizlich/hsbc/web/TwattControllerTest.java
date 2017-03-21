package pl.maciejkizlich.hsbc.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static com.google.common.truth.Truth.assertThat;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import pl.maciejkizlich.hsbc.TwatterApplication;
import pl.maciejkizlich.hsbc.model.Twatt;
import pl.maciejkizlich.hsbc.model.User;
import pl.maciejkizlich.hsbc.repository.TwattRepository;
import pl.maciejkizlich.hsbc.repository.UserRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TwatterApplication.class)
@WebAppConfiguration
public class TwattControllerTest {
  
  private MockMvc mockMvc;
  
  @Autowired
  private WebApplicationContext webApplicationContext;
  
  @Autowired
  private TwattRepository twattRepository;
  
  @Autowired
  private UserRepository userRepository;
  
  @Before
  public void setup() {
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    twattRepository.deleteAllInBatch();
    userRepository.deleteAllInBatch();
  }

  @Test
  public void postTwatt_missingContent() throws Exception {
    mockMvc.perform(post("/twatts/post"))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
        .andExpect(jsonPath("$.message", Matchers.is("Content must not be null.")));
  }
  
  @Test
  public void postTwatt_missingUsername() throws Exception {
    mockMvc.perform(post("/twatts/post?content=test"))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
        .andExpect(jsonPath("$.message", Matchers.is("Username must not be null.")));
  }

  @Test
  public void postTwatt_newUser() throws Exception {
    assertThat(userRepository.findAll()).isEmpty();
    assertThat(twattRepository.findAll()).isEmpty();
    
    mockMvc.perform(post("/twatts/post?content=test&username=user"))
        .andExpect(status().isCreated());
    
    assertThat(userRepository.findAll()).hasSize(1);
    assertThat(twattRepository.findAllByUsername("user")).hasSize(1);
  }
  
  @Test
  public void postTwatt_existingUser() throws Exception {
    userRepository.save(new User("user"));
    
    assertThat(twattRepository.findAll()).isEmpty();
    
    mockMvc.perform(post("/twatts/post?content=test&username=user"))
        .andExpect(status().isCreated());
    
    assertThat(userRepository.findAll()).hasSize(1);
    assertThat(twattRepository.findAllByUsername("user")).hasSize(1);
  }
  
  @Test
  public void postTwatt_charactersLimitExceeded() throws Exception {
    userRepository.save(new User("user"));
    
    mockMvc
        .perform(post(
            "/twatts/post?content=testtesttetesttestte"
            + "testtesttetesttesttetesttesttetesttestt"
            + "testtesttetesttesttetesttesttetesttestt"
            + "retesttesttetesttesttetesttesttetesttes"
            + "tteq&username=user"))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
        .andExpect(jsonPath("$.message", Matchers.is("Twatt must not be longer than 140 characters")));
    
    assertThat(twattRepository.findAll()).isEmpty();;
  }
  
  @Test
  public void getAllTwattsFromUser_missingUsername() throws Exception {
    mockMvc.perform(get("/twatts"))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
        .andExpect(jsonPath("$.message", Matchers.is("Required String parameter 'username' is not present")));
  }

  @Test
  public void getAllTwattsFromUser_nonExistingUser() throws Exception {
    mockMvc.perform(get("/twatts?username=notexistinguser"))
        .andExpect(status().isOk())
        .andExpect(content().json("[]"));
  }

  @Test
  public void getAllTwattsFromUser() throws Exception {
    User user = userRepository.save(new User("user"));
    twattRepository.save(new Twatt("test", user));
    
    mockMvc.perform(get("/twatts?username=user"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[*]", Matchers.hasSize(1)))
        .andExpect(jsonPath("$[0].user.username", Matchers.is("user")))
        .andExpect(jsonPath("$[0].content", Matchers.is("test")));
  }
  
  @Test
  public void getAllTwattsFromFollowees_missingUsername() throws Exception {
    mockMvc.perform(get("/twatts/"))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
        .andExpect(jsonPath("$.message", Matchers.is("Required String parameter 'username' is not present")));
  }
  
  @Test
  public void getAllTwattsFromFollowees_nonExistingUser() throws Exception {
    mockMvc.perform(get("/twatts/nonexistinguser/followeesTwatts"))
        .andExpect(status().isOk())
        .andExpect(content().json("[]"));
  }
  
  @Test
  public void getAllTwattsFromFollowees() throws Exception {
    prepareFollowingRelation();
    
    mockMvc.perform(get("/twatts/followerUser/followeesTwatts"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[*]", Matchers.hasSize(1)))
        .andExpect(jsonPath("$[0].user.username", Matchers.is("followeeUser")))
        .andExpect(jsonPath("$[0].content", Matchers.is("test")));
  }

  private void prepareFollowingRelation() {
    User followeeUseer = userRepository.save(new User("followeeUser"));
    User followerUser = new User("followerUser");
    followerUser.follow(followeeUseer);
    userRepository.save(followerUser);
    twattRepository.save(new Twatt("test", followeeUseer));
  }

}
