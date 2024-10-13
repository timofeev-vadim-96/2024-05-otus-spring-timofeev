package ru.otus.hw.controllers.classic;

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
import org.springframework.web.server.ResponseStatusException;
import ru.otus.hw.security.SecurityConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = PageController.class)
@Import(SecurityConfig.class)
@DisplayName("Контроллер для работы со страницами")
public class PageControllerSecurityTest {
    private static final String[] USER_ROLES = new String[]{"USER"};

    private static final String[] ADMIN_ROLES = new String[]{"ADMIN"};

    private static final String[] ANONYMOUS_ROLES = new String[]{"ANONYMOUS"};

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private PageController pageController;

    @BeforeEach
    void stubbing() {
        doThrow(new ResponseStatusException(HttpStatus.OK)).when(pageController).create();
        doThrow(new ResponseStatusException(HttpStatus.OK)).when(pageController).edit(anyLong(), any(Model.class));
        doThrow(new ResponseStatusException(HttpStatus.OK)).when(pageController).list();
        doThrow(new ResponseStatusException(HttpStatus.OK)).when(pageController).book(anyLong(), any(Model.class));
        doThrow(new ResponseStatusException(HttpStatus.OK)).when(pageController).errorPage();
    }

    @DisplayName("should return expected status")
    @ParameterizedTest(name = "{0} for user {1} should return {3} status")
    @MethodSource("getTestData")
    void shouldReturnExpectedStatus(String url, String userName, String[] roles,
                                    int status, boolean checkLoginRedirection) throws Exception {

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(url);

        if (nonNull(userName)) {
            request = request.with(user(userName).roles(roles));
        }

        ResultActions resultActions = mvc.perform(request)
                .andExpect(status().is(status));

        if (checkLoginRedirection) {
            resultActions.andExpect(redirectedUrlPattern("**/login"));
        }
    }

    private static Stream<Arguments> getTestData() {
        List<Arguments> args = new ArrayList<>();
        addArgsForCreatePage(args);
        addArgsForEditPage(args);
        addArgsForListPage(args);
        addArgsForBookPage(args);
        addArgsForErrorPage(args);

        return args.stream();
    }

    private static void addArgsForCreatePage(List<Arguments> args) {
        args.addAll(List.of(
                Arguments.of("/create", "user", USER_ROLES, 403, false),
                Arguments.of("/create", "admin", ADMIN_ROLES, 200, false),
                Arguments.of("/create", null, null, 302, true)));
    }

    private static void addArgsForEditPage(List<Arguments> args) {
        args.addAll(List.of(
                Arguments.of("/edit/1", "user", USER_ROLES, 403, false),
                Arguments.of("/edit/1", "admin", ADMIN_ROLES, 200, false),
                Arguments.of("/edit/1", null, null, 302, true)));
    }

    private static void addArgsForListPage(List<Arguments> args) {
        args.addAll(List.of(
                Arguments.of("/", "user", USER_ROLES, 200, false),
                Arguments.of("/", "admin", ADMIN_ROLES, 200, false),
                Arguments.of("/", null, null, 302, true)));
    }

    private static void addArgsForBookPage(List<Arguments> args) {
        args.addAll(List.of(
                Arguments.of("/book/1", "user", USER_ROLES, 200, false),
                Arguments.of("/book/1", "admin", ADMIN_ROLES, 200, false),
                Arguments.of("/book/1", null, null, 302, true)));
    }

    private static void addArgsForErrorPage(List<Arguments> args) {
        args.add(
                Arguments.of("/error", "anonymousUser", ANONYMOUS_ROLES, 200, false));
    }
}
