package org.example.jtsb01.question.repository;

import java.util.List;
import java.util.Optional;
import org.example.jtsb01.question.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    Optional<Question> findBySubject(String subject);
    Optional<Question> findBySubjectAndContent(String subject, String content);
    List<Question> findBySubjectLike(String subject);
    Page<Question> findAll(Pageable pageable);
}
