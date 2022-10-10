package com.fenrir.masterdetail.service;

import com.fenrir.masterdetail.dto.StatisticsDTO;
import com.fenrir.masterdetail.exception.ResourceNotFoundException;
import com.fenrir.masterdetail.model.Book;
import com.fenrir.masterdetail.model.Review;
import com.fenrir.masterdetail.repository.BookRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class BookService {
    private BookRepository bookRepository;

    public Book get(Long id) {
        return getById(id);
    }

    public Page<Book> getAll(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }

    public Book create(Book book) {
        book.setId(null);
        return bookRepository.save(book);
    }

    public Book update(Long id, Book book) {
        Book bookToUpdate = getById(id);
        bookToUpdate.setTitle(book.getTitle());
        bookToUpdate.setAuthor(book.getAuthor());
        bookToUpdate.setDescription(book.getDescription());
        bookToUpdate.setCover(book.getCover());
        return bookRepository.save(bookToUpdate);
    }

    public void delete(Long id) {
        Book book = getById(id);
        bookRepository.delete(book);
    }

    private Book getById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Book was not found for id=%d", id)
                ));
    }
}
