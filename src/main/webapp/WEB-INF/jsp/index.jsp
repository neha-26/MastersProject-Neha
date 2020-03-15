<%@ page language="java" contentType="text/html;"
	pageEncoding="ISO-8859-1"%>

<!DOCTYPE html>
<html>
<head>
<title>Force-Directed Graph</title>
<style>
.node {
	cursor: pointer;
	stroke: #000;
	stroke-width: .5px;
}

.link {
	fill: none;
	stroke: #9ecae1;
	stroke-width: 1.5px;
}
</style>
<script type="text/javascript"
	src="http://code.jquery.com/jquery-1.11.0.min.js"></script>
<script src="https://d3js.org/d3.v3.min.js"></script>
</head>
<body>
	<%--  <h2>Your name is ${nameX}</h2> --%>
	<script type="text/javascript">
		
    	function showSubNodes(nodeId) {
    		 $.ajax({
    		        url : 'nodes?nodeId=' + nodeId,
    		        method : 'GET',
    		        async : false,
    		        complete : function(data) {
    		            console.log(data.responseText);
    		            nodesChild = data;
    		        }
    		    });
    	}
</script>

	<script type="text/javascript">

var width = 1200,
    height = 600,
    root;

//root = "[${rootJ}]";
//root = JSON.parse('${rootJ}');
//console.log("root from controller :- "+root);
 var nodes = [];//,links =[];
  
  nodes = JSON.parse('${nodes}');
  console.log("nodes from controller :- "+nodes);

var force = d3.layout.force()
    .gravity(.02)
    .charge(function(d) { return d._children ? -d.size / 100 : -30; })
    .linkDistance(function(d) { return d.target._children ? 120 : 120; })//collapse : expand
    .size([width, height])
    .on("tick", tick);

var svg = d3.select("body").append("svg")
    .attr("width", width)
    .attr("height", height);

var link = svg.selectAll(".link"),
    node = svg.selectAll(".node");
    
var fill = d3.scale.category10();

function getDepth(n) { return n.depth; }

update();

 var links;
 
function update() {
	 
  // nodes = flatten(root),
   links = d3.layout.tree().links(nodes);

  // Restart the force layout.
  force
      .nodes(nodes)
      .links(links)
      .start();
  
//this updates the convex hulls
  svg.selectAll("group").remove();
  
//Update the links…
  link = svg.selectAll("line.link")
      .data(links, function(d) { return d.target.id; });

  // Exit any old links.
  link.exit().remove();

  // Enter any new links.
  link.enter().insert("line", ".node")
      .attr("class", "link")
      .attr("x1", function(d) { return d.source.x; })
      .attr("y1", function(d) { return d.source.y; })
      .attr("x2", function(d) { return d.target.x; })
      .attr("y2", function(d) { return d.target.y; });

  // Update the nodes…
  node = node.data(nodes, function(d) { return d.id; })
  			.style("fill", color)

  // Exit any old nodes.
  node.exit().remove();
  			
  

  // Enter any new nodes.
  node.enter().append("circle")
      .attr("class", "node")
      .attr("cx", function(d) { return d.x; })
      .attr("cy", function(d) { return d.y; })
      .attr("r", function(d) { return d.children ? 12.5 : Math.sqrt(d.size) / 10;})
      // .attr("r", function(d) { return Math.sqrt(d.size) / 10;})
     .style("fill", color)
    .style("stroke-dasharray",  function(d) { if (d.children) { return "10,3" } else { return "0,0" }})
      .on("click", click)
  .append("title")
  .text(function(d) { return d.name })
  .call(force.drag);
  
  
  node.on('mouseover', function (d) {
    // Highlight the nodes: every node is green except of him
     node.style('fill', "#DCDCDC").style('stroke', "#DCDCDC");
     d3.select(this).style('fill', color).style('stroke', "#000");
    links.forEach(function(link) {
    	if(link.source.id === d.id) {
    		
    		node.filter(function(e) {
      			 return (e === link.target); //connected nodes
    	    }).style('fill', color)
    	      .style('stroke', "#000");
    	}
    	else if(link.target.id === d.id) {
			
    		node.filter(function(e) {
     			 return (e === link.source); //connected nodes
   	   		 }).style('fill', color)
   	     	   .style('stroke', "#000"); 
    	}
    	
    });
   
    // Highlight the connections
    link
      .style('stroke', function (link_d) { //console.log("link_d source: "+link_d.source.name); 
     		// console.log("link_d target: "+link_d.target.name);
      			return link_d.source.id === d.id || link_d.target.id === d.id ? '#ff9896' : 'lightgrey';})
      			//#b8b8b8 - gray //#ff9896 -  pink
      .style('stroke-width', function (link_d) { return link_d.source.id === d.id || link_d.target.id === d.id ? 4 : 1;})
  })
  .on('mouseout', function (d) {
    node.style('fill', color).style('stroke', "#000"); 
    link
      .style('stroke', 'slategray')//##9ecae1
      .style('stroke-width', '1')
  });
  
  
//new code
  node.transition()
     .attr("r", function(d) { return d.children ? 12.5 : Math.sqrt(d.size) / 10; })
	 .style("stroke-dasharray",  function(d) { if (d.children) { return "10,3" } else { return "0,0" }});// make the stroke dashed
	
  
}

