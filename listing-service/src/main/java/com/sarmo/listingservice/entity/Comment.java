package com.sarmo.listingservice.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Уникальный идентификатор комментария

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "listing_id", nullable = false)
    @JsonBackReference("listingComments") // Уникальное имя для связи с Listing
    private Listing listing;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User author; // Связь с локальной сущностью User

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content; // Текст комментария

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    @JsonBackReference("commentReplies") // Уникальное имя для связи с родительским комментарием
    private Comment parentComment;

    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("commentReplies") // Соответствующее имя для управляющей ссылки
    private List<Comment> replies = new ArrayList<>();

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now(); // Дата создания

    @Column(nullable = false)
    private boolean edited = false; // Поле для пометки комментария как отредактированного

    public Comment() {}

    public Comment(Listing listing, User author, String content, Comment parentComment) {
        this.listing = listing;
        this.author = author;
        this.content = content;
        this.parentComment = parentComment;
        this.createdAt = LocalDateTime.now();
    }

    public List<Comment> flattenReplies() {
        List<Comment> flattened = new ArrayList<>();
        flattenRepliesRecursive(this, flattened);
        return flattened;
    }

    private void flattenRepliesRecursive(Comment comment, List<Comment> flattened) {
        flattened.add(comment);
        for (Comment reply : comment.getReplies()) {
            flattenRepliesRecursive(reply, flattened);
        }
    }

    // Геттеры и сеттеры

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Listing getListing() {
        return listing;
    }

    public void setListing(Listing listing) {
        this.listing = listing;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Comment getParentComment() {
        return parentComment;
    }

    public void setParentComment(Comment parentComment) {
        this.parentComment = parentComment;
    }

    public List<Comment> getReplies() {
        return replies;
    }

    public void setReplies(List<Comment> replies) {
        this.replies = replies;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isEdited() {
        return edited;
    }

    public void setEdited(boolean edited) {
        this.edited = edited;
    }
}