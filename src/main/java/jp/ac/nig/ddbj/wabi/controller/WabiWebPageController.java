package jp.ac.nig.ddbj.wabi.controller;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class WabiWebPageController {

	@RequestMapping(value="/")
	@ResponseStatus(value=HttpStatus.NOT_FOUND)
	@ResponseBody
	public String showWabiWebPage(@RequestParam(value="lang",defaultValue="en") String lang, ModelMap model) {
		if (lang==null) { lang="en"; }
		
		if (lang.equals("ja")) {
			model.addAttribute("program_title", "blastn (クエリー：塩基配列, 検索対象:塩基配列)");
			
			model.addAttribute("language_chooser_label", "ENGLISH");
			model.addAttribute("language_chooser_url", "?lang=en");
			
			// http://www.ddbj.nig.ac.jp/index-j.html などの"j"の部分。
			model.addAttribute("language_code", "j");
			model.addAttribute("language_code2", "ja");
		}
		else {
			model.addAttribute("program_title", "blastn (Query: DNA vs. Target: DNA)");
			model.addAttribute("language_chooser_label", "JAPANESE");
			model.addAttribute("language_chooser_url", "?lang=ja");
			
			model.addAttribute("language_code", "e");
			model.addAttribute("language_code2", "en");
		}
		
		model.addAttribute("word_size", 11);

		// return new ModelAndView("wabi-webpage", model);
		return "WABI: The specified resource is not found.";
	}
	
	
}