package com.fenrir.masterdetail.controller;

import com.fenrir.masterdetail.model.Book;
import com.fenrir.masterdetail.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@AllArgsConstructor
@RestController
@RequestMapping(
        path = "/api/books",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class BookController {
    private BookService bookService;

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getBookById(@PathVariable("id") Long id) {
        Book book = bookService.get(id);
        return ResponseEntity.ok(book);
    }

    @GetMapping
    public ResponseEntity<?> getAllBooks(
            @PageableDefault(sort = "title", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<Book> books = bookService.getAll(pageable);
        return ResponseEntity.ok(books);
    }

    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> postBook(@RequestBody Book book, UriComponentsBuilder builder) {
        book = bookService.create(book);
        Long bookId = book.getId();
        URI location = builder.replacePath("/api/books/{id}")
                .buildAndExpand(bookId)
                .toUri();
        return ResponseEntity.created(location).body(book);
    }

    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateBook(@PathVariable("id") Long id, @RequestBody Book book) {
        book = bookService.update(id, book);
        return ResponseEntity.ok(book);
    }

    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBookById(@PathVariable("id") Long id) {
        bookService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
