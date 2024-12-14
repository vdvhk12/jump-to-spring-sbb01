package org.example.jtsb01.question.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.jtsb01.global.exception.DataNotFoundException;
import org.example.jtsb01.question.entity.Question;
import org.example.jtsb01.question.model.QuestionDto;
import org.example.jtsb01.question.model.QuestionForm;
import org.example.jtsb01.question.repository.QuestionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;

    public List<QuestionDto> getList() {
        return questionRepository.findAll().stream().map(QuestionDto::fromEntity).toList();
    }

    public Page<Question> getList(int page) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));

        Pageable pageable = PageRequest.of(page - 1, 10, Sort.by(sorts));
        return questionRepository.findAll(pageable);
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
