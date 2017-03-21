package pl.maciejkizlich.hsbc.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import pl.maciejkizlich.hsbc.exception.ServiceException;
import pl.maciejkizlich.hsbc.service.UserService;


@RestController
@RequestMapping("/users")
public class UserController {

  private final UserService userService;

  @Autowired
  private UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping(path = "/{follower}")
  @ResponseStatus(HttpStatus.OK)
  public void followUser(
      @PathVariable("follower") String follower, 
      @RequestParam("followee") String followee) throws ServiceException {
    userService.followUser(follower, followee);
  }

}
