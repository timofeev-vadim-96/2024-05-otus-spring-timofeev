package ru.otus.hw.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.otus.hw.controllers.dto.CommentViewDto;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.dto.BookDto;
import ru.otus.hw.services.dto.CommentDto;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    private final BookService bookService;

    @PostMapping("/comment/create/{bookId}")
    public String createComment(@PathVariable("bookId") long bookId,
                                @Valid @ModelAttribute("comment") CommentViewDto dto,
                                BindingResult result,
                                Model model) {
        if (result.hasErrors()) {
            populateModelWithCatalogs(model, bookId);
            return "book";
        }

        commentService.create(dto.getText(), bookId);
        return "redirect:/book/book_page/" + bookId;
    }

    @PostMapping("comment/delete/{id}")
    public String deleteComment(@PathVariable("id") long id,
                                @RequestParam("bookId") long bookId) {
        commentService.deleteById(id);

        return "redirect:/book/book_page/" + bookId;
    }

    private void populateModelWithCatalogs(Model model, long bookId) {
        BookDto book = bookService.findById(bookId);
        List<CommentDto> comments = commentService.findAllByBookId(bookId);

        model.addAttribute("comments", comments);
        model.addAttribute("genres", book.getGenres());
        model.addAttribute("book", book);
    }
}
