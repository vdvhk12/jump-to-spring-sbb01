package org.example.jtsb01.question.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.jtsb01.answer.model.AnswerDto;
import org.example.jtsb01.answer.repository.AnswerRepository;
import org.example.jtsb01.global.exception.DataNotFoundException;
import org.example.jtsb01.question.entity.Question;
import org.example.jtsb01.question.model.QuestionDto;
import org.example.jtsb01.question.model.QuestionForm;
import org.example.jtsb01.question.repository.QuestionRepository;
import org.example.jtsb01.user.model.SiteUserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

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

    public QuestionDto getQuestion(Long id, int page) {

        Question question = questionRepository.findById(id)
            .orElseThrow(() -> new DataNotFoundException("Question not found"));

        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.asc("createDate"));
        
        Pageable pageable = PageRequest.of(page - 1, 10, Sort.by(sorts));
        Page<AnswerDto> answerPage = answerRepository.findByQuestionId(question.getId(), pageable)
            .map(AnswerDto::fromEntity);

        return QuestionDto.fromEntity(question, answerPage);
    }

    public void createQuestion(QuestionForm questionForm, SiteUserDto siteUserDto) {
        questionRepository.save(Question.builder()
            .subject(questionForm.getSubject())
            .content(questionForm.getContent())
            .createDate(LocalDateTime.now())
            .author(SiteUserDto.fromDto(siteUserDto))
            .build());
    }

    public void modifyQuestion(Long id, QuestionForm questionForm) {
        Question question = questionRepository.findById(id)
            .orElseThrow(() -> new DataNotFoundException("Question not found"));
        questionRepository.save(Question.builder()
            .id(question.getId())
            .subject(questionForm.getSubject())
            .content(questionForm.getContent())
            .createDate(question.getCreateDate())
            .modifyDate(LocalDateTime.now())
            .author(question.getAuthor())
            .answerList(question.getAnswerList())
            .build());
    }

    public void deleteQuestion(Long id) {
        questionRepository.deleteById(id);
    }
}
