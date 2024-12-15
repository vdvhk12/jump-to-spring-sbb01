package org.example.jtsb01.answer.service;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.example.jtsb01.answer.entity.Answer;
import org.example.jtsb01.answer.model.AnswerDto;
import org.example.jtsb01.answer.model.AnswerForm;
import org.example.jtsb01.answer.repository.AnswerRepository;
import org.example.jtsb01.global.exception.DataNotFoundException;
import org.example.jtsb01.question.entity.Question;
import org.example.jtsb01.question.repository.QuestionRepository;
import org.example.jtsb01.user.model.SiteUserDto;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;

    public void createAnswer(Long id, AnswerForm answerForm, SiteUserDto siteUserDto) {
        Question question = questionRepository.findById(id)
            .orElseThrow(() -> new DataNotFoundException("Question not found"));

        answerRepository.save(Answer.builder()
            .content(answerForm.getContent())
            .createDate(LocalDateTime.now())
            .author(SiteUserDto.fromDto(siteUserDto))
            .question(question)
            .build());
    }

    public AnswerDto getAnswer(Long id) {
        return AnswerDto.fromEntity(answerRepository.findById(id)
            .orElseThrow(() -> new DataNotFoundException("Answer not found")));
    }

    public void modifyAnswer(Long id, AnswerForm answerForm) {
        Answer answer = answerRepository.findById(id)
            .orElseThrow(() -> new DataNotFoundException("Answer not found"));
        answerRepository.save(Answer.builder()
            .id(answer.getId())
            .content(answerForm.getContent())
            .createDate(answer.getCreateDate())
            .modifyDate(LocalDateTime.now())
            .author(answer.getAuthor())
            .question(answer.getQuestion())
            .build());
    }

    public void deleteAnswer(Long id) {
        answerRepository.deleteById(id);
    }

    public void voteAnswer(Long id, SiteUserDto siteUserDto) {
        Answer answer = answerRepository.findById(id)
            .orElseThrow(() -> new DataNotFoundException("Answer not found"));
        answer.getVoter().add(SiteUserDto.fromDto(siteUserDto));
        answerRepository.save(answer);
    }
}
