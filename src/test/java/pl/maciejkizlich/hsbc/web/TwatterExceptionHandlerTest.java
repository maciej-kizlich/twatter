package pl.maciejkizlich.hsbc.web;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import pl.maciejkizlich.hsbc.exception.ServiceException;

@RunWith(MockitoJUnitRunner.class)
public class TwatterExceptionHandlerTest {

  @Mock
  private HttpServletRequest request;
  
  private TwatterExceptionHandler twatterExceptionHandler;
  
  @Test
  public void shouldHandleBadRequest() {
    final String expectedPath = "request path";
    final String expectedMessage = "error message";

    when(request.getRequestURL()).thenReturn(new StringBuffer(expectedPath));
    twatterExceptionHandler = new TwatterExceptionHandler();

    Map<String, String> exceptedBody = new HashMap<>();
    exceptedBody.put("path", expectedPath);
    exceptedBody.put("message", expectedMessage);

    ResponseEntity<Object> responseEntity =
        twatterExceptionHandler.badRequest(request, new ServiceException(expectedMessage));

    assertThat(responseEntity)
        .isEqualTo(new ResponseEntity<>(exceptedBody, HttpStatus.BAD_REQUEST));
  }

}
