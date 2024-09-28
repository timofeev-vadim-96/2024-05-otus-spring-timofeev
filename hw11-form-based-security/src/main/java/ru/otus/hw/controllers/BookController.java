package ru.otus.hw.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.otus.hw.controllers.dto.BookViewDto;
import ru.otus.hw.controllers.dto.CommentViewDto;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.GenreService;
import ru.otus.hw.services.dto.AuthorDto;
import ru.otus.hw.services.dto.BookDto;
import ru.otus.hw.services.dto.CommentDto;
import ru.otus.hw.services.dto.GenreDto;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class BookController {
    private final BookService bookService;

    private final CommentService commentService;

    private final AuthorService authorService;

    private final GenreService genreService;

    @GetMapping(value = "/{id}")
    public String get(@PathVariable("id") long id, Model model) {
        BookDto book = bookService.findById(id);
        model.addAttribute("book", book);
        model.addAttribute("genres", book.getGenres().stream()
                .map(GenreDto::getName)
                .toList().toString()
                .replace('[', ' ')
                .replace(']', ' ')
                .trim());
        List<CommentDto> comments = commentService.findAllByBookId(id);
        model.addAttribute("comments", comments);
        model.addAttribute("comment", new CommentViewDto());

        return "book";
    }

    @GetMapping(value = "/edit/{id}")
    public String getEditPage(@PathVariable("id") long id, Model model) {
        BookDto book = bookService.findById(id);
        List<AuthorDto> authors = authorService.findAll();
        List<GenreDto> genres = genreService.findAll();

        model.addAttribute("authors", authors);
        model.addAttribute("book", book);
        model.addAttribute("dto", new BookViewDto());
        model.addAttribute("genres", genres);

        return "edit";
    }

    @PostMapping(value = "/{id}")
    public String update(@PathVariable("id") long id,
                         @Valid @ModelAttribute("dto") BookViewDto dto,
                         BindingResult result,
                         Model model) {
        if (result.hasErrors()) {
            BookDto book = bookService.findById(id);
            List<AuthorDto> authors = authorService.findAll();
            List<GenreDto> genres = genreService.findAll();

            model.addAttribute("book", book);
            model.addAttribute("authors", authors);
            model.addAttribute("genres", genres);
            model.addAttribute("dto", new BookViewDto());

            return "edit";
        }

        bookService.update(id, dto.getTitle(), dto.getAuthorId(), dto.getGenres());
        return "redirect:/";
    }

    @PostMapping()
    public String create(@Valid @ModelAttribute("book") BookViewDto book,
                         BindingResult result,
                         Model model) {
        if (result.hasErrors()) {
            List<AuthorDto> authors = authorService.findAll();
            List<GenreDto> genres = genreService.findAll();

            model.addAttribute("authors", authors);
            model.addAttribute("genres", genres);

            return "create";
        }

        bookService.create(book.getTitle(), book.getAuthorId(), book.getGenres());
        return "redirect:/";
    }

    @GetMapping("/create")
    public String getCreatePage(Model model) {
        List<AuthorDto> authors = authorService.findAll();
        List<GenreDto> genres = genreService.findAll();

        model.addAttribute("authors", authors);
        model.addAttribute("genres", genres);
        model.addAttribute("book", new BookViewDto());

        return "create";
    }

    @GetMapping
    public String getAll(Model model) {
        List<BookDto> books = bookService.findAll();
        model.addAttribute("books", books);
        return "books";
    }

    @PostMapping(value = "/delete/{id}")
    public String delete(@PathVariable("id") long id) {
        bookService.deleteById(id);
        return "redirect:/";
    }

    @GetMapping(value = "/error")
    public String errorPage() {
        return "error";
    }
}