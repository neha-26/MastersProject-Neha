package org.myproj.controller;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

import javax.validation.Valid;

import org.json.JSONArray;
import org.json.JSONObject;
import org.myproj.model.FormSub;
import org.myproj.services.GraphService;
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
public class graphController {

	public GraphService graphService;
	
	
	public graphController(GraphService graphService) {
		this.graphService = graphService;
	}
	
	@ModelAttribute("myformSub")
	   public FormSub getFormSub() {
	      return new FormSub();
	   }
	
	 @RequestMapping(value = "/index", method = RequestMethod.POST)
    public ModelAndView index(@RequestParam("file1") MultipartFile file,
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
    } 

	private ModelAndView kcore(MultipartFile file, FormSub formSub,ModelMap model) {
		Instant start = Instant.now();
		try {
			JSONArray jAry = new JSONArray();
			jAry = graphService.preprocess(file.getBytes(),formSub);
					
			Instant finish = Instant.now();
			long timeElapsed = Duration.between(start, finish).toMillis(); // in millis
			
			model.addAttribute("nodes", jAry);
			model.addAttribute("totalTime",timeElapsed);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		ModelAndView mav = new ModelAndView("index");
		return mav;
	}

	private ModelAndView edgeweight(MultipartFile file, FormSub formSub,ModelMap model) {
		Instant start = Instant.now();
		try {
			JSONArray jAry = new JSONArray();
			jAry = graphService.preprocessEdge(file.getBytes(),formSub);
			
			Instant finish = Instant.now();
			long timeElapsed = Duration.between(start, finish).toMillis(); // in millis
			
			model.addAttribute("nodes", jAry);
			model.addAttribute("totalTime",timeElapsed);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		ModelAndView mav = new ModelAndView("index");
		return mav;
	}

	@RequestMapping(value ="/index",method=RequestMethod.GET)
	public ModelAndView indexPage(Model model) {
		
		ModelAndView mav = new ModelAndView("index");
		return mav;
	}

	@RequestMapping(value = "/nodes", method = RequestMethod.GET, produces = "application/json")
	public String getChildNodes(@RequestParam(value = "nodeId", defaultValue = "111") String nodeId,
			@RequestParam(value = "approach") String approach,
			Model model) {
		System.out.println("nodeId : "+ nodeId);
		System.out.println("approach : "+ approach);
		Instant start = Instant.now();
		JSONObject js = new JSONObject();
		JSONArray ja = new JSONArray();
		
		if(approach.equals("kcore")) {
			ja = graphService.clickProcess(Integer.parseInt(nodeId));
		}
		else
		ja = graphService.clickProcess1(Integer.parseInt(nodeId));
		
		Instant finish = Instant.now();
		long timeElapsed = Duration.between(start, finish).toMillis(); // in millis
		
		js.append("totalTime", timeElapsed);
		js.append("nodesChild", ja);
		
		return js.toString();
		//return ja.toString();
	}
	
	@RequestMapping(value = "/getGroup")
	public Integer[] getGroup() {
		return graphService.getGroup();
	}

}
