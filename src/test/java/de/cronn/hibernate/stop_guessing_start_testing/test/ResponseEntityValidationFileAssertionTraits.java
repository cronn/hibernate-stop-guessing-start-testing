package de.cronn.hibernate.stop_guessing_start_testing.test;

import de.cronn.assertions.validationfile.FileExtensions;
import de.cronn.assertions.validationfile.junit5.JUnit5ValidationFileAssertions;
import de.cronn.assertions.validationfile.normalization.ValidationNormalizer;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import tools.jackson.databind.ObjectMapper;

public interface ResponseEntityValidationFileAssertionTraits extends JUnit5ValidationFileAssertions {

    ObjectMapper objectMapper();

    default void assertWithFileIncludingHttpStatus(ResponseEntity<?> response) {
        assertWithFileIncludingHttpStatus(response, defaultValidationNormalizer());
    }

    default void assertWithFileIncludingHttpStatusWithSuffix(ResponseEntity<?> response, String suffix) {
        assertWithFileIncludingHttpStatusWithSuffix(response, defaultValidationNormalizer(), suffix);
    }

    default void assertWithFileIncludingHttpStatus(ResponseEntity<?> response, ValidationNormalizer validationNormalizer) {
        assertWithFileIncludingHttpStatusWithSuffix(response, validationNormalizer, null);
    }

    default void assertWithFileIncludingHttpStatusWithSuffix(ResponseEntity<?> response, ValidationNormalizer validationNormalizer, String suffix) {
        String httpStatus = "// HTTP " + response.getStatusCode() + "\n";
        String actual = httpStatus + toActual(response);
        FileExtensions extension = isJsonResponse(response) ? FileExtensions.JSON5 : FileExtensions.TXT;
        assertWithFileWithSuffix(actual, validationNormalizer, suffix, extension);
    }

    default void assertWithFile(ResponseEntity<?> response) {
        assertWithFile(response, defaultValidationNormalizer());
    }

    default void assertWithFile(ResponseEntity<?> response, ValidationNormalizer validationNormalizer) {
        String actual = toActual(response);
        FileExtensions extension = isJsonResponse(response) ? FileExtensions.JSON : FileExtensions.TXT;
        assertWithFile(actual, validationNormalizer, extension);
    }

    default void assertWithFileWithSuffix(ResponseEntity<?> response, String suffix) {
        assertWithFileWithSuffix(response, defaultValidationNormalizer(), suffix);
    }

    default void assertWithFileWithSuffix(ResponseEntity<?> response, ValidationNormalizer validationNormalizer, String suffix) {
        String actual = toActual(response);
        FileExtensions extension = isJsonResponse(response) ? FileExtensions.JSON : FileExtensions.TXT;
        assertWithFileWithSuffix(actual, validationNormalizer, suffix, extension);
    }

    private static boolean isJsonResponse(ResponseEntity<?> response) {
        MediaType contentType = response.getHeaders().getContentType();
        if (contentType == null) {
            return false;
        }
        return contentType.isCompatibleWith(MediaType.APPLICATION_JSON) || contentType.isCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON);
    }

    private String toActual(ResponseEntity<?> response) {
        Object body = response.getBody();
        if (body == null) {
            return "--- no body ---";
        } else if (body instanceof String value) {
            return value;
        } else {
            return toJson(body);
        }
    }

    private String toJson(Object actual) {
        return objectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(actual);
    }

    @Override
    default void assertWithJsonFile(String actual) {
        assertWithJsonFileWithSuffix(actual, defaultValidationNormalizer(), null);
    }

    default void assertWithJsonFile(Object actual) {
        assertWithJsonFile(actual, defaultValidationNormalizer());
    }

    default void assertWithJsonFile(Object actual, ValidationNormalizer validationNormalizer) {
        assertWithJsonFileWithSuffix(actual, validationNormalizer, null);
    }

    default void assertWithJsonFileWithSuffix(Object actual, String suffix) {
        assertWithJsonFileWithSuffix(actual, defaultValidationNormalizer(), suffix);
    }

    default void assertWithJsonFileWithSuffix(Object actual, ValidationNormalizer validationNormalizer, String suffix) {
        String actualAsJsonString = toJson(actual);
        assertWithJsonFileWithSuffix(actualAsJsonString, validationNormalizer, suffix);
    }

    default ValidationNormalizer defaultValidationNormalizer() {
        return ValidationNormalizer.doNothing();
    }

}
