package dov.projects.restfulskeleton.controllers.errorhandling;

import com.fasterxml.jackson.databind.ObjectMapper;
import dov.projects.restfulskeleton.controllers.UserRestController;
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
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.ExceptionHandlerMethodResolver;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static dov.projects.restfulskeleton.controllers.TestUtils.assertErrorResponsesEqual;
import static dov.projects.restfulskeleton.controllers.TestUtils.assertErrorResponsesEqualWithoutMessage;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@Test
@ContextConfiguration(locations = {"classpath:/applicationContext-test-controllers.xml"})
@WebAppConfiguration
public class GlobalExceptionHandlerTest extends AbstractTestNGSpringContextTests {
    private static final String EXCEPTION_MESSAGE = "Some exception";
    private static final long NON_EXISTING_USER_ID = 2212341928764312876L;

    @Autowired
    private UserRestController userRestController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeMethod
    public void setUp() throws Exception {
        objectMapper = new ObjectMapper();

        mockMvc = standaloneSetup(userRestController).setHandlerExceptionResolvers(createExceptionResolver()).build();
    }

    public void shouldHandleUserNotFoundException() throws Exception {
        int errorCodeValue = ErrorCode.USER_NOT_FOUND.getValue();
        Long userId = NON_EXISTING_USER_ID;

        String exceptionMessage = "Could not find user '" + userId + "'";
        int httpStatusBadRequestValue = HttpStatus.NOT_FOUND.value();
        MockHttpServletRequestBuilder httpOperationAndAddress = get("/users/" + userId);

        MockHttpServletResponse response = getResponseFromGetOperation(httpOperationAndAddress);

        assertErrorResponsesEqual(
                objectMapper.readValue(response.getContentAsString(), ErrorResponse.class),
                createErrorResponse(exceptionMessage, errorCodeValue, httpStatusBadRequestValue));
        assertThat(response.getStatus(), is(httpStatusBadRequestValue));
    }

    public void shouldHandleGlobalException() throws Exception {
        int errorCodeValue = ErrorCode.UNSPECIFIED_GENERIC_EXCEPTION.getValue();
        int httpStatusBadRequestValue = HttpStatus.INTERNAL_SERVER_ERROR.value();
        MockHttpServletRequestBuilder httpOperationAndAddress = delete("/users"); // unsupported operation

        MockHttpServletResponse response = getResponseFromGetOperation(httpOperationAndAddress);

        assertErrorResponsesEqualWithoutMessage(
                objectMapper.readValue(response.getContentAsString(), ErrorResponse.class),
                createErrorResponse(EXCEPTION_MESSAGE, errorCodeValue, httpStatusBadRequestValue));
        assertThat(response.getStatus(), is(httpStatusBadRequestValue));
    }

    public void shouldHandleJpaSystemExceptions() throws Exception {
        String exceptionMessage = "data exception: string data, right truncation";
        int errorCodeValue = ErrorCode.JPA_EXCEPTION.getValue();
        int httpStatusBadRequestValue = HttpStatus.BAD_REQUEST.value();

        MockHttpServletResponse response = getResponseFromPostOperation();

        assertErrorResponsesEqualWithoutMessage(
                objectMapper.readValue(response.getContentAsString(), ErrorResponse.class),
                createErrorResponse(exceptionMessage, errorCodeValue, httpStatusBadRequestValue));

        assertThat(response.getStatus(), is(httpStatusBadRequestValue));
    }

    private MockHttpServletResponse getResponseFromPostOperation() throws Exception {
        return mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUser()))
                        .accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();
    }

    private MockHttpServletResponse getResponseFromGetOperation(MockHttpServletRequestBuilder httpOperationAndAddress) throws Exception {
        return mockMvc.perform(httpOperationAndAddress
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();
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

}