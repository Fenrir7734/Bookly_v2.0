package com.fenrir.masterdetail.repository;

import com.fenrir.masterdetail.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
}
