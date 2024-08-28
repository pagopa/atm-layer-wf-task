package it.pagopa.atmlayer.wf.task.test.configuration;

import it.pagopa.atmlayer.wf.task.bean.exceptions.ErrorException;
import it.pagopa.atmlayer.wf.task.configuration.AuthorizationFilter;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

class AuthorizationFilterTest {

    private AuthorizationFilter authorizationFilter;
    private ContainerRequestContext requestContext;

    @BeforeEach
    public void setUp() {
        authorizationFilter = new AuthorizationFilter();
        requestContext = mock(ContainerRequestContext.class);
    }

    @Test
    void testFilter_ShouldThrowException_WhenApiKeyMismatch() {

        MultivaluedMap<String, String> headers = new MultivaluedHashMap<>();
        headers.add("x-api-key", "wrong-api-key");

        when(requestContext.getHeaders()).thenReturn(headers);

        when(requestContext.getHeaderString("Authorization")).thenReturn("eyJraWQiOiJMamxSWVVGTEVlOTFlSVZMc3RtYkNjSDk0RllrRlFrMzlGRnFwb2k1VHlVPSIsImFsZyI6IlJTMjU2In0.eyJzdWIiOiI1OThhcGpjcjNnZTFlb2ZkNnAzc2lqcmR1NiIsInRva2VuX3VzZSI6ImFjY2VzcyIsInNjb3BlIjoiZGV2XC90YXNrcyIsImF1dGhfdGltZSI6MTcyNDg0Nzc1OCwiaXNzIjoiaHR0cHM6XC9cL2NvZ25pdG8taWRwLmV1LXNvdXRoLTEuYW1hem9uYXdzLmNvbVwvZXUtc291dGgtMV9zRVpGOVBxQWYiLCJleHAiOjE3MjQ4NTEzNTgsImlhdCI6MTcyNDg0Nzc1OCwidmVyc2lvbiI6MiwianRpIjoiN2RmYmFjNmEtZTFiNi00ODRhLWE5N2MtOGNlYTBlNmZiMDMwIiwiY2xpZW50X2lkIjoiNTk4YXBqY3IzZ2UxZW9mZDZwM3NpanJkdTYifQ.MTaK6B-Ro6mvhVvfyF5-0dfP10dV98RJksG5fJMXAGUi22pu6JmZgh-qglyB8B6gWTEyBAR6k7d4NNzdXo65DNqzOtenSzwjSdnHQqg3o5Qv0wBq7xJ652GhMZSa6RO3QWPnF56gYBiFoCVbhhliYc3z1OlY7pk1wo6DnbhlkzlCGfHj2DMK23BQ3U4JBm3lj9w86G1QMzDiEaXfgkgQJRG7Z0iPfJzn6uouwyZXM3fGgDbTfF7NbFSlNXQDJZ_e2TMbZbR-5BUt-G8uGPs8CLfGTscX-4iThOr11Ra_jPniwBaajEIpndkukj4g9xOtW8Z1wCu0r2ES75hIMyBOKw");

        assertThrows(ErrorException.class, () -> {
            authorizationFilter.filter(requestContext);
        });
    }

    @Test
    void testFilter_ShouldNotThrowException_WhenApiKeyMatches() {

        MultivaluedMap<String, String> headers = new MultivaluedHashMap<>();
        headers.add("x-api-key", "598apjcr3ge1eofd6p3sijrdu6");

        when(requestContext.getHeaders()).thenReturn(headers);

        when(requestContext.getHeaderString("Authorization")).thenReturn("eyJraWQiOiJMamxSWVVGTEVlOTFlSVZMc3RtYkNjSDk0RllrRlFrMzlGRnFwb2k1VHlVPSIsImFsZyI6IlJTMjU2In0.eyJzdWIiOiI1OThhcGpjcjNnZTFlb2ZkNnAzc2lqcmR1NiIsInRva2VuX3VzZSI6ImFjY2VzcyIsInNjb3BlIjoiZGV2XC90YXNrcyIsImF1dGhfdGltZSI6MTcyNDg0Nzc1OCwiaXNzIjoiaHR0cHM6XC9cL2NvZ25pdG8taWRwLmV1LXNvdXRoLTEuYW1hem9uYXdzLmNvbVwvZXUtc291dGgtMV9zRVpGOVBxQWYiLCJleHAiOjE3MjQ4NTEzNTgsImlhdCI6MTcyNDg0Nzc1OCwidmVyc2lvbiI6MiwianRpIjoiN2RmYmFjNmEtZTFiNi00ODRhLWE5N2MtOGNlYTBlNmZiMDMwIiwiY2xpZW50X2lkIjoiNTk4YXBqY3IzZ2UxZW9mZDZwM3NpanJkdTYifQ.MTaK6B-Ro6mvhVvfyF5-0dfP10dV98RJksG5fJMXAGUi22pu6JmZgh-qglyB8B6gWTEyBAR6k7d4NNzdXo65DNqzOtenSzwjSdnHQqg3o5Qv0wBq7xJ652GhMZSa6RO3QWPnF56gYBiFoCVbhhliYc3z1OlY7pk1wo6DnbhlkzlCGfHj2DMK23BQ3U4JBm3lj9w86G1QMzDiEaXfgkgQJRG7Z0iPfJzn6uouwyZXM3fGgDbTfF7NbFSlNXQDJZ_e2TMbZbR-5BUt-G8uGPs8CLfGTscX-4iThOr11Ra_jPniwBaajEIpndkukj4g9xOtW8Z1wCu0r2ES75hIMyBOKw");

        assertDoesNotThrow(() -> {
            authorizationFilter.filter(requestContext);
        });

        verify(requestContext, times(2)).getHeaders();
    }

    @Test
    void testFilter_ShouldNotThrowException_WhenApiKeyIsMissing() {

        MultivaluedMap<String, String> headers = new MultivaluedHashMap<>();

        when(requestContext.getHeaders()).thenReturn(headers);

        when(requestContext.getHeaderString("x-client-id")).thenReturn("correct-client-id");

        assertDoesNotThrow(() -> {
            authorizationFilter.filter(requestContext);
        });

        verify(requestContext, times(1)).getHeaders();
    }
}

