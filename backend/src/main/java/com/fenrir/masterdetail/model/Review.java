package com.fenrir.masterdetail.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class Review {

    @Setter(AccessLevel.NONE)
    @EmbeddedId
    private Id id = new Id();

    @Size(max = 1000)
    private String content;

    @NotNull
    @Min(1)
    @Max(5)
    @Column(name = "rate", nullable = false)
    private Integer rate;

    @Setter(AccessLevel.NONE)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Setter(AccessLevel.NONE)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(insertable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Book book;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(insertable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    public Review(Id id, String content, Integer rate) {
        this.id = id;
        this.content = content;
        this.rate = rate;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @EqualsAndHashCode
    @Embeddable
    public static class Id implements Serializable {
        @Column(name = "user_id")
        private Long userId;

        @Column(name = "book_id")
        private Long bookId;
    }
}
