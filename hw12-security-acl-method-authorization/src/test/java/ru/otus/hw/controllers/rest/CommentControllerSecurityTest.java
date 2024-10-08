package ru.otus.hw.controllers.rest;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ResponseStatusException;
import ru.otus.hw.controllers.dto.CommentViewDto;
import ru.otus.hw.security.SecurityConfig;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
@Import(SecurityConfig.class)
@DisplayName("контроллер для работы с комментариями (безопасность)")
public class CommentControllerSecurityTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private CommentController commentController;

    @MockBean
    private UserDetailsService userDetailsService;

    /**
     * Для случаев, когда доступ в метод по url разрешен
     */
    @BeforeEach
    void stubbing() {
        doThrow(new ResponseStatusException(HttpStatus.CREATED)).when(commentController).create(any(CommentViewDto.class));
        doThrow(new ResponseStatusException(HttpStatus.OK)).when(commentController).getAll(anyLong());
        doThrow(new ResponseStatusException(HttpStatus.OK)).when(commentController).delete(anyLong());
    }

    @DisplayName("should return expected status")
    @ParameterizedTest(name = "{0} {1} for user {4} should return {6} status")
    @MethodSource("getTestData")
    void shouldReturnExpectedStatus(String method, String url, Map<String, String> params, String content,
                                    String userName, String[] roles,
                                    int status, boolean checkLoginRedirection) throws Exception {

        MockHttpServletRequestBuilder request = method2RequestBuilder(method, url, params, content);

        if (nonNull(userName)) {
            request = request.with(user(userName).roles(roles));
        }

        ResultActions resultActions = mvc.perform(request)
                .andExpect(status().is(status));

        if (checkLoginRedirection) {
            resultActions.andExpect(redirectedUrlPattern("**/login"));
        }
    }

    private MultiValueMap<String, String> convertToMultiValueMap(Map<String, String> params) {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>(params.size());
        for (String key : params.keySet()) {
            map.add(key, params.get(key));
        }
        return map;
    }

    private MockHttpServletRequestBuilder method2RequestBuilder(
            String method, String url, Map<String, String> params, String content) {
        Map<String, Function<String, MockHttpServletRequestBuilder>> methodMap =
                Map.of("get", MockMvcRequestBuilders::get,
                        "post", MockMvcRequestBuilders::post,
                        "put", MockMvcRequestBuilders::put,
                        "delete", MockMvcRequestBuilders::delete,
                        "patch", MockMvcRequestBuilders::patch);
        return methodMap.get(method)
                .apply(url)
                .params(convertToMultiValueMap(params))
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);
    }

    private static Stream<Arguments> getTestData() throws JsonProcessingException {
        var mapper = new ObjectMapper();
        var userRole = new String[]{"USER"};
        var adminRole = new String[]{"ADMIN"};
        String comment = mapper.writeValueAsString(
                new CommentViewDto("some comment with length larger then 10", 1L));
        return Stream.of(
                Arguments.of("post", "/api/v1/comment",
                        Map.of(), comment, "user", userRole, 201, false),
                Arguments.of("post", "/api/v1/comment",
                        Map.of(), comment, "admin", adminRole, 201, false),
                Arguments.of("post", "/api/v1/comment",
                        Map.of(), comment, null, null, 302, true),

                Arguments.of("get", "/api/v1/comment/1", Map.of(), Strings.EMPTY, "user", userRole, 200, false),
                Arguments.of("get", "/api/v1/comment/1", Map.of(), Strings.EMPTY, "admin", adminRole, 200, false),
                Arguments.of("get", "/api/v1/comment/1", Map.of(), Strings.EMPTY, null, null, 302, true),

                Arguments.of("delete", "/api/v1/comment/1", Map.of(), Strings.EMPTY, "user", userRole, 403, false),
                Arguments.of("delete", "/api/v1/comment/1", Map.of(), Strings.EMPTY, "admin", adminRole, 200, false),
                Arguments.of("delete", "/api/v1/comment/1", Map.of(), Strings.EMPTY, null, null, 302, true)

        );
    }
}
