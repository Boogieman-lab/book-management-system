package com.example.bookmanagementsystembo.book.view;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user/book")
public class BookViewController {

    @GetMapping("/bookList")
    public String bookList(Model model) {
        model.addAttribute("activeMenu", "books");
        return "user/book/book_list"; // 경로 수정
    }

    @GetMapping("/bookDetail/{bookId}")
    public String bookDetail(@PathVariable Long bookId, Model model) {
        model.addAttribute("bookId", bookId);
        return "user/book/book_detail";
    }

}