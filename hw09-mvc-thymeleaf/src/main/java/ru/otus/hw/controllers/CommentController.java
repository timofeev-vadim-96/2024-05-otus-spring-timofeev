package ru.otus.hw.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.otus.hw.controllers.dto.CommentViewDto;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.dto.BookDto;
import ru.otus.hw.services.dto.CommentDto;
import ru.otus.hw.services.dto.GenreDto;

import java.util.List;

@Controller
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    private final BookService bookService;

    @PostMapping("/{bookId}")
    public String createComment(@PathVariable("bookId") long bookId,
                                @Valid @ModelAttribute("comment") CommentViewDto dto,
                                BindingResult result,
                                Model model) {
        if (result.hasErrors()) {
            BookDto book = bookService.findById(bookId);
            List<CommentDto> comments = commentService.findAllByBookId(bookId);
            model.addAttribute("book", book);
            model.addAttribute("comments", comments);
            model.addAttribute("genres", book.getGenres().stream()
                    .map(GenreDto::getName)
                    .toList().toString()
                    .replace('[', ' ')
                    .replace(']', ' ')
                    .trim());

            return "book";
        }

        commentService.create(dto.getText(), bookId);
        return "redirect:/" + bookId;
    }

    @PostMapping("/delete/{id}")
    public String deleteComment(@PathVariable("id") long id,
                                @RequestParam("bookId") long bookId) {
        commentService.deleteById(id);
        return "redirect:/" + bookId;
    }
}
