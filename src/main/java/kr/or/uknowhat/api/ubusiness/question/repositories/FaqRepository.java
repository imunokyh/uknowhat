package kr.or.uknowhat.api.ubusiness.question.repositories;

import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import kr.or.uknowhat.api.ubusiness.question.domain.Faq;

/**
 * 
 * Faq Repository
 *
 */
@Repository
public interface FaqRepository extends JpaRepository<Faq, Long> {

    Page<Faq> findAllByQuestionContaining(Pageable pageable, String searchText);

    /* 이전글 */
    @Query(value = "SELECT id, question, answer, created_date, created_user_id, modified_date, modified_user_id " +
            " FROM faq_tb WHERE created_date < :createdDate ORDER BY created_date DESC LIMIT 1", nativeQuery = true)
    Faq  findPreById(@Param("createdDate") Date createdDate);
    /* 다음글 */
    @Query(value = "SELECT id, question, answer, created_date, created_user_id, modified_date, modified_user_id " +
            " FROM faq_tb WHERE created_date > :createdDate ORDER BY created_date  ASC LIMIT 1", nativeQuery = true)
    Faq  findNextById(@Param("createdDate") Date createdDate);
}
