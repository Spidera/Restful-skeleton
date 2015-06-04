package dov.projects.restfulskeleton.controllers;

import dov.projects.restfulskeleton.controllers.errorhandling.ErrorResponse;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class TestUtils {

    public static void assertErrorResponsesEqualWithoutMessage(ErrorResponse actualErrorResponse, ErrorResponse expectedErrorResponse) {
        assertThat(actualErrorResponse.getStatus(), is(expectedErrorResponse.getStatus()));
        assertThat(actualErrorResponse.getCode(), is(expectedErrorResponse.getCode()));
    }

    public static void assertErrorResponsesEqual(ErrorResponse actualErrorResponse, ErrorResponse expectedErrorResponse) {
        assertErrorResponsesEqualWithoutMessage(actualErrorResponse, expectedErrorResponse);
        assertThat(actualErrorResponse.getMessage(), is(expectedErrorResponse.getMessage()));
    }
}
