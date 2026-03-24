package com.example.bookmanagementsystembo.bookRequest.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BookRequestViewController {

    @GetMapping("/book-requests")
    public String bookRequests() {
        return "user/book/book-requests";
    }

}
