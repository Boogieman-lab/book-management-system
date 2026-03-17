package com.example.bookmanagementsystembo.bookrequest.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/user/book")
@Controller
public class BookRequestViewController {

    @GetMapping("/requestBook")
    public String requestBook() {
        return "user/book/request_book";
    }

}