function tick() {
	
   //nodes = flatten(root),
   links = d3.layout.tree().links(nodes);
  	
   /* svg.selectAll("path").remove();

  svg.selectAll("path#group")
      .data(groupNodes)
      .enter().insert("path", "circle")
      .attr("class", "group")
      .style("fill", groupHullFill)
      .style("stroke", groupHullFill)
      .style("stroke-width", 45)
      .style("stroke-linejoin", "round")
      .style("opacity", .5) 
  	  .attr("ID","group")
      .attr("d", groupPath);
  
 svg.selectAll("path#subgroup")
  .data(subgroupNodes)
  .enter().insert("path", "circle")
    .style("fill", subgroupHullFill)
    .style("stroke", subgroupHullFill)
    .style("stroke-width", 25)
    .style("stroke-linejoin", "round")
    .style("opacity", .2)
	.attr("ID","subgroup")
    .attr("d", groupPath);

svg.selectAll("path#subsubgroup")
  .data(subsubgroupNodes)
  .enter().insert("path", "circle")
    .style("fill", subsubgroupHullFill)
    .style("stroke", subsubgroupHullFill)
    .style("stroke-width", 15)
    .style("stroke-linejoin", "round")
    .style("opacity", 0.2)
	.attr("ID","subsubgroup")
    .attr("d", groupPath); */
 /*   
// this redraws the links on top of the convex hulls
 link = svg.selectAll("line.link")
      .data(links, function(d) { return d.target.id; });

  // Exit any old links.
  link.exit().remove();

  // Enter any new links.
  link.enter().insert("line", ".node")
      .attr("class", "link")
      .attr("x1", function(d) { return d.source.x; })
      .attr("y1", function(d) { return d.source.y; })
      .attr("x2", function(d) { return d.target.x; })
      .attr("y2", function(d) { return d.target.y; });

  // Update the nodes…
  node = node.data(nodes, function(d) { return d.id; })
  			.style("fill", color);

  // Exit any old nodes.
  node.exit().remove();

  // Enter any new nodes.
  node.enter().append("circle")
      .attr("class", "node")
      .attr("cx", function(d) { return d.x; })
      .attr("cy", function(d) { return d.y; })
      //.attr("r", function(d) { return d.children ? 12.5 : Math.sqrt(d.size) / 10;})
      .attr("r", function(d) { return Math.sqrt(d.size) / 10;})
     //.style("fill", color)
    // .style("stroke", function(d) { return 'steelblue'})
    // .style("stroke-width", function(d) { if (d.children) { return 2.5 } else { return 0.5 }})
     .style("stroke-dasharray",  function(d) { if (d.children) { return "10,3" } else { return "0,0" }})
      .on("click", click)
      .call(force.drag)
 	 .append("title")
  		.text(function(d) { return d.name });
  
  node.on('mouseover', function (d) {
	    // Highlight the nodes: every node is green except of him
	     node.style('fill', "#DCDCDC").style('stroke', "#DCDCDC");
	     d3.select(this).style('fill', color).style('stroke', "#000");
	    links.forEach(function(link) {
	    	if(link.source.id === d.id) {
	    		
	    		node.filter(function(e) {
	      			 return (e === link.target); //connected nodes
	    	    }).style('fill', color)
	    	      .style('stroke', "#000");
	    	}
	    	else if(link.target.id === d.id) {
				
	    		node.filter(function(e) {
	     			 return (e === link.source); //connected nodes
	   	   		 }).style('fill', color)
	   	     	   .style('stroke', "#000"); 
	    	}
	    	
	    });
	   
	    // Highlight the connections
	    link
	      .style('stroke', function (link_d) { //console.log("link_d source: "+link_d.source.name); 
	     		// console.log("link_d target: "+link_d.target.name);
	      			return link_d.source.id === d.id || link_d.target.id === d.id ? '#ff9896' : 'lightgrey';})
	      			//#b8b8b8 - gray //#ff9896 -  pink
	      .style('stroke-width', function (link_d) { return link_d.source.id === d.id || link_d.target.id === d.id ? 4 : 1;})
	  })
	  .on('mouseout', function (d) {
	    node.style('fill', color).style('stroke', "#000"); 
	    link
	      .style('stroke', 'slategray')//##9ecae1
	      .style('stroke-width', '1')
	  });
	  */

  
  
//	}
link.attr("x1", function(d) { return d.source.x; })
    .attr("y1", function(d) { return d.source.y; })
    .attr("x2", function(d) { return d.target.x; })
    .attr("y2", function(d) { return d.target.y; });

node.attr("cx", function(d) { return d.x; })
    .attr("cy", function(d) { return d.y; });
    
}

