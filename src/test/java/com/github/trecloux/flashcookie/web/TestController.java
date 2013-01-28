package com.github.trecloux.flashcookie.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class TestController {
	
	@RequestMapping(value="/", method=RequestMethod.GET)
	public String root(Model model)  {
		return "home";
	}
	
	@RequestMapping(value="/setFlashAttr", method=RequestMethod.GET)
	public String setFlashAttr(RedirectAttributes redirectAttributes)  {
		redirectAttributes.addFlashAttribute("flashAttr", "It works");
		return "redirect:/";
	}
}
