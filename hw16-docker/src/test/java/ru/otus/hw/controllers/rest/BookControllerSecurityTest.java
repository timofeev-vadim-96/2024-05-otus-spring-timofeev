package ru.otus.hw.controllers.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ResponseStatusException;
import ru.otus.hw.controllers.dto.BookViewDto;
import ru.otus.hw.controllers.dto.CommentViewDto;
import ru.otus.hw.security.SecurityConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = BookController.class)
@Import(SecurityConfig.class)
@DisplayName("контроллер для работы с книгами")
public class BookControllerSecurityTest {
    private static final String[] USER_ROLES = new String[]{"USER"};

    private static final String[] ADMIN_ROLES = new String[]{"ADMIN"};

    @Autowired
    private MockMvc mvc;

    @MockBean
    private BookController bookController;

    @MockBean
    private UserDetailsService userDetailsService;

    /**
     * Для случаев, когда доступ в метод по url разрешен
     */
    @BeforeEach
    void stubbing() {
        doThrow(new ResponseStatusException(HttpStatus.CREATED)).when(bookController).create(any(BookViewDto.class));
        doThrow(new ResponseStatusException(HttpStatus.OK)).when(bookController).getAll();
        doThrow(new ResponseStatusException(HttpStatus.OK)).when(bookController).get(anyLong());
        doThrow(new ResponseStatusException(HttpStatus.OK)).when(bookController).delete(anyLong());
        doThrow(new ResponseStatusException(HttpStatus.OK)).when(bookController).update(any(BookViewDto.class));
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
        List<Arguments> args = new ArrayList<>();
        addArgsForGet(args);
        addArgsForGetAll(args);
        addArgsForUpdate(args);
        addArgsForCreate(args);
        addArgsForDelete(args);

        return args.stream();
    }

    private static void addArgsForGet(List<Arguments> args) {
        args.addAll(List.of(
                Arguments.of("get", "/api/v1/book/1",
                        Map.of(), Strings.EMPTY, "user", USER_ROLES, 200, false),
                Arguments.of("get", "/api/v1/book/1",
                        Map.of(), Strings.EMPTY, "admin", ADMIN_ROLES, 200, false),
                Arguments.of("get", "/api/v1/book/1",
                        Map.of(), Strings.EMPTY, null, null, 302, true)));
    }

    private static void addArgsForGetAll(List<Arguments> args) {
        args.addAll(List.of(
                Arguments.of("get", "/api/v1/book",
                        Map.of(), Strings.EMPTY, "user", USER_ROLES, 200, false),
                Arguments.of("get", "/api/v1/book",
                        Map.of(), Strings.EMPTY, "admin", ADMIN_ROLES, 200, false),
                Arguments.of("get", "/api/v1/book",
                        Map.of(), Strings.EMPTY, null, null, 302, true)));
    }

    private static void addArgsForUpdate(List<Arguments> args) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        BookViewDto dto = new BookViewDto(1L, "title with length larger then 3", 1L, Set.of(1L));
        String jsonDto = mapper.writeValueAsString(dto);

        args.addAll(List.of(
                Arguments.of("put", "/api/v1/book",
                        Map.of(), jsonDto, "user", USER_ROLES, 403, false),
                Arguments.of("put", "/api/v1/book",
                        Map.of(), jsonDto, "admin", ADMIN_ROLES, 200, false),
                Arguments.of("put", "/api/v1/book",
                        Map.of(), jsonDto, null, null, 302, true)));
    }

    private static void addArgsForCreate(List<Arguments> args) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        BookViewDto dto = new BookViewDto(null, "title with length larger then 3", 1L, Set.of(1L));
        String jsonDto = mapper.writeValueAsString(dto);

        args.addAll(List.of(
                Arguments.of("post", "/api/v1/book",
                        Map.of(), jsonDto, "user", USER_ROLES, 403, false),
                Arguments.of("post", "/api/v1/book",
                        Map.of(), jsonDto, "admin", ADMIN_ROLES, 201, false),
                Arguments.of("post", "/api/v1/book",
                        Map.of(), jsonDto, null, null, 302, true)));
    }

    private static void addArgsForDelete(List<Arguments> args) throws JsonProcessingException {
        args.addAll(List.of(
                Arguments.of("delete", "/api/v1/book/1",
                        Map.of(), Strings.EMPTY, "user", USER_ROLES, 403, false),
                Arguments.of("delete", "/api/v1/book/1",
                        Map.of(), Strings.EMPTY, "admin", ADMIN_ROLES, 200, false),
                Arguments.of("delete", "/api/v1/book/1",
                        Map.of(), Strings.EMPTY, null, null, 302, true)));
    }
}
