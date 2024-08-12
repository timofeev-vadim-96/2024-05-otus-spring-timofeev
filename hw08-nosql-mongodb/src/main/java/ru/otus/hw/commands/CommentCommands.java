package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.command.annotation.Command;
import ru.otus.hw.converters.CommentConverter;
import ru.otus.hw.services.CommentService;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@Command(group = "comment-commands")
public class CommentCommands {
    private final CommentService commentService;

    private final CommentConverter commentConverter;

    @Command(description = "Find all by book id. Params: bookId", command = "cbbid")
    public String findAllByBookId(String bookId) {
        return commentService.findAllByBookId(bookId).stream()
                .map(commentConverter::commentToString)
                .collect(Collectors.joining("," + System.lineSeparator()));
    }

    @Command(description = "Insert comment. Params: text, bookId", command = "cins")
    public String insertBook(String text, String bookId) {
        var savedComment = commentService.create(text, bookId);
        return commentConverter.commentToString(savedComment);
    }

    // bupd 4 editedBook 3 2,5
    @Command(description = "Update comment. Params: id, text", command = "cupd")
    public String updateComment(String id, String text) {
        var updatedComment = commentService.update(text, id);
        return commentConverter.commentToString(updatedComment);
    }

    // bdel 4
    @Command(description = "Delete comment by id. Params: id", command = "cdel")
    public void deleteComment(String id) {
        commentService.deleteById(id);
    }
}