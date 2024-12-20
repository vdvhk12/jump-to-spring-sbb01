package org.example.jtsb01.answer.repository;

import org.example.jtsb01.answer.entity.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
    Page<Answer> findByQuestionId(Long questionId, Pageable pageable);
    Page<Answer> findAllByAuthorId(Long authorId, Pageable pageable);
}
