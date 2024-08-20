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
import ru.otus.hw.services.CommentService;

@Controller
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    private final BookController bookController;

    @PostMapping("comment/{bookId}")
    public String createComment(@PathVariable("bookId") long bookId,
                                @Valid @ModelAttribute("comment") CommentViewDto dto,
                                BindingResult result,
                                Model model) {
        if (result.hasErrors()) {
            return bookController.get(bookId, model);
        }

        commentService.create(dto.getText(), bookId);
        return bookController.get(bookId, model);
    }

    @PostMapping("comment/delete/{id}")
    public String deleteComment(@PathVariable("id") long id,
                                @RequestParam("bookId") long bookId,
                                Model model) {
        commentService.deleteById(id);
        return bookController.get(bookId, model);
    }
}
