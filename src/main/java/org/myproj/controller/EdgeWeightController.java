package org.myproj.controller;

import java.io.IOException;

import javax.validation.Valid;

import org.json.JSONArray;
import org.myproj.model.FormSub;
import org.myproj.services.GraphEdgeWeightService;
import org.myproj.services.GraphService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class EdgeWeightController {

	public GraphEdgeWeightService graphEdgeService;
	
	
	 public EdgeWeightController(GraphEdgeWeightService graphEdgeService) {
		 this.graphEdgeService = graphEdgeService; 
	 }
	 
	
	@ModelAttribute("myformSub")
	   public FormSub getFormSub() {
	      return new FormSub();
	   }
	
	/* @RequestMapping(value = "/index", method = RequestMethod.POST)
    public ModelAndView index1(@RequestParam("file1") MultipartFile file,
    		@Valid @ModelAttribute("myformSub")FormSub formSub, 
           BindingResult result, ModelMap model) {
		 System.out.println("approach "+ formSub.getApproach());
		 System.out.println("file1 "+ formSub.getFile1());
		 System.out.println("file : "+ file.getSize());
		
        if("kcore".equals(formSub.getApproach())) {
        	kcore(file,formSub,model);
        }else
        	edgeweight(file,formSub,model);
		
		ModelAndView mav = new ModelAndView("index");
		return mav;
    }  */

	private ModelAndView edgeweight(MultipartFile file, FormSub formSub,ModelMap model) {
		try {
			model.addAttribute("nodes", graphEdgeService.preprocessEdge(file.getBytes(),formSub));
		} catch (IOException e) {
			e.printStackTrace();
		}
		ModelAndView mav = new ModelAndView("index");
		return mav;
	}

	@RequestMapping(value ="/index111",method=RequestMethod.GET)
	public ModelAndView index(Model model) {
		
		ModelAndView mav = new ModelAndView("index");
		return mav;
	}

	@RequestMapping(value = "/nodes111", method = RequestMethod.GET, produces = "application/json")
	public String test(@RequestParam(value = "nodeId", defaultValue = "111") String nodeId,
			@RequestParam(value = "approach") String approach,
			Model model) {
		System.out.println("nodeId : "+ nodeId);
		System.out.println("approach : "+ approach);
		JSONArray ja = new JSONArray();
		
		
		ja = graphEdgeService.clickProcess(Integer.parseInt(nodeId));
		
		
		return ja.toString();
	}
	
	@RequestMapping(value = "/getGroup111")
	public Integer[] getGroup() {
		return graphEdgeService.getGroup();
	}

}
