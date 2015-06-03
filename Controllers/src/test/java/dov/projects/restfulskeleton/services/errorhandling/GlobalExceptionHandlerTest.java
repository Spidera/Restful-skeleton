package dov.projects.restfulskeleton.services.errorhandling;

import com.google.gson.Gson;
import dov.projects.restfulskeleton.controllers.UserRestController;
import dov.projects.restfulskeleton.controllers.errorhandling.ErrorCode;
import dov.projects.restfulskeleton.controllers.errorhandling.ErrorResponse;
import dov.projects.restfulskeleton.controllers.errorhandling.GlobalExceptionHandler;
import dov.projects.restfulskeleton.controllers.errorhandling.exception.UserNotFoundException;
import dov.projects.restfulskeleton.dao.UserRepository;
import dov.projects.restfulskeleton.model.User;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.ExceptionHandlerMethodResolver;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@Test
@ContextConfiguration(locations = {"classpath:/applicationContext-test-controllers.xml"})
@WebAppConfiguration
public class GlobalExceptionHandlerTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private UserRestController userRestController;

    private MockMvc mockMvc;

    @Mock
    private UserRepository userRepository;
    private Gson gson;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        gson = new Gson();

        mockMvc = standaloneSetup(userRestController).setHandlerExceptionResolvers(createExceptionResolver()).build();

        userRestController.setUserRepository(userRepository);
    }

    public void shouldHandleUserNotFoundException() throws Exception {
        int errorCodeValue = ErrorCode.USER_NOT_FOUND.getValue();
        long userId = 22;
        String exceptionMessage = "Could not find user '" + userId + "'";
        int httpStatusBadRequestValue = HttpStatus.NOT_FOUND.value();
        MockHttpServletRequestBuilder httpOperationAndAddress = get("/users/" + userId);

        when(userRepository.exists(userId)).thenThrow(new UserNotFoundException(userId));

        performAssertions(
                gson,
                getResponse(gson, httpOperationAndAddress),
                httpStatusBadRequestValue,
                createErrorResponse(exceptionMessage, errorCodeValue, httpStatusBadRequestValue));
    }

    public void shouldHandleGlobalException() throws Exception {
        int errorCodeValue = ErrorCode.UNSPECIFIED_GENERIC_EXCEPTION.getValue();
        String exceptionMessage = "some message";
        int httpStatusBadRequestValue = HttpStatus.INTERNAL_SERVER_ERROR.value();
        MockHttpServletRequestBuilder httpOperationAndAddress = get("/users");

        when(userRepository.findAll()).thenThrow(new RuntimeException(exceptionMessage));

        performAssertions(
                gson,
                getResponse(gson, httpOperationAndAddress),
                httpStatusBadRequestValue,
                createErrorResponse(exceptionMessage, errorCodeValue, httpStatusBadRequestValue));
    }

    @Test(dataProvider = "jpaExceptionDataProvider")
    public void shouldHandleJpaSystemExceptions(String exceptionMessage, int errorCodeValue) throws Exception {
        int httpStatusBadRequestValue = HttpStatus.BAD_REQUEST.value();
        MockHttpServletRequestBuilder httpOperationAndAddress = post("/users");

        when(userRepository.save((User) anyObject())).thenThrow(new JpaSystemException(new RuntimeException(exceptionMessage)));

        performAssertions(
                gson,
                getResponse(gson, httpOperationAndAddress),
                httpStatusBadRequestValue,
                createErrorResponse(exceptionMessage, errorCodeValue, httpStatusBadRequestValue));
    }


    @DataProvider(name = "jpaExceptionDataProvider")
    private Object[][] jpaExceptionDataProvider() {
        return new Object[][]{
                {
                        "Data too long for column 'password' at row 1", ErrorCode.DATA_TOO_LONG_FOR_PASSWORD.getValue()
                },
                {
                        "Unspecified JPA exception", ErrorCode.UNSPECIFIED_JPA_EXCEPTION.getValue()
                }
        };
    }

    private ErrorResponse createErrorResponse(String exceptionMessage, int value, int httpStatusBadRequestValue) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage(exceptionMessage);
        errorResponse.setCode(value);
        errorResponse.setStatus(httpStatusBadRequestValue);
        return errorResponse;
    }

    private User createUser() {
        User user = new User();
        user.setPassword("some password");
        user.setEmail("212@2.com");
        user.setGivenName("asdb");
        user.setFamilyName("qwer");
        return user;
    }

    private ExceptionHandlerExceptionResolver createExceptionResolver() {
        ExceptionHandlerExceptionResolver exceptionResolver = new ExceptionHandlerExceptionResolver() {
            protected ServletInvocableHandlerMethod getExceptionHandlerMethod(HandlerMethod handlerMethod, Exception exception) {
                return new ServletInvocableHandlerMethod(new GlobalExceptionHandler(),
                        new ExceptionHandlerMethodResolver(GlobalExceptionHandler.class).resolveMethod(exception));
            }
        };
        exceptionResolver.afterPropertiesSet();
        exceptionResolver.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        return exceptionResolver;
    }

    private MockHttpServletResponse getResponse(Gson gson, MockHttpServletRequestBuilder httpOperationAndAddress) throws Exception {
        return mockMvc.perform(httpOperationAndAddress
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(createUser()))
                .accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();
    }

    private void performAssertions(Gson gson,
                                   MockHttpServletResponse response,
                                   int httpStatusBadRequestValue,
                                   ErrorResponse errorResponse) throws Exception {
        assertThat(gson.fromJson(response.getContentAsString(), ErrorResponse.class), is(errorResponse));
        assertThat(response.getStatus(), is(httpStatusBadRequestValue));
    }
}