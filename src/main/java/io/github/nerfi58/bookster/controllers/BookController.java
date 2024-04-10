package io.github.nerfi58.bookster.controllers;

import io.github.nerfi58.bookster.dtos.BookDto;
import io.github.nerfi58.bookster.dtos.PageDto;
import io.github.nerfi58.bookster.services.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/book")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/all")
    public ResponseEntity<PageDto<BookDto>> getAllBooksWithFiltering(
            @RequestParam(name = "sort") String sort,
            @RequestParam(name = "filter") String filterMap,
            @RequestParam(name = "page") int page) {

        return ResponseEntity.ok(bookService.getAllBooksWithFiltering(filterMap, sort, page));
    }

    @GetMapping("/user/{username}")
    @PreAuthorize("#username == authentication.name")
    public ResponseEntity<PageDto<BookDto>> getBooksReadByUserWithFiltering(
            @PathVariable(name = "username") String username,
            @RequestParam(name = "sort") String sort,
            @RequestParam(name = "filter") String filterMap,
            @RequestParam(name = "page") int page) {

        return ResponseEntity.ok(bookService.getBooksReadByUserWithFiltering(username, filterMap, sort, page));
    }

}
