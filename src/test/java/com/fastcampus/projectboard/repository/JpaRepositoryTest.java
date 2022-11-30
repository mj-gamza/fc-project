package com.fastcampus.projectboard.repository;

import com.fastcampus.projectboard.config.JpaConfig;
import com.fastcampus.projectboard.domain.Article;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

// JpaConfig의 존재를 DataJpaTest는 알지 못한다.(내가 직접 만든 것이기 때문) 그래서 @Import 필요
// 임포트 하지 않으면 JpaConfig에서 Auditing 한 것이 적용되지 않는다.
@DisplayName("JPA 연결 테스트")
@Import(JpaConfig.class)
@DataJpaTest
class JpaRepositoryTest {

    private final ArticleRepository articleRepository;
    private final ArticleCommentRepository articleCommentRepository;

    // @Autowired를 사용해서 필드 주입을 할 수 있다.
    // 하지만 Junit5와 스프링부트를 사용하면 Test에서도 생성자 주입을 사용할 수 있다.

    //각 생성자 파라미터에 @Autowired를 걸 수 있다.
    public JpaRepositoryTest(@Autowired ArticleRepository articleRepository, @Autowired ArticleCommentRepository articleCommentRepository) {
        this.articleRepository = articleRepository;
        this.articleCommentRepository = articleCommentRepository;
    }

    @DisplayName("select 테스트")
    @Test
    void givenTestData_whenSelecting_thenWorksFine(){

        //find한 내용을 List에 넣는다.
        List<Article> articles = articleRepository.findAll();

        assertThat(articles)
                .isNotNull()
                .hasSize(123);
    }

    @DisplayName("insert 테스트")
    @Test
    void givenTestData_whenInserting_thenWorksFine(){

        long previousCount = articleRepository.count();

        Article savedArticle = articleRepository.save(Article.of("new article", "new content", "#spring"));

        assertThat(articleRepository.count())
                .isEqualTo(previousCount+1);
    }

    @DisplayName("update 테스트")
    @Test
    void givenTestData_whenUpdating_thenWorksFine() {

        // 기존 데이터 하나 가져오기 (1번은 무조건 있을테니까 id=1 로 find)
        // 데이터가 없으면 Throw
        Article article = articleRepository.findById(1L).orElseThrow(IllegalArgumentException::new);
        String updatedHashtag = "#springboot";
        article.setHashtag(updatedHashtag);

        Article savedArticle = articleRepository.saveAndFlush(article);

        assertThat(savedArticle).hasFieldOrPropertyWithValue("hashtag",updatedHashtag);
    }

    @DisplayName("delete 테스트")
    @Test
    void givenTestData_whenDeleting_thenWorksFine() {

        Article article = articleRepository.findById(1L).orElseThrow(IllegalArgumentException::new);
        long previousArticleCount = articleRepository.count(); // 게시글 개수
        long previousArticleCommentCount = articleCommentRepository.count(); // 댓글 개수
        int deletedCommentsSize = article.getArticleComments().size(); // 삭제한 댓글 개수

        articleRepository.delete(article);

        assertThat(articleRepository.count()).isEqualTo(previousArticleCount - 1);
        assertThat(articleCommentRepository.count()).isEqualTo(previousArticleCommentCount - deletedCommentsSize);
    }
}
