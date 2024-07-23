package com.bms.admin.book;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.json.JSONArray;
import org.json.JSONObject;

@Service
public class BookService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private BookRepository bookRepository;

    private static final String API_URL = "http://www.aladin.co.kr/ttb/api/ItemList.aspx?ttbkey=ttbrlatngus16912321001&QueryType=ItemNewAll&MaxResults=10&start=1&SearchTarget=Book&output=js&Version=20131101";

    public void fetchAndSaveBooks() {
        // API 호출
        String response = restTemplate.getForObject(API_URL, String.class);
        // JSON 파싱
        JSONObject jsonObject = new JSONObject(response);
        JSONArray items = jsonObject.getJSONArray("item");

        for (int i = 0; i < items.length(); i++) {
            JSONObject item = items.getJSONObject(i);
            BookEntity book = new BookEntity();
            book.setTitle(item.getString("title"));
            book.setAuthor(item.getString("author"));
            book.setPublisher(item.getString("publisher"));
            book.setPubDate(item.getString("pubDate"));
            book.setDescription(item.getString("description"));
            // 데이터베이스에 저장
            bookRepository.save(book);
        }
    }
}
