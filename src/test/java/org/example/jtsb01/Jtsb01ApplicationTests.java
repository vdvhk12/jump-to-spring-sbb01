package org.example.jtsb01;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
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

    @Test
    @DisplayName("Question Create Test")
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
    void test02() {
        //given
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

        //when
        List<Question> questions = questionRepository.findAll();

        //then
        assertThat(questions).hasSize(2);
    }

}
