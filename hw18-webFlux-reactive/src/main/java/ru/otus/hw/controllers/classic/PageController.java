package ru.otus.hw.controllers.classic;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class PageController {
    @GetMapping("/create")
    public String create() {
        return "create";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") String id, Model model) {
        model.addAttribute("id", id);
        return "edit";
    }

    @GetMapping
    public String list() {
        return "books";
    }

    @GetMapping("/book/{id}")
    public String book(@PathVariable("id") String id, Model model) {
        model.addAttribute("id", id);
        return "book";
    }
}
