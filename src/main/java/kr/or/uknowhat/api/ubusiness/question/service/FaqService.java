package kr.or.uknowhat.api.ubusiness.question.service;

import org.springframework.data.domain.Page;

import kr.or.uknowhat.api.ubusiness.question.domain.Faq;
import kr.or.uknowhat.api.ubusiness.question.vo.FaqVo;

public interface FaqService {

    /**
     * FAQ 게시글 페이징 조회
     * @param page 페이지
     * @param size 사이즈
     * @param searchText 검색 내용
     * @return FAQ 게시글 목록
     */
    Page<Faq> listFaq(int page, int size, String searchText);

    /**
     * Id를 이용한 FAQ 개별조회
     * @param id FAQ id
     * @return FAQ 상세
     */
    Faq getFaq(Long id);

    /**
     * FAQ 게시글 등록
     * @param faqVo FAQ vo
     */
    void insertFaq(FaqVo faqVo);

    /**
     * FAQ 게시글 수정
     * @param faqVo FAQ vo
     */
    void updateFaq(FaqVo faqVo);

    /**
     * id를 이용한 게시글 삭제
     * @param id FAQ id
     */
    void deleteFaq(Long id);

}
