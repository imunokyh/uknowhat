package kr.or.uknowhat.api.ubusiness.question.web;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import kr.or.uknowhat.api.framework.vo.Result;
import kr.or.uknowhat.api.ubusiness.common.ErrorCode;
import kr.or.uknowhat.api.ubusiness.question.service.FaqService;
import kr.or.uknowhat.api.ubusiness.question.vo.FaqVo;
import lombok.extern.slf4j.Slf4j;


/**
 * <pre>
 * FAQ 게시판 Controller
 * </pre>
 *
 * @author cbinlim@crossent.com
 * @since 2021. 04. 19.
 * @see
 */
@RestController
@RequestMapping(value = "/api/v1/sample")
@Slf4j
public class FaqController {


    @Autowired
    private FaqService faqService;

    @ApiOperation(value = "FAQ 게시글 페이지 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "페이지", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "size", value = "사이즈", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "searchText", value = "검색 내용", dataType = "string", paramType = "query")})
    @GetMapping(value="/faqs")
    public Result listFaq(@RequestParam(defaultValue = "0")  int page,
                                 @RequestParam(defaultValue = "5")  int size,
                                 @RequestParam(required = false, defaultValue = "") String searchText) {
        Result ret = new Result();
        ret.setCode(ErrorCode.SUCCESS);
        ret.setResult(faqService.listFaq(page, size, searchText));
        return ret;
    }

    @ApiOperation(value = "id를 이용한 개별 조회")
    @ApiImplicitParam(name = "id", value = "아이디", required = true, dataType = "Long", paramType = "path")
    @GetMapping(value="/faqs/{id}")
    public Result getFaq(@PathVariable Long id) {
        Result ret = new Result();
        ret.setCode(ErrorCode.SUCCESS);
        ret.setResult(faqService.getFaq(id));
        return ret;
    }

    @ApiOperation(value = "FAQ 게시글 등록")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "faqVo", value = "FAQ VO", required = true, dataType = "FaqVo", paramType = "body")
    })
    @PostMapping(value="/faqs")
    public Result insertFaq(@RequestBody @Valid FaqVo faqVo) {
        faqService.insertFaq(faqVo);
        Result ret = new Result();
        ret.setCode(ErrorCode.SUCCESS);
        return ret;
    }

    @ApiOperation(value = "FAQ 게시글 수정")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "faqVo", value = "FAQ VO", required = true, dataType = "FaqVo", paramType = "body")
    })
    @PutMapping(value="/faqs")
    public Result updateFaq(@RequestBody @Valid FaqVo faqVo) {
        faqService.updateFaq(faqVo);
        Result ret = new Result();
        ret.setCode(ErrorCode.SUCCESS);
        return ret;
    }

    @ApiOperation(value = "id를 이용한 게시글 삭제")
    @ApiImplicitParam(name = "id", value = "아이디", required = true, dataType = "Long", paramType = "path")
    @DeleteMapping(value="/faqs/{id}")
    public Result deleteFaq(@PathVariable Long id) {
        faqService.deleteFaq(id);
        Result ret = new Result();
        ret.setCode(ErrorCode.SUCCESS);
        return ret;
    }

}


