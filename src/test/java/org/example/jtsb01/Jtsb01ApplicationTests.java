package org.example.jtsb01;

import static org.assertj.core.api.Assertions.*;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.example.jtsb01.answer.Answer;
import org.example.jtsb01.answer.AnswerRepository;
import org.example.jtsb01.question.Question;
import org.example.jtsb01.question.QuestionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class Jtsb01ApplicationTests {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @Test
    @DisplayName("Question Create Test")
    @Transactional
    void test01() {
        //given
        //when
        Question question1 = questionRepository.save(Question.builder()
            .subject("sbb가 무엇인가요?")
            .content("sbb에 대해서 알고 싶습니다.")
            .createDate(LocalDateTime.now())
            .build());

        Question question2 = questionRepository.save(Question.builder()
            .subject("스프링부트 모델 질문입니다.")
            .content("id는 자동으로 생성되나요?")
            .createDate(LocalDateTime.now())
            .build());

        //then
        assertThat(question1.getId()).isEqualTo(1);
        assertThat(question2.getId()).isEqualTo(2);
        assertThat(question1.getSubject()).isEqualTo("sbb가 무엇인가요?");
        assertThat(question2.getContent()).isEqualTo("id는 자동으로 생성되나요?");
    }

    @Test
    @DisplayName("Question findAll Test")
    @Transactional
    void test02() {
        //given
        questionRepository.save(Question.builder()
            .subject("sbb가 무엇인가요?")
            .content("sbb에 대해서 알고 싶습니다.")
            .createDate(LocalDateTime.now())
            .build());

        questionRepository.save(Question.builder()
            .subject("스프링부트 모델 질문입니다.")
            .content("id는 자동으로 생성되나요?")
            .createDate(LocalDateTime.now())
            .build());

        //when
        List<Question> questions = questionRepository.findAll();

        //then
        assertThat(questions).hasSize(2);
    }

    @Test
    @DisplayName("Question findById Test")
    @Transactional
    void test03() {
        //given
        Question question = questionRepository.save(Question.builder()
            .subject("sbb가 무엇인가요?")
            .content("sbb에 대해서 알고 싶습니다.")
            .createDate(LocalDateTime.now())
            .build());

        //when
        Optional<Question> result = questionRepository.findById(question.getId());

        //then
        assertThat(result).isPresent();
        assertThat(result.get().getContent()).isEqualTo(question.getContent());
    }

    @Test
    @DisplayName("Question findBySubject Test")
    @Transactional
    void test04() {
        //given
        Question question = questionRepository.save(Question.builder()
            .subject("sbb가 무엇인가요?")
            .content("sbb에 대해서 알고 싶습니다.")
            .createDate(LocalDateTime.now())
            .build());

        //when
        Optional<Question> result = questionRepository.findBySubject(question.getSubject());

        //then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(question.getId());
        assertThat(result.get().getContent()).isEqualTo(question.getContent());
    }

    @Test
    @DisplayName("Question findBySubjectAndContent Test")
    @Transactional
    void test05() {
        //given
        Question question = questionRepository.save(Question.builder()
            .subject("sbb가 무엇인가요?")
            .content("sbb에 대해서 알고 싶습니다.")
            .createDate(LocalDateTime.now())
            .build());

        //when
        Optional<Question> result = questionRepository.findBySubjectAndContent(
            question.getSubject(), question.getContent());

        //then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(question.getId());
        assertThat(result.get().getContent()).isEqualTo(question.getContent());
    }

    @Test
    @DisplayName("Question findBySubjectLike Test")
    @Transactional
    void test06() {
        //given
        Question question1 = questionRepository.save(Question.builder()
            .subject("sbb가 무엇인가요?")
            .content("sbb에 대해서 알고 싶습니다.")
            .createDate(LocalDateTime.now())
            .build());

        Question question2 = questionRepository.save(Question.builder()
            .subject("sbb가 무엇인가요?")
            .content("sbb에 대해서 알고 싶습니다.")
            .createDate(LocalDateTime.now())
            .build());

        //when
        List<Question> result = questionRepository.findBySubjectLike("sbb%");

        //then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(question1.getId());
        assertThat(result.get(1).getId()).isEqualTo(question2.getId());
    }

    @Test
    @DisplayName("Question Update Test")
    @Transactional
    void test07() {
        //given
        Question question = questionRepository.save(Question.builder()
            .subject("sbb가 무엇인가요?")
            .content("sbb에 대해서 알고 싶습니다.")
            .createDate(LocalDateTime.now())
            .answerList(new ArrayList<>())
            .build());

        //when
        Question result = questionRepository.save(Question.builder()
            .id(question.getId())
            .subject(question.getSubject())
            .content("수정된 제목")
            .createDate(question.getCreateDate())
            .answerList(question.getAnswerList())
            .build());

        //then
        assertThat(result.getId()).isEqualTo(question.getId());
        assertThat(result.getContent()).isEqualTo("수정된 제목");
    }

    @Test
    @DisplayName("Question Delete Test")
    @Transactional
    void test08() {
        //given
        Question question = questionRepository.save(Question.builder()
            .subject("sbb가 무엇인가요?")
            .content("sbb에 대해서 알고 싶습니다.")
            .createDate(LocalDateTime.now())
            .answerList(new ArrayList<>())
            .build());

        //when
        questionRepository.delete(question);
        Optional<Question> result = questionRepository.findById(question.getId());

        //then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Answer Create Test")
    @Transactional
    void test09() {
        //given
        //when
        Question question = questionRepository.save(Question.builder()
            .subject("스프링부트 모델 질문입니다.")
            .content("id는 자동으로 생성되나요?")
            .createDate(LocalDateTime.now())
            .answerList(new ArrayList<>())
            .build());

        Answer answer = answerRepository.save(Answer.builder()
            .content("네 자동으로 생성됩니다.")
            .createDate(LocalDateTime.now())
            .question(question)
            .build());

        //then
        assertThat(answer.getId()).isEqualTo(1);
        assertThat(answer.getContent()).isEqualTo("네 자동으로 생성됩니다.");
        assertThat(answer.getQuestion().getId()).isEqualTo(question.getId());
    }

    @Test
    @DisplayName("Answer Read Test")
    @Transactional
    void test10() {
        //given
        Question question = questionRepository.save(Question.builder()
            .subject("스프링부트 모델 질문입니다.")
            .content("id는 자동으로 생성되나요?")
            .createDate(LocalDateTime.now())
            .answerList(new ArrayList<>())
            .build());

        Answer answer = answerRepository.save(Answer.builder()
            .content("네 자동으로 생성됩니다.")
            .createDate(LocalDateTime.now())
            .question(question)
            .build());

        //when
        Optional<Answer> result = answerRepository.findById(answer.getId());

        //then
        assertThat(result).isPresent();
        assertThat(answer.getContent()).isEqualTo(result.get().getContent());
    }

    @Test
    @DisplayName("Answer Update Test")
    @Transactional
    void test11() {
        //given
        Question question = questionRepository.save(Question.builder()
            .subject("스프링부트 모델 질문입니다.")
            .content("id는 자동으로 생성되나요?")
            .createDate(LocalDateTime.now())
            .answerList(new ArrayList<>())
            .build());

        Answer answer = answerRepository.save(Answer.builder()
            .content("네 자동으로 생성됩니다.")
            .createDate(LocalDateTime.now())
            .question(question)
            .build());

        //when
        Answer result = answerRepository.save(Answer.builder()
            .id(answer.getId())
            .content("수정된 답변")
            .createDate(answer.getCreateDate())
            .question(answer.getQuestion())
            .build());

        //then
        assertThat(result.getId()).isEqualTo(answer.getId());
        assertThat(result.getContent()).isEqualTo("수정된 답변");
    }

    @Test
    @DisplayName("Answer Delete Test")
    @Transactional
    void test12() {
        //given
        Question question = questionRepository.save(Question.builder()
            .subject("스프링부트 모델 질문입니다.")
            .content("id는 자동으로 생성되나요?")
            .createDate(LocalDateTime.now())
            .answerList(new ArrayList<>())
            .build());

        Answer answer = answerRepository.save(Answer.builder()
            .content("네 자동으로 생성됩니다.")
            .createDate(LocalDateTime.now())
            .question(question)
            .build());

        //when
        answerRepository.delete(answer);
        Optional<Answer> result = answerRepository.findById(answer.getId());

        //then
        assertThat(result).isEmpty();
    }

}
