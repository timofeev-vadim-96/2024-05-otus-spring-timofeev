package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.command.annotation.Command;
import ru.otus.hw.models.Comment;
import ru.otus.hw.services.CommentService;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@Command(group = "comment-commands")
public class CommentCommands {
    private final CommentService commentService;

    @Command(description = "Find all by book id. Params: bookId", command = "cbbid")
    public String findAllByBookId(long bookId) {
        return commentService.findAllByBookId(bookId).stream()
                .map(Comment::toString)
                .collect(Collectors.joining("," + System.lineSeparator()));
    }

    @Command(description = "Insert comment. Params: text, bookId", command = "cins")
    public String insertBook(String text, long bookId) {
        var savedComment = commentService.insert(text, bookId);
        return savedComment.toString();
    }

    // bupd 4 editedBook 3 2,5
    @Command(description = "Update comment. Params: id, text", command = "cupd")
    public String updateBook(long id, String text) {
        var updatedBook = commentService.update(text, id);
        return updatedBook.toString();
    }

    // bdel 4
    @Command(description = "Delete comment by id. Params: id", command = "cdel")
    public void deleteBook(long id) {
        commentService.deleteById(id);
    }
}
