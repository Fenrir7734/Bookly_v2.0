package com.fenrir.masterdetail.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@EqualsAndHashCode
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String title;

    @NotBlank
    private String author;

    @Column(name = "description", length = 5000)
    private String description;
    private String cover;

    @Setter(AccessLevel.NONE)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Setter(AccessLevel.NONE)
    @CreationTimestamp
    private LocalDateTime createdAt;

    public Book(String title, String author, String description, String cover) {
        this.title = title;
        this.author = author;
        this.description = description;
        this.cover = cover;
    }
}
