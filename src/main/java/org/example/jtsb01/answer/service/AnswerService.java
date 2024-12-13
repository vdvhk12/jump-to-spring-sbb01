package org.example.jtsb01.answer.service;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.example.jtsb01.answer.entity.Answer;
import org.example.jtsb01.answer.model.AnswerForm;
import org.example.jtsb01.answer.repository.AnswerRepository;
import org.example.jtsb01.global.exception.DataNotFoundException;
import org.example.jtsb01.question.entity.Question;
import org.example.jtsb01.question.repository.QuestionRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;

    public void createAnswer(Long id, AnswerForm answerForm) {
        Question question = questionRepository.findById(id)
            .orElseThrow(() -> new DataNotFoundException("Question not found"));

        answerRepository.save(Answer.builder()
            .content(answerForm.getContent())
            .createDate(LocalDateTime.now())
            .question(question)
            .build());
    }
}
