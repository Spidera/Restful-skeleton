package dov.projects.restfulskeleton.services;

import dov.projects.restfulskeleton.controllers.UserRestController;
import dov.projects.restfulskeleton.controllers.errorhandling.GlobalExceptionHandler;
import dov.projects.restfulskeleton.dao.UserRepository;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.ExceptionHandlerMethodResolver;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@Test
@ContextConfiguration(locations = {"classpath:/applicationContext-test-controllers.xml"})
@WebAppConfiguration
public class UserRestControllerTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private UserRestController userRestController;

    private MockMvc mockMvc;

    @Mock
    private UserRepository userRepository;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        mockMvc = standaloneSetup(userRestController)
                .setHandlerExceptionResolvers(createExceptionResolver())
                .build();

        // TODO: should I keep the mock or use real repository?
//        userRestController.setUserRepository(userRepository);
    }

//    public void shouldHandleUserNotFoundException() throws Exception {
//        int errorCodeValue = ErrorCode.USER_NOT_FOUND.getValue();
//        long userId = 22;
//        String exceptionMessage = "Could not find user '" + userId + "'";
//        Gson gson = new Gson();
//        MockHttpServletResponse response;
//        int httpStatusBadRequestValue = HttpStatus.NOT_FOUND.value();
//
//        when(userRepository.exists(userId)).thenThrow(new UserNotFoundException(userId));
//
//        response = mockMvc.perform(get("/users/" + userId)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(gson.toJson(createUser("some password", "212@2.com", "asdb", "qwer")))
//                .accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();
//
//        ErrorResponse errorResponse = createErrorResponse(exceptionMessage, errorCodeValue, httpStatusBadRequestValue);
//
//        assertThat(gson.fromJson(response.getContentAsString(), ErrorResponse.class), is(errorResponse));
//        assertThat(response.getStatus(), is(httpStatusBadRequestValue));
//    }
//
//    public void shouldHandleGlobalException() throws Exception {
//        int errorCodeValue = ErrorCode.UNSPECIFIED_GENERIC_EXCEPTION.getValue();
//        String exceptionMessage = "some message";
//        Gson gson = new Gson();
//        MockHttpServletResponse response;
//        int httpStatusBadRequestValue = HttpStatus.INTERNAL_SERVER_ERROR.value();
//
//        when(userRepository.findAll()).thenThrow(new RuntimeException(exceptionMessage));
//
//        response = mockMvc.perform(get("/users")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(gson.toJson(createUser("some password", "212@2.com", "asdb", "qwer")))
//                .accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();
//
//        ErrorResponse errorResponse = createErrorResponse(exceptionMessage, errorCodeValue, httpStatusBadRequestValue);
//
//        assertThat(gson.fromJson(response.getContentAsString(), ErrorResponse.class), is(errorResponse));
//        assertThat(response.getStatus(), is(httpStatusBadRequestValue));
//    }
//
//    @Test(dataProvider = "jpaExceptionDataProvider")
//    public void shouldHandleJpaSystemExceptions(String exceptionMessage, int errorCodeValue) throws Exception {
//        Gson gson = new Gson();
//        MockHttpServletResponse response;
//        int httpStatusBadRequestValue = HttpStatus.BAD_REQUEST.value();
//
//        when(userRepository.save((User) anyObject())).thenThrow(new JpaSystemException(new RuntimeException(exceptionMessage)));
//
//        response = mockMvc.perform(post("/users")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(gson.toJson(createUser("some password", "212@2.com", "asdb", "qwer")))
//                .accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();
//
//        ErrorResponse errorResponse = createErrorResponse(exceptionMessage, errorCodeValue, httpStatusBadRequestValue);
//
//        assertThat(gson.fromJson(response.getContentAsString(), ErrorResponse.class), is(errorResponse));
//        assertThat(response.getStatus(), is(httpStatusBadRequestValue));
//    }
//
//    @DataProvider(name = "jpaExceptionDataProvider")
//    private Object[][] jpaExceptionDataProvider() {
//        return new Object[][]{
//                {
//                        "Data too long for column 'password' at row 1", ErrorCode.DATA_TOO_LONG_FOR_PASSWORD.getValue()
//                },
//                {
//                        "Unspecified JPA exception", ErrorCode.UNSPECIFIED_JPA_EXCEPTION.getValue()
//                }
//        };
//    }
//
//    private ErrorResponse createErrorResponse(String exceptionMessage, int value, int httpStatusBadRequestValue) {
//        ErrorResponse errorResponse = new ErrorResponse();
//        errorResponse.setMessage(exceptionMessage);
//        errorResponse.setCode(value);
//        errorResponse.setStatus(httpStatusBadRequestValue);
//        return errorResponse;
//    }
//
//    private User createUser(String password, String email, String givenName, String familyName) {
//        User user = new User();
//        user.setPassword(password);
//        user.setEmail(email);
//        user.setGivenName(givenName);
//        user.setFamilyName(familyName);
//        return user;
//    }
//
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
}