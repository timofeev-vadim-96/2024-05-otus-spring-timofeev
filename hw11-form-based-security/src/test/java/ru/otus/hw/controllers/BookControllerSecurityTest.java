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
import org.springframework.validation.BindingResult;
import org.springframework.web.server.ResponseStatusException;
import ru.otus.hw.controllers.dto.BookViewDto;
import ru.otus.hw.security.SecurityConfig;

import java.util.ArrayList;
import java.util.List;
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

@WebMvcTest(BookController.class)
@Import(SecurityConfig.class)
@DisplayName("контроллер для работы с книгами (безопасность)")
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
        doThrow(new ResponseStatusException(HttpStatus.OK)).when(bookController).get(anyLong(), any(Model.class));
        doThrow(new ResponseStatusException(HttpStatus.OK)).when(bookController).getAll(any(Model.class));
        doThrow(new ResponseStatusException(HttpStatus.OK)).when(bookController).getCreatePage(any(Model.class));
        doThrow(new ResponseStatusException(HttpStatus.OK)).when(bookController).getEditPage(anyLong(), any(Model.class));
        doThrow(new ResponseStatusException(HttpStatus.OK)).when(bookController).create(any(BookViewDto.class), any(BindingResult.class), any(Model.class));
        doThrow(new ResponseStatusException(HttpStatus.OK)).when(bookController).update(anyLong(), any(BookViewDto.class), any(BindingResult.class), any(Model.class));
        doThrow(new ResponseStatusException(HttpStatus.OK)).when(bookController).errorPage();
        doThrow(new ResponseStatusException(HttpStatus.OK)).when(bookController).delete(anyLong());
    }

    @DisplayName("should return expected status")
    @ParameterizedTest(name = "{0} {1} for user {2} should return {4} status")
    @MethodSource("getTestData")
    void shouldReturnExpectedStatus(String method, String url,
                                    String userName, String[] roles,
                                    int status, boolean checkLoginRedirection) throws Exception {

        MockHttpServletRequestBuilder request = method2RequestBuilder(method, url);

        if (nonNull(userName)) {
            request = request.with(user(userName).roles(roles));
        }

        ResultActions resultActions = mvc.perform(request)
                .andExpect(status().is(status));

        if (checkLoginRedirection) {
            resultActions.andExpect(redirectedUrlPattern("**/login"));
        }
    }

    private MockHttpServletRequestBuilder method2RequestBuilder(String method, String url) {
        Map<String, Function<String, MockHttpServletRequestBuilder>> methodMap =
                Map.of("get", MockMvcRequestBuilders::get,
                        "post", MockMvcRequestBuilders::post,
                        "put", MockMvcRequestBuilders::put,
                        "delete", MockMvcRequestBuilders::delete,
                        "patch", MockMvcRequestBuilders::patch);
        return methodMap.get(method).apply(url);
    }


    private static Stream<Arguments> getTestData() {
        List<Arguments> args = new ArrayList<>();
        addArgsForGet(args);
        getArgsForGetEditPage(args);
        getArgsForUpdate(args);
        getArgsForCreate(args);
        getArgsForGetCreatePage(args);
        getArgsForGetAll(args);
        getArgsForDelete(args);
        args.add(Arguments.of("get", "/error", null, null, 200, false));

        return args.stream();
    }

    private static void addArgsForGet(List<Arguments> args) {
        args.addAll(List.of(
                Arguments.of("get", "/1", "user", USER_ROLES, 200, false),
                Arguments.of("get", "/1", "admin", ADMIN_ROLES, 200, false),
                Arguments.of("get", "/1", null, null, 302, true)));
    }

    private static void getArgsForGetEditPage(List<Arguments> args) {
        args.addAll(List.of(
                Arguments.of("get", "/edit/1", "user", BookControllerSecurityTest.USER_ROLES, 403, false),
                Arguments.of("get", "/edit/1", "admin", BookControllerSecurityTest.ADMIN_ROLES, 200, false),
                Arguments.of("get", "/edit/1", null, null, 302, true)));
    }

    private static void getArgsForUpdate(List<Arguments> args) {
        args.addAll(List.of(
                Arguments.of("post", "/1", "user", BookControllerSecurityTest.USER_ROLES, 403, false),
                Arguments.of("post", "/1", "admin", BookControllerSecurityTest.ADMIN_ROLES, 200, false),
                Arguments.of("post", "/1", null, null, 302, true)));
    }

    private static void getArgsForCreate(List<Arguments> args) {
        args.addAll(List.of(
                Arguments.of("post", "/", "user", BookControllerSecurityTest.USER_ROLES, 403, false),
                Arguments.of("post", "/", "admin", BookControllerSecurityTest.ADMIN_ROLES, 200, false),
                Arguments.of("post", "/", null, null, 302, true)));
    }

    private static void getArgsForGetCreatePage(List<Arguments> args) {
        args.addAll(List.of(
                Arguments.of("get", "/create", "user", BookControllerSecurityTest.USER_ROLES, 403, false),
                Arguments.of("get", "/create", "admin", BookControllerSecurityTest.ADMIN_ROLES, 200, false),
                Arguments.of("get", "/create", null, null, 302, true)));
    }

    private static void getArgsForGetAll(List<Arguments> args) {
        args.addAll(List.of(
                Arguments.of("get", "/", "user", BookControllerSecurityTest.USER_ROLES, 200, false),
                Arguments.of("get", "/", "admin", BookControllerSecurityTest.ADMIN_ROLES, 200, false),
                Arguments.of("get", "/", null, null, 302, true)));
    }

    private static void getArgsForDelete(List<Arguments> args) {
        args.addAll(List.of(
                Arguments.of("post", "/delete/1", "user", BookControllerSecurityTest.USER_ROLES, 403, false),
                Arguments.of("post", "/delete/1", "admin", BookControllerSecurityTest.ADMIN_ROLES, 200, false),
                Arguments.of("post", "/delete/1", null, null, 302, true)));
    }
}
