package com.fastcampus.projectboard.repository;

import com.fastcampus.projectboard.domain.Article;
import com.fastcampus.projectboard.domain.QArticle;
import com.querydsl.core.types.dsl.DateTimeExpression;
import com.querydsl.core.types.dsl.StringExpression;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface ArticleRepository extends
        JpaRepository<Article, Long>,
        QuerydslPredicateExecutor<Article>, // 엔티티 안에 있는 모든 필드에 대한 기본 검색기능을 추가해준다. 대소문자를 구분하지는 않는다. 하지만 부분검색이 안된다.
        QuerydslBinderCustomizer<QArticle> {

    // 검색에 대한 세부 기능 재정의
    // 인터페이스 파일이라 이 안에서 원래 구현을 넣을 수 없지만, Java8부터 가능해짐
    @Override
    default void customize(QuerydslBindings bindings, QArticle root){
        bindings.excludeUnlistedProperties(true);
        // 현재 QuerydslPredicateExecutor에 의해서 엔티티에 있는 모든 필드에 대한 검색이 되고 있다. 우리가 원하는 필드만 설정하기 위해서 사용한다.
        // true로 하면 list하지 않은 프로퍼티는 검색에서 제외한다. (기본값은 false)

        bindings.including(root.title, root.content, root.hashtag, root.createdAt, root.createdBy); // 검색을 원하는 필드를 추가한다.

        // 이 둘의 차이는 쿼리문이 다르다.
//        bindings.bind(root.title).first(StringExpression::likeIgnoreCase); // like ''
        bindings.bind(root.title).first(StringExpression::containsIgnoreCase); // like '%%'
        // 부분검색을 하려면 검색어에 %를 넣어주어야 하는데, like를 사용하면 이 %를 내가 직접 넣어주어야 한다.
        // % 넣는 것을 수동으로 정하고 싶을 때에는 like를 사용하고, 그렇지 않을 경우에는 contains를 사용하면 된다.
        bindings.bind(root.content).first(StringExpression::containsIgnoreCase);
        bindings.bind(root.hashtag).first(StringExpression::containsIgnoreCase);
        bindings.bind(root.createdAt).first(DateTimeExpression::eq);
        bindings.bind(root.createdBy).first(StringExpression::containsIgnoreCase);

    }

}
