package ru.otus.hw.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.otus.hw.controllers.dto.CommentViewDto;
import ru.otus.hw.services.CommentService;

@Controller
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/comment/create/{bookId}")
    public String createComment(@PathVariable("bookId") long bookId,
                                @Valid @ModelAttribute("comment") CommentViewDto dto,
                                BindingResult result) {
        if (result.hasErrors()) {
            return "redirect:/book/book_page/" + bookId;
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
}
