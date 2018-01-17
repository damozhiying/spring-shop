package de.beuth.clara.claraSoftware.guiController;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.*;

@Controller
public class GUIController {
	
	@RequestMapping("/")
	public String index() {
		return "index";
	}

}
