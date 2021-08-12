package kr.or.uknowhat.api.ubusiness.question.service.impl;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.or.uknowhat.api.ubusiness.question.domain.Faq;
import kr.or.uknowhat.api.ubusiness.question.repositories.FaqRepository;
import kr.or.uknowhat.api.ubusiness.question.service.FaqService;
import kr.or.uknowhat.api.ubusiness.question.vo.FaqVo;
import lombok.extern.slf4j.Slf4j;


@Service
@Transactional
@Slf4j
public class FaqServiceImpl implements FaqService {

    @Autowired
    private FaqRepository faqRepo;


    /**
     * FAQ 게시글 페이징 조회
     * @param page 페이지
     * @param size 사이즈
     * @param searchText 검색 내용
     * @return FAQ 게시글 목록
     */
    @Override
    public Page<Faq> listFaq(int page, int size, String searchText) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        return faqRepo.findAllByQuestionContaining(pageable, searchText);
    }

    /**
     * Id를 이용한 FAQ 개별조회
     * @param id FAQ id
     * @return FAQ 상세
     */
    @Override
    public Faq getFaq(Long id) {
        Faq faq = null;

        Optional<Faq> optionalFaq = faqRepo.findById(id);
        if (optionalFaq.isPresent()) {
            faq = optionalFaq.get();
            faq.setPreFaq(faqRepo.findPreById(faq.getCreatedDate()));
            faq.setNextFaq(faqRepo.findNextById(faq.getCreatedDate()));
        }

        return faq;
    }

    /**
     * FAQ 게시글 등록
     */
    @Override
    public void insertFaq(FaqVo faqVo) {
        Faq faq = new Faq();
        faq.setQuestion(faqVo.getQuestion());
        faq.setAnswer(faqVo.getAnswer());
        faq.setCreatedDate(new Date());
        faq.setModifiedDate(new Date());
        faqRepo.save(faq);
    }

    /**
     * FAQ 게시글 수정
     */
    @Override
    public void updateFaq(FaqVo faqVo) {
        Optional<Faq> optionalFaq = faqRepo.findById(faqVo.getId());
        if (optionalFaq.isPresent()) {
            Faq faq = optionalFaq.get();
            faq.setQuestion(faqVo.getQuestion());
            faq.setAnswer(faqVo.getAnswer());
            faq.setModifiedDate(new Date());
            faqRepo.save(faq);
        }
    }

    /**
     * id를 이용한 게시글 삭제
     * @param id FAQ id
     */
    @Override
    public void deleteFaq(Long id) {
        Optional<Faq> optionalFaq = faqRepo.findById(id);
        if(optionalFaq.isPresent()) {
            faqRepo.delete(optionalFaq.get());
        }
    }
}