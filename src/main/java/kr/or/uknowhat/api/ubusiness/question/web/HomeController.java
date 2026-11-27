package kr.or.uknowhat.api.ubusiness.question.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class HomeController {
	@GetMapping
	public String index() {
		return "/index.html";
	}
	
	/**
	 * Vue Router(history 모드) 화면 경로는 index.html 로 전달한다.
	 * 이 처리가 없으면 게임 중 새로고침하거나 주소로 직접 접근할 때 401/404 가 발생한다.
	 */
	@GetMapping(value = { "/login", "/signup", "/about", "/h", "/play/**", "/exam/**" })
	public String spa() {
		return "forward:/index.html";
	}
}
