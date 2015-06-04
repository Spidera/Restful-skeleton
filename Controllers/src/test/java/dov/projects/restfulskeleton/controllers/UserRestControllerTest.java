package dov.projects.restfulskeleton.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableSet;
import dov.projects.restfulskeleton.controllers.errorhandling.ErrorCode;
import dov.projects.restfulskeleton.controllers.errorhandling.ErrorResponse;
import dov.projects.restfulskeleton.controllers.errorhandling.GlobalExceptionHandler;
import dov.projects.restfulskeleton.dao.UserRepository;
import dov.projects.restfulskeleton.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.ExceptionHandlerMethodResolver;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@Test
@ContextConfiguration(locations = {"classpath:/applicationContext-test-controllers.xml"})
@WebAppConfiguration
public class UserRestControllerTest extends AbstractTestNGSpringContextTests {

    private static final String USER_EMAIL = "email1";
    private static final long NON_EXISTING_USER_ID = 34998721504987129L;

    @Autowired
    private UserRestController userRestController;

    @Autowired
    private UserRepository userRepository;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private Set<User> expectedUsers;

    @BeforeMethod
    public void setUp() throws Exception {
        objectMapper = new ObjectMapper();

        mockMvc = standaloneSetup(userRestController).setHandlerExceptionResolvers(createExceptionResolver()).build();
    }

    @BeforeMethod
    public void populateDbWithUsers() {
        expectedUsers = newHashSet();
        expectedUsers.add(userRepository.save(createUser("pass1", USER_EMAIL, "name1", "family1")));
        expectedUsers.add(userRepository.save(createUser("pass2", "email2", "name2", "family2")));
    }

    @AfterMethod
    public void cleanupDatabase() {
        userRepository.deleteAll();
    }

    public void shouldGetAllUsers() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(get("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        Set<User> responseUsers = ImmutableSet.copyOf(objectMapper.readValue(response.getContentAsString(), User[].class));

        assertThat(responseUsers, is(expectedUsers));
        assertThat(response.getStatus(), is(HttpStatus.OK.value()));
    }

    public void shouldGetUser_whenUserExists() throws Exception {
        User expectedUser = expectedUsers.iterator().next();
        Long userId = expectedUser.getId();

        MockHttpServletResponse response = mockMvc.perform(get("/users/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        User responseUser = objectMapper.readValue(response.getContentAsString(), User.class);

        assertThat(responseUser, is(expectedUser));
        assertThat(response.getStatus(), is(HttpStatus.OK.value()));
    }

    public void shouldReturnUserNotFoundErrorObject_whenGettingUserByIdAndUserDoesNotExist() throws Exception {
        Long userId = 34998721504987129L;

        MockHttpServletResponse response = mockMvc.perform(get("/users/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        ErrorResponse expectedErrorResponse = createErrorResponse(
                "Could not find user '" + userId + "'",
                ErrorCode.USER_NOT_FOUND.getValue(),
                HttpStatus.NOT_FOUND.value());

        ErrorResponse actualErrorResponse = objectMapper.readValue(response.getContentAsString(), ErrorResponse.class);

        TestUtils.assertErrorResponsesEqual(actualErrorResponse, expectedErrorResponse);
        assertThat(response.getStatus(), is(HttpStatus.NOT_FOUND.value()));
    }

    public void shouldReturnUserNotFoundErrorObject_whenDeletingUserByIdAndUserDoesNotExist() throws Exception {
        Long userId = NON_EXISTING_USER_ID;

        MockHttpServletResponse response = mockMvc.perform(delete("/users/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        ErrorResponse expectedErrorResponse = createErrorResponse(
                "Could not find user '" + userId + "'",
                ErrorCode.USER_NOT_FOUND.getValue(),
                HttpStatus.NOT_FOUND.value());

        ErrorResponse actualErrorResponse = objectMapper.readValue(response.getContentAsString(), ErrorResponse.class);

        TestUtils.assertErrorResponsesEqual(actualErrorResponse, expectedErrorResponse);
        assertThat(response.getStatus(), is(HttpStatus.NOT_FOUND.value()));
    }

    public void shouldDeleteUser_whenUserExists() throws Exception {
        User expectedUser = expectedUsers.iterator().next();
        Long userId = expectedUser.getId();

        MockHttpServletResponse response = mockMvc.perform(delete("/users/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        expectedUsers.remove(expectedUser);

        Set<User> responseUsers = ImmutableSet.copyOf(userRepository.findAll());

        assertThat(responseUsers, is(expectedUsers));
        assertThat(response.getStatus(), is(HttpStatus.NO_CONTENT.value()));
    }


    public void shouldAddUser_whenUserWithEmailAddressDoesntExist() throws Exception {
        User newUser = createUser("pass3", "email3", "name3", "family3");

        MockHttpServletResponse response = mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser))
                .accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        Set<User> responseUsers = ImmutableSet.copyOf(userRepository.findAll());

        expectedUsers.add(newUser);

        assertThat(responseUsers, is(expectedUsers));
        assertThat(response.getStatus(), is(HttpStatus.CREATED.value()));
    }

    public void shouldReturnUserAlreadyExistsErroObject_whenUserWithEmailAddressExists() throws Exception {
        User newUser = createUser("pass3", USER_EMAIL, "name3", "family3");

        MockHttpServletResponse response = mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser))
                .accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        ErrorResponse expectedErrorResponse = createErrorResponse(
                "User with email '" + USER_EMAIL + "' already exists",
                ErrorCode.USER_EXISTS.getValue(),
                HttpStatus.CONFLICT.value());

        ErrorResponse actualErrorResponse = objectMapper.readValue(response.getContentAsString(), ErrorResponse.class);

        TestUtils.assertErrorResponsesEqual(actualErrorResponse, expectedErrorResponse);
        assertThat(response.getStatus(), is(HttpStatus.CONFLICT.value()));
    }

    private ErrorResponse createErrorResponse(String exceptionMessage, int value, int httpStatusBadRequestValue) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage(exceptionMessage);
        errorResponse.setCode(value);
        errorResponse.setStatus(httpStatusBadRequestValue);
        return errorResponse;
    }

    private User createUser(String password, String email, String givenName, String familyName) {
        User user = new User();
        user.setPassword(password);
        user.setEmail(email);
        user.setGivenName(givenName);
        user.setFamilyName(familyName);
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
}