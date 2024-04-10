package io.github.nerfi58.bookster.services;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.github.nerfi58.bookster.controllers.BookController;
import io.github.nerfi58.bookster.dtos.BookDto;
import io.github.nerfi58.bookster.dtos.PageDto;
import io.github.nerfi58.bookster.mappers.BookMapper;
import io.github.nerfi58.bookster.mappers.PageMapper;
import io.github.nerfi58.bookster.repositories.BookRepository;
import io.github.nerfi58.bookster.repositories.specifications.BookSpecification;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class BookService {

    private final BookRepository bookRepository;

    private static final int PAGE_SIZE = 20;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public PageDto<BookDto> getAllBooksWithFiltering(String filterMapAsString,
                                                     String sort,
                                                     int page) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by(Sort.Direction.ASC, sort));

        Map<String, String> filterMap = new Gson().fromJson(
                filterMapAsString,
                new TypeToken<HashMap<String, String>>() {
                }.getType()
        );

        PageDto<BookDto> pageDtoBookDto = PageMapper.pageToPageDto(bookRepository.findAll(
                Specification.where(BookSpecification.titleLike(filterMap.get("title")))
                        .and(BookSpecification.authorLike(filterMap.get("author")))
                        .and(BookSpecification.genreLike(filterMap.get("genre")))
                        .and(BookSpecification.publisherLike(filterMap.get("publisher"))),
                pageable
        ).map(BookMapper::bookToBookDto));

        String nextUrl = MvcUriComponentsBuilder.fromMethodName(
                        BookController.class,
                        "getAllBooksWithFiltering",
                        sort,
                        URLEncoder.encode(filterMapAsString, StandardCharsets.UTF_8),
                        page + 1
                )
                .buildAndExpand()
                .toString();

        //if not last page set url to next page else set null
        pageDtoBookDto.setNext(pageDtoBookDto.getPageNumber() + 1 >= pageDtoBookDto.getTotalPages() ? null : nextUrl);

        return pageDtoBookDto;
    }

    public PageDto<BookDto> getBooksReadByUserWithFiltering(String username,
                                                            String filterMapAsString,
                                                            String sort,
                                                            int page) {

        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by(Sort.Direction.ASC, sort));

        Map<String, String> filterMap = new Gson().fromJson(
                filterMapAsString,
                new TypeToken<HashMap<String, String>>() {
                }.getType()
        );

        PageDto<BookDto> pageDto = PageMapper.pageToPageDto(bookRepository.findAll(
                Specification.where(BookSpecification.booksReadByUser(username))
                        .and(BookSpecification.titleLike(filterMap.get("title")))
                        .and(BookSpecification.authorLike(filterMap.get("author")))
                        .and(BookSpecification.publisherLike(filterMap.get("publisher")))
                        .and(BookSpecification.genreLike(filterMap.get("genre"))),
                pageable
        ).map(BookMapper::bookToBookDto));

        String nextUrl = MvcUriComponentsBuilder.fromMethodName(
                BookController.class,
                "getBooksReadByUserWithFiltering",
                username,
                sort,
                URLEncoder.encode(filterMapAsString, StandardCharsets.UTF_8),
                page + 1
        ).buildAndExpand().toString();

        //if not last page set url to next page else set null
        pageDto.setNext(pageDto.getPageNumber() + 1 >= pageDto.getTotalPages() ? null : nextUrl);

        return pageDto;
    }
}
