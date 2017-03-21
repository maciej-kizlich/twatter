package pl.maciejkizlich.hsbc.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

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
import pl.maciejkizlich.hsbc.model.User;
import pl.maciejkizlich.hsbc.repository.UserRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TwatterApplication.class)
@WebAppConfiguration
public class UserControllerTest {
  
  private MockMvc mockMvc;
 
  @Autowired
  private WebApplicationContext webApplicationContext;
  
  @Autowired
  private UserRepository userRepository;
  
  @Before
  public void setup() {
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    userRepository.deleteAllInBatch();
  }

  @Test
  public void followUser_missingRequestParameter() throws Exception {
    mockMvc.perform(get("/users/badusername2"))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
        .andExpect(jsonPath("$.message", Matchers.is("Required String parameter 'followee' is not present")));
  }
  
  @Test
  public void followUser_followerNotFound() throws Exception {
    mockMvc.perform(get("/users/badusername2?followee=testuser"))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
        .andExpect(jsonPath("$.message", Matchers.is("Follower not found.")));
  }

  @Test
  public void followUser_followeeNotFound() throws Exception {
    userRepository.save(new User("existinguser"));   
    
    mockMvc.perform(get("/users/existinguser?followee=testuser"))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
        .andExpect(jsonPath("$.message", Matchers.is("Followee not found.")));
  }
  
  @Test
  public void followUser() throws Exception {
    userRepository.save(new User("existinguser"));
    userRepository.save(new User("usertofollow"));

    mockMvc.perform(get("/users/existinguser?followee=usertofollow")).andExpect(status().isOk());
  }
}
