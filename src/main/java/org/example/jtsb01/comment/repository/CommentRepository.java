package org.example.jtsb01.comment.repository;

import org.example.jtsb01.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

}
