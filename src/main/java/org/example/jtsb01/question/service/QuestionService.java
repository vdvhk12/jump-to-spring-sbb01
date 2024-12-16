package org.example.jtsb01.question.service;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.io.Serial;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.jtsb01.answer.entity.Answer;
import org.example.jtsb01.answer.model.AnswerDto;
import org.example.jtsb01.answer.repository.AnswerRepository;
import org.example.jtsb01.category.entity.Category;
import org.example.jtsb01.category.repository.CategoryRepository;
import org.example.jtsb01.global.exception.DataNotFoundException;
import org.example.jtsb01.question.entity.Question;
import org.example.jtsb01.question.model.QuestionDto;
import org.example.jtsb01.question.model.QuestionForm;
import org.example.jtsb01.question.repository.QuestionRepository;
import org.example.jtsb01.user.entity.SiteUser;
import org.example.jtsb01.user.model.SiteUserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final CategoryRepository categoryRepository;

    public List<QuestionDto> getList() {
        return questionRepository.findAll().stream().map(QuestionDto::fromEntity).toList();
    }

    public Page<QuestionDto> getList(int page) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));

        Pageable pageable = PageRequest.of(page - 1, 10, Sort.by(sorts));
        return questionRepository.findAll(pageable).map(QuestionDto::fromEntity);
    }

    public Page<QuestionDto> getList(String kw, int page) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));

        Pageable pageable = PageRequest.of(page - 1, 10, Sort.by(sorts));
//        Specification<Question> spec = searchQuestion(kw);
//        return questionRepository.findAll(spec, pageable).map(QuestionDto::fromEntity);
        return questionRepository.findAllByKeyword(kw, pageable).map(QuestionDto::fromEntity);
    }

    public QuestionDto getQuestion(Long id) {
        return QuestionDto.fromEntity(questionRepository.findById(id)
            .orElseThrow(() -> new DataNotFoundException("Question not found")));
    }

    public QuestionDto getQuestion(Long id, int page, String sort) {

        Question question = questionRepository.findById(id)
            .orElseThrow(() -> new DataNotFoundException("Question not found"));

        Pageable pageable = getPageable(page, sort);
        Page<AnswerDto> answerPage = answerRepository.findByQuestionId(question.getId(), pageable)
            .map(AnswerDto::fromEntity);

        return QuestionDto.fromEntity(question, answerPage);
    }

    private Pageable getPageable(int page, String sort) {
        List<Sort.Order> sorts = new ArrayList<>();
        if (sort.isEmpty() || sort.equals("old")) {
            sorts.add(Sort.Order.asc("createDate"));
        } else if(sort.equals("new")) {
            sorts.add(Sort.Order.desc("createDate"));
        } else if(sort.equals("recommend")) {
            sorts.add(Sort.Order.desc("voter"));
        }

        return PageRequest.of(page - 1, 10, Sort.by(sorts));
    }

    public void createQuestion(QuestionForm questionForm, SiteUserDto siteUserDto) {
        Category category = categoryRepository.findById(questionForm.getCategoryId())
            .orElseThrow(() -> new DataNotFoundException("Category not found"));
        questionRepository.save(Question.builder()
            .category(category)
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

    public void voteQuestion(Long id, SiteUserDto siteUserDto) {
        Question question = questionRepository.findById(id)
            .orElseThrow(() -> new DataNotFoundException("Question not found"));
        question.getVoter().add(SiteUserDto.fromDto(siteUserDto));
        questionRepository.save(question);
    }

    private Specification<Question> searchQuestion(String kw) {
        return new Specification<>() {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<Question> q, CriteriaQuery<?> query,
                CriteriaBuilder cb) {
                query.distinct(true);
                Join<Question, SiteUser> u1 = q.join("author", JoinType.LEFT);
                Join<Question, Answer> a = q.join("answerList", JoinType.LEFT);
                Join<Answer, SiteUser> u2 = a.join("author", JoinType.LEFT);
                return cb.or(cb.like(q.get("subject"), "%" + kw + "%"),
                    cb.like(q.get("content"), "%" + kw + "%"),
                    cb.like(u1.get("username"), "%" + kw + "%"),    // 질문 작성자
                    cb.like(a.get("content"), "%" + kw + "%"),      // 답변 내용
                    cb.like(u2.get("username"), "%" + kw + "%"));
            }
        };
    }
}
