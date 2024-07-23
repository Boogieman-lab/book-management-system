package me.bookhub.managementsystem.service;

//import me.bookhub.managementsystem.domain.Book;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;

@Service
public class AladinApiClient {

//    private final RestTemplate restTemplate = new RestTemplate();
//    private static final String API_URL = "https://api.aladin.co.kr/ttb/api/ItemSearch.aspx";
//
//    public List<Book> fetchBooks() {
//        String url = String.format("%s?ttbkey=%s&Query=%s&Output=JS&Version=20131101", API_URL, "your_api_key", "book");
//
//        // Here you will need to map the response to your Book entity
//        // Assuming you have a method to map the response to a list of Book objects
//        List<Book> books = mapResponseToBooks(restTemplate.getForObject(url, String.class));
//
//        return books;
//    }
//
//    private List<Book> mapResponseToBooks(String response) {
//        // Implement the mapping logic
//    }
}
