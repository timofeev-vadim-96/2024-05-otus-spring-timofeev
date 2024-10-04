package ru.otus.hw.controllers;

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
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
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
        doThrow(new ResponseStatusException(HttpStatus.CREATED)).when(commentController).createComment(anyLong(), any(CommentViewDto.class), any(BindingResult.class), any(Model.class));
        doThrow(new ResponseStatusException(HttpStatus.OK)).when(commentController).deleteComment(anyLong(), anyLong());
    }

    @DisplayName("should return expected status")
    @ParameterizedTest(name = "{0} {1} for user {3} should return {5} status")
    @MethodSource("getTestData")
    void shouldReturnExpectedStatus(String method, String url, Map<String, String> params,
                                    String userName, String[] roles,
                                    int status, boolean checkLoginRedirection) throws Exception {

        MockHttpServletRequestBuilder request = method2RequestBuilder(method, url, params);

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

    private MockHttpServletRequestBuilder method2RequestBuilder(String method, String url, Map<String, String> params) {
        Map<String, Function<String, MockHttpServletRequestBuilder>> methodMap =
                Map.of("get", MockMvcRequestBuilders::get,
                        "post", MockMvcRequestBuilders::post,
                        "put", MockMvcRequestBuilders::put,
                        "delete", MockMvcRequestBuilders::delete,
                        "patch", MockMvcRequestBuilders::patch);
        return methodMap.get(method).apply(url).params(convertToMultiValueMap(params));
    }


    private static Stream<Arguments> getTestData() {
        var userRole = new String[]{"USER"};
        var adminRole = new String[]{"ADMIN"};
        return Stream.of(
                Arguments.of("post", "/comment/1", Map.of(), "user", userRole, 403, false),
                Arguments.of("post", "/comment/1", Map.of(), "admin", adminRole, 201, false),
                Arguments.of("post", "/comment/1", Map.of(), null, null, 302, true),
                Arguments.of("post", "/comment/delete/1", Map.of("bookId", String.valueOf(1L)), "user", userRole, 403, false),
                Arguments.of("post", "/comment/delete/1", Map.of("bookId", String.valueOf(1L)), "admin", adminRole, 200, false),
                Arguments.of("post", "/comment/delete/1", Map.of("bookId", String.valueOf(1L)), null, null, 302, true));
    }
}
