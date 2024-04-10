package io.github.nerfi58.bookster.repositories.specifications;

import io.github.nerfi58.bookster.entities.Author;
import io.github.nerfi58.bookster.entities.Book;
import io.github.nerfi58.bookster.entities.Genre;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

public class BookSpecification {

    public static Specification<Book> booksReadByUser(String username) {
        return (root, query, cb) -> cb.equal(root.joinSet("usersWhoRead").get("username"), username);
    }

    public static Specification<Book> titleLike(String title) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
    }

    public static Specification<Book> authorLike(String author) {
        return (root, query, cb) -> {
            Subquery<Author> subquery = query.subquery(Author.class);
            Root<Author> authorRoot = subquery.from(Author.class);

            subquery.select(authorRoot);
            subquery.where(cb.and(
                    cb.equal(root.get("authors").get("id"), authorRoot.get("id")),
                    cb.like(cb.lower(authorRoot.get("fullName")), "%" + author.toLowerCase() + "%")
            ));
            return cb.exists(subquery);
        };
    }

    public static Specification<Book> publisherLike(String publisher) {
        return (root, query, cb) -> cb.like(
                cb.lower(root.join("publisher").get("name")),
                "%" + publisher.toLowerCase() + "%"
        );
    }

    public static Specification<Book> genreLike(String genre) {
        return (root, query, cb) -> {
            Subquery<Genre> subquery = query.subquery(Genre.class);
            Root<Genre> genreRoot = subquery.from(Genre.class);
            subquery.select(genreRoot);
            subquery.where(cb.and(
                    cb.equal(root.get("genres").get("id"), genreRoot.get("id")),
                    cb.like(cb.lower(genreRoot.get("genre")), "%" + genre.toLowerCase() + "%")
            ));
            return cb.exists(subquery);
        };
    }
}
