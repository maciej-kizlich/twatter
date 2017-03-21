package pl.maciejkizlich.hsbc.web;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.ImmutableList;

import pl.maciejkizlich.hsbc.exception.ServiceException;
import pl.maciejkizlich.hsbc.model.Twatt;
import pl.maciejkizlich.hsbc.service.TwattService;


@RestController
@RequestMapping("/twatts")
public class TwattController {

  private final TwattService twattService;

  @Autowired
  private TwattController(TwattService twattService) {
    this.twattService = twattService;
  }

  @PostMapping("/post")
  @ResponseStatus(HttpStatus.CREATED)
  public void postTwatt(@RequestParam Map<String, String> params) throws ServiceException {
    twattService.addNewTwatt(params.get("content"), params.get("username"));
  }

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public ImmutableList<Twatt> getAllTwattsByUser(@RequestParam String username) {
    return twattService.getAllTwattsByUser(username);
  }

  @GetMapping(path = "/{username}/followeesTwatts")
  @ResponseStatus(HttpStatus.OK)
  public ImmutableList<Twatt> getFolloweesTwatts(
      @PathVariable("username") String username) {
    return twattService.getFolloweesTwatts(username);
  }

}
