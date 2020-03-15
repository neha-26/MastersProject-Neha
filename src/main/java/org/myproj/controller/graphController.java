package org.myproj.controller;

import org.myproj.services.GraphService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class graphController {

	private final GraphService graphService;

	public graphController(GraphService graphService) {
		this.graphService = graphService;
	}

	@RequestMapping("/index")
	public ModelAndView index(Model model) {
		//model.addAttribute("rootJ", graphService.preprocess().toString());
		//model.addAttribute("nodes", graphService.createNodeArray());
		model.addAttribute("nodes", graphService.preprocess());
		// model.addAttribute("nodeGroups", );
		ModelAndView mav = new ModelAndView("index");
		return mav;
		//return "index";
	}

	@RequestMapping(value = "/nodes", method = RequestMethod.GET, produces = "application/json")
	public String test(@RequestParam(value = "nodeId", defaultValue = "111") String nodeId,Model model) {
		System.out.println("nodeId : "+ nodeId);
		//return "course_name\":\"def\"";
		return graphService.clickProcess(Integer.parseInt(nodeId)).toString();
	}

	@RequestMapping("/welcome")
	public String loginMessage() {
		return "welcome";
	}

}