// Color leaf nodes orange, and packages white or blue.
function color(d) {
  return d._children ? "#3182bd" : d.children ? "#c6dbef" : "#fd8d3c";
  //fd8d3c - orange //c6dbef - light skyblue //3182bd - dark blue(when collapse)
}

function click(d) {
  if(d._children === undefined || (d.children === undefined && d._children == null)){
	  
 	 showSubNodes(d.id);
 	//nodesChild.responseText = "";//testing
 	
 	//for leaf node
 	 if(nodesChild.responseText != "[]"){
 	 	console.log("nodesChild : "+nodesChild.responseJSON);
 	 	d._children = nodesChild.responseJSON;//testing
 	 	//nodesChild.responseJSON.forEach(element => nodes.push(element));
 	 }
 	 
  }
  
  if (!d3.event.defaultPrevented) {
    if (d.children) {
      //d._children = d.children;
     // d.children = null;
      
      //delete child node while collapsing(click-when already expanded)
     // d._children.forEach(element =>
    	//nodes = nodes.filter(e => e !== element)	  
     // );
      
      //delete all sub children //recursion
      recurseDelete(d);
      
      function recurseDelete(d) {
    	  d._children = d.children;
 	      d.children = null;
    	  if(d._children != null){
    	 	 d._children.forEach(element => recurseDelete(element));
    	 	 
    	 	 d._children.forEach(element =>
    	    	nodes = nodes.filter(e => e !== element)	  
    	     );	
          }
      }
      
      
    } else {
     // d.children = d._children;
     // d._children = null;
      
      //add child nodes 
      //d.children.forEach(element => nodes.push(element));
      
      //add all sub children
      recurseAdd(d);
      
      function recurseAdd(d) {
    	  d.children = d._children;
          d._children = null;
    	  if(d.children != null){
    		  d.children.forEach(element =>
	 			nodes.push(element)	  
	     		);	
    	 	 d.children.forEach(element => recurseAdd(element));
          }
      }
      
    }
    update();
  }
}

</script>
</body>
</html>