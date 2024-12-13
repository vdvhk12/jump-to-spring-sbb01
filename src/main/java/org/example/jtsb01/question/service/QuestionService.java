package org.example.jtsb01.question.service;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.jtsb01.global.exception.DataNotFoundException;
import org.example.jtsb01.question.entity.Question;
import org.example.jtsb01.question.model.QuestionDto;
import org.example.jtsb01.question.model.QuestionForm;
import org.example.jtsb01.question.repository.QuestionRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;

    public List<QuestionDto> getList() {
        return questionRepository.findAll().stream().map(QuestionDto::fromEntity).toList();
    }

    public QuestionDto getQuestion(Long id) {
        return QuestionDto.fromEntity(questionRepository.findById(id)
            .orElseThrow(() -> new DataNotFoundException("Question not found")));
    }

    public void createQuestion(QuestionForm questionForm) {
        questionRepository.save(Question.builder()
            .subject(questionForm.getSubject())
            .content(questionForm.getContent())
            .createDate(LocalDateTime.now())
            .build());
    }
}
