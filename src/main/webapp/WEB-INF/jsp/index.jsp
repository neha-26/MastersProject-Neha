<%@ page language="java" contentType="text/html;"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

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
	stroke: #7d7a7a;
	stroke-width: 1.2px;
}

path.hull {
	fill: lightsteelblue;
	fill-opacity: 0.28;
}

.input-file {
	position: relative;
	margin: 60px 60px 0
} /* Remove margin, it is just for stackoverflow viewing */
.input-file .input-group-addon {
	border: 0px;
	padding: 0px;
}

.input-file .input-group-addon .btn {
	border-radius: 0 4px 4px 0
}

.input-file .input-group-addon input {
	cursor: pointer;
	position: absolute;
	width: 32px;
	z-index: 2;
	top: 0;
	right: 0;
	filter: alpha(opacity = 0);
	-ms-filter: "progid:DXImageTransform.Microsoft.Alpha(Opacity=0)";
	opacity: 0;
	background-color: transparent;
	color: transparent;
}

.btn-file {
	position: absolute;
}

p.flip {
	cursor: pointer;
	/* text-align: center; */
}

div.panel, p.flip {
	line-height: 30px;
	margin: auto;
	font-size: 16px;
	padding: 5px;
	/* text-align: center; */
	background: #555;
	border: solid 1px #666;
	color: #ffffff;
	border-radius: 3px;
	user-select: none;
}

div.tooltip {
	position: absolute;
	text-align: center;
	width: 160px;
	height: 51px;
	padding: 2px;
	font: 12px sans-serif;
	background: #FBEC5D;
	border: 0px;
	border-radius: 8px;
	pointer-events: none;
}

.btn {
	background-color: #4CAF50;
}
	body {
	  background-color: #130C0E;
	  /* padding: 20px 0; */
	  margin: 0 auto;
	  width: 100%;
	  height: 600px;
	}
	svg {
		width: 100%;
		height: 100%;
	}
</style>

<script src="https://d3js.org/d3.v3.min.js"></script>
<script type="text/javascript"
	src="http://code.jquery.com/jquery-1.11.0.min.js"></script>

<script
	src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/js/bootstrap.min.js"></script>
<link
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap.min.css"
	rel="stylesheet" />
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.1/jquery.min.js"></script>

</head>
<body>
	<p class="flip">
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Click to show/hide
		input panel &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Time taken
		(milliseconds) :
		<button type="button" id='timeMessage' class="btn">000</button>
	</p>

	<div id="myDiv" class="container panel">
		<form:form name="myForm" enctype="multipart/form-data" action="/index"
			method="post" modelAttribute="myformSub"
			onsubmit="return validateForm()">
			<div class="form-group">

				<div class="form-group">
					<form:input type='file' class="align-items-center" path="file1"></form:input>
				</div>

				<div class='radio-inline'>
					<form:label class="form-check-label" path="approach">
						<form:radiobutton class="form-check-input" path="approach" id='r1'
							value='kcore' checked="checked" />KCore</form:label>
				</div>

				<div class='radio-inline'>
					<form:label class="form-check-label" path="approach">
						<form:radiobutton class="form-check-input" path="approach" id='r2'
							value='edgeweight' />EdgeWeight</form:label>
				</div>

				<div id="kcore" class="desc">
					<div class="form-group row">
						<form:label for="K_start" class="col-sm-2 col-form-label"
							path="K_start">K_start</form:label>
						<div class="col-sm-2">
							<form:input type="text" class="form-control" id="K_start"
								placeholder="K_start" path="K_start" />
						</div>
					</div>
					<div class="form-group row">
						<form:label for="K_increment" class="col-sm-2 col-form-label"
							path="K_increment">K_increment</form:label>
						<div class="col-sm-2">
							<form:input type="text" class="form-control" id="K_increment"
								placeholder="K_increment" path="K_increment" />
						</div>
					</div>
					<div class="form-group row">
						<form:label for="MIN_SCC_SIZE" class="col-sm-2 col-form-label"
							path="MIN_SCC_SIZE">MIN_SCC_SIZE</form:label>
						<div class="col-sm-2">
							<form:input type="text" class="form-control" id="MIN_SCC_SIZE"
								placeholder="MIN_SCC_SIZE" path="MIN_SCC_SIZE" />
						</div>
					</div>
					<div class="form-group row">
						<form:label for="DELETED_NODE_PERCENT"
							class="col-sm-2 col-form-label" path="DELETED_NODE_PERCENT">NODE_PERCENT</form:label>
						<div class="col-sm-2">
							<form:input type="text" class="form-control"
								id="DELETED_NODE_PERCENT" placeholder="NODE_PERCENT"
								path="DELETED_NODE_PERCENT" />
						</div>
					</div>
				</div>
				<div id="edgeweight" class="desc">
					<div class="form-group row">
						<form:label for="K_start1" class="col-sm-2 col-form-label"
							path="K_start1">K_start</form:label>
						<div class="col-sm-2">
							<form:input type="text" class="form-control" id="K_start1"
								placeholder="K_start" path="K_start1" />
						</div>
					</div>
					<div class="form-group row">
						<form:label for="K_increment1" class="col-sm-2 col-form-label"
							path="K_increment1">K_increment</form:label>
						<div class="col-sm-2">
							<form:input type="text" class="form-control" id="K_increment1"
								placeholder="K_increment" path="K_increment1" />
						</div>
					</div>
					<div class="form-group row">
						<form:label for="MIN_SCC_SIZE1" class="col-sm-2 col-form-label"
							path="MIN_SCC_SIZE1">MIN_SCC_SIZE</form:label>
						<div class="col-sm-2">
							<form:input type="text" class="form-control" id="MIN_SCC_SIZE1"
								placeholder="MIN_SCC_SIZE" path="MIN_SCC_SIZE1" />
						</div>
					</div>
					<div class="form-group row">
						<form:label for="DELETED_NODE_PERCENT1"
							class="col-sm-2 col-form-label" path="DELETED_NODE_PERCENT1">NODE_PERCENT</form:label>
						<div class="col-sm-2">
							<form:input type="text" class="form-control"
								id="DELETED_NODE_PERCENT1" placeholder="NODE_PERCENT"
								path="DELETED_NODE_PERCENT1" />
						</div>
					</div>
					<div class="form-group row">
						<form:label for="VERTEX_PERCENTAGE"
							class="col-sm-2 col-form-label" path="VERTEX_PERCENTAGE">VERTEX_PERCENTAGE</form:label>
						<div class="col-sm-2">
							<form:input type="text" class="form-control"
								id="VERTEX_PERCENTAGE" placeholder="VERTEX_PERCENTAGE"
								path="VERTEX_PERCENTAGE" />
						</div>
					</div>
					<div class="form-group row">
						<form:label for="WEIGHT_THRESHOLD" class="col-sm-2 col-form-label"
							path="WEIGHT_THRESHOLD">WEIGHT_THRESHOLD</form:label>
						<div class="col-sm-2">
							<form:input type="text" class="form-control"
								id="WEIGHT_THRESHOLD" placeholder="WEIGHT_THRESHOLD"
								path="WEIGHT_THRESHOLD" />
						</div>
					</div>
				</div>
				<div class="col-auto">
					<button type="submit" class="btn btn-primary mb-2">Submit</button>
				</div>
			</div>
		</form:form>
	</div>
	<!-- </div> -->

	<%--  <h2>Your name is ${nameX}</h2> --%>
	<script type="text/javascript">
	$(document).ready(function(){ 
		//var flag = true;
		 $(".desc").hide();
		 
		 var radioValue = $("input[name='approach']:checked").val();
	      if(radioValue === 'kcore'){
	          //alert("Your are a - " + radioValue);
	    	  $("#kcore").show(); 
	      }else
	    	  $("#edgeweight").show(); 
		 
		 /* if(flag == true){
			 $("#kcore").show(); 
		 } */
	    $("input[name='approach']").click(function() {
	        var test = $(this).val();
	        $(".desc").hide();
	        $("#"+test).show();
	    }); 
	    
	    $(".flip").click(function(){
	    	$(".panel").toggle();
	    });
	    
	    $("button[type='submit']").click(function() {
	    	//flag = false;
	    	$(".panel").toggle();
	    });
	    
	    //validateForm
	    
	});
		
	function validateForm() {
		var x_file1 = document.forms["myForm"]["file1"].value;
		  if (x_file1 == "") {
		    alert("Please select one File! ");
		    return false;
		  }
		  
		  var radioValue = $("input[name='approach']:checked").val();
	      if(radioValue === 'kcore'){
			  var x_K_start = document.forms["myForm"]["K_start"].value;
			  if (x_K_start == "") {
		  		  alert("K_start must be filled out");
		    	return false;
		 	 }
		 	 var x_K_increment = document.forms["myForm"]["K_increment"].value;
		  	if (x_K_increment == "") {
		    	alert("K_increment must be filled out");
		    	return false;
		 	 }
		 	 var x_MIN_SCC_SIZE = document.forms["myForm"]["MIN_SCC_SIZE"].value;
		  	if (x_MIN_SCC_SIZE == "") {
		    	alert("MIN_SCC_SIZE must be filled out");
		    	return false;
		  	}
		  	var x_DELETED_NODE_PERCENT = document.forms["myForm"]["DELETED_NODE_PERCENT"].value;
		  	if (x_DELETED_NODE_PERCENT == "") {
		    	alert("DELETED_NODE_PERCENT must be filled out");
		    	return false;
		  	}
	      }
		}
	
	var group = [];var totalTime;
	//kcore
    	function showSubNodes(nodeId, approach) {
    		 $.ajax({
    		       // url : 'nodes?nodeId=' + nodeId,
    		        url : 'nodes?nodeId=' + nodeId + "&approach=" + approach,
    		        method : 'GET',
    		        async : false,
    		        complete : function(data) {
    		            console.log(data.responseText);
    		           // nodesChild = data;//working
    		           // nodesChild = data.responseJSON.nodesChild;
    		            nodesChild = data;
    		            console.log("Time Takne : "+data.responseJSON.totalTime);
    		            totalTime = data.responseJSON.totalTime;
    		            
    		            document.getElementById('timeMessage').innerHTML = totalTime;
    		        }
    		    });
    		 $.ajax({
 		        url : 'getGroup',
 		        method : 'GET',
 		        async : false,
 		        complete : function(data) {
 		            console.log(data.responseText);
 		           // group = data;
 		            console.log(" group from controller : "+data.responseJSON);
 		            group.push(data.responseJSON);
 		        }
 		    });
    	}
    	
    	//edgeweight
    	function showSubNodesEdge(nodeId, approach) {
    		 $.ajax({
    		        url : 'nodes?nodeId=' + nodeId + "&approach=" + approach,
    		        method : 'GET',
    		        async : false,
    		        complete : function(data) {
    		            console.log("edgeweight : "+ data.responseText);
    		            nodesChild = data;
    		            console.log("Time Takne : "+data.responseJSON.totalTime);
    		            totalTime = data.responseJSON.totalTime;
    		            
    		            document.getElementById('timeMessage').innerHTML = totalTime;
    		        }
    		    });
    		 $.ajax({
 		        url : 'getGroup',
 		        method : 'GET',
 		        async : false,
 		        complete : function(data) {
 		            console.log("edgeweight : "+data.responseText);
 		           // group = data;
 		            console.log(" edgeweight-group from controller : "+data.responseJSON);
 		            group.push(data.responseJSON);
 		        }
 		    });
    	}
</script>

	<script type="text/javascript">


var width = 1200;
var height = 500;
var radius = 6;
var root;
//var width = window.innerWidth;
//var height = window.innerHeight - 100;

//root = "[${rootJ}]";
//root = JSON.parse('${rootJ}');
//console.log("root from controller :- "+root);
 var nodes = [];//,links =[];
  
  nodes = JSON.parse('${nodes}');
  console.log("nodes from controller :- "+nodes);
  totalTime = JSON.parse('${totalTime}');
  document.getElementById('timeMessage').innerHTML = totalTime;
  console.log("totalTime from controller :- "+totalTime);
  
  var curve = d3.svg.line()
  .interpolate("cardinal-closed")
  .tension(.85);
  
  function drawCluster(d) {
	  return curve(d.path); // 0.8
	}

var force = d3.layout.force()
    .gravity(0.01)//0.1
    //.charge(function(d) { return d._children ? -d.size / 100 : -30; })//old
    .charge(function(d) { return d._children ? -d.size / 100 : -300; }) //.charge(function(d) { return d._children ? -d.size / 100 : -600; })
	.friction(0.3)//0.5
    .linkDistance(function(d) { return d.target._children ? 100 : 100; })//collapse : expand
    .size([width, height])
    .on("tick", tick);

var svg = d3.select("body").append("svg")
    .attr("width", width)
    .attr("height", height);
    
hullg = svg.append("g");

//Define the div for the tooltip
var div = d3.select("body").append("div")	
    .attr("class", "tooltip")				
    .style("opacity", 0);

var link = svg.selectAll(".link"),
    node = svg.selectAll(".node");
    
var fill = d3.scale.category20();

var groupHullColor = "#e7cb94";
var groupHullFill = function(d, i) { return groupHullColor; };

function getDepth(n) { return n.depth; }

update();

 var links;
 
function update() {
	console.log(" ^^group : "+ group)
  // nodes = flatten(root),
   links = d3.layout.tree().links(nodes);

  // Restart the force layout.
  force
      .nodes(nodes)
      .links(links)
      .start();
  
  hullg.selectAll("path.hull").remove();
  hull = hullg.selectAll("path.hull")
      .data(convexHulls())
    .enter().append("path")
      .attr("class", "hull")
      .attr("d", drawCluster)
      .style("fill",  function(d) { return fill(d.group); }); 
     // .style("fill", groupHullFill); 
    
  
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
     .style("fill", color)
    .style("stroke-dasharray",  function(d) { if (d.children) { return "10,3" } else { return "0,0" }})
      .on("click", click)
    .call(force.drag);
  
  //new code
   node.on("mouseover", function(d) {		
            div.transition()		
                .duration(200)		
                .style("opacity", .9);		
            div	.html("No. of nodes: "+d.totalNodes + "<br/>"  + "No. of edges: "+ d.totalEdges
            		+ "<br/>"  + "Density: "+ d.density)	
                .style("left", (d3.event.pageX) + "px")		
                .style("top", (d3.event.pageY - 28) + "px");	
            })					
        .on("mouseout", function(d) {		
            div.transition()		
                .duration(500)		
                .style("opacity", 0);	
        });
  
  /* node.on('mouseover', function (d) {
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
  
//new code
  node.transition()
     .attr("r", function(d) { return d.children ? 12.5 : Math.sqrt(d.size) / 10; })
	 .style("stroke-dasharray",  function(d) { if (d.children) { return "10,3" } else { return "0,0" }});// make the stroke dashed
	
  
}

function tick() {
	
	/* node.attr("transform", function (d) {
        d.x = Math.max(radius, Math.min(width - radius, d.x));
        d.y = Math.max(radius, Math.min(height - radius, d.y));
        return "translate("+d.x+","+d.y+")";
  }); */  
  node.attr("cx", function(d) { return d.x; })
  .attr("cy", function(d) { return d.y; });
  
   //nodes = flatten(root),
   links = d3.layout.tree().links(nodes);
   
   if (!hull.empty()) {
	      hull.data(convexHulls())
	          .attr("d", drawCluster);
	    } 
  	
link.attr("x1", function(d) { return d.source.x; })
    .attr("y1", function(d) { return d.source.y; })
    .attr("x2", function(d) { return d.target.x; })
    .attr("y2", function(d) { return d.target.y; });

//node.attr("transform", function (d) { return "translate(" + d.x + "," + d.y + ")"});
//node.attr("cx", function(d) { return d.x = Math.max(d.width, Math.min(width - d.width, d.x)); })
//.attr("cy", function(d) { return d.y = Math.max(d.height, Math.min(height - d.height, d.y)); });

//node.attr("cx", function(d) { return d.x = Math.max(radius, Math.min(width - radius, d.x)); })
//.attr("cy", function(d) { return d.y = Math.max(radius, Math.min(height - radius, d.y)); });
    
}

// Color leaf nodes orange, and packages white or blue.
function color(d) {
  return d._children ? "#3182bd" : d.children ? "#6dbcf7" : "#fd8d3c";
  //fd8d3c - orange //#c6dbef - light skyblue //#3182bd - dark blue(when collapse)
}

function click(d) {
	if(d.leaf){
		alert("It is a Leaf Node.");
		return;
	}

  if(d._children === undefined || (d.children === undefined && d._children == null)){
	  
	  var radioValue = $("input[name='approach']:checked").val();
      if(radioValue === 'kcore'){
          //alert("Your are a - " + radioValue);
          showSubNodes(d.id,radioValue);
      }else
    	  showSubNodesEdge(d.id,radioValue);
 	
 	//for leaf node
 	// if(nodesChild.responseText != "[]"){//working
 	// if(nodesChild.responseText != "{}"){
 		console.log("#### "+ nodesChild.responseJSON.nodesChild[0].length);
 	 if(nodesChild.responseJSON.nodesChild[0].length != 0){
 	 	//console.log("nodesChild : "+nodesChild.responseJSON);
 	 	//d._children = nodesChild.responseJSON;//working
 	 	console.log("d._children : "+nodesChild.responseJSON.nodesChild);
 	 	d._children = nodesChild.responseJSON.nodesChild[0];
 		console.log("d._children**** : "+nodesChild.responseJSON);
 	 	
 	 } 
 	 
  }
  
  if (!d3.event.defaultPrevented) {
    if (d.children) {
      d._children = d.children;
      d.children = null;
      
      //delete child node while collapsing(click-when already expanded)
      d._children.forEach(element =>
    	nodes = nodes.filter(e => e !== element)	  
      );
      
      //delete all sub children //recursion
     /* recurseDelete(d);
      
      function recurseDelete(d) {
    	  d._children = d.children;
 	      d.children = null;
    	  if(d._children != null){
    	 	 d._children.forEach(element => recurseDelete(element));
    	 	 
    	 	 d._children.forEach(element =>
    	    	nodes = nodes.filter(e => e !== element)	  
    	     );	
          }
      }*/
      
      
    } else if( d._children){
      d.children = d._children;
      d._children = null;
      
      //add child nodes 
      d.children.forEach(element => nodes.push(element));
      
      //add all sub children
      /*recurseAdd(d);
      
      function recurseAdd(d) {
    	  d.children = d._children;
          d._children = null;
    	  if(d.children != null){
    		  d.children.forEach(element =>
	 			nodes.push(element)	  
	     		);	
    	 	 d.children.forEach(element => recurseAdd(element));
          }
      }*/
      
    }else{
    	d.leaf = true;
    	alert("It is a Leaf Node.");
    }
    	
    update();
  }
}

function convexHulls() {
	
	  var hulls = {};
	  var offset = 20;//15
	  for(var k=0; k<nodes.length; ++k){
		  var n = nodes[k];
		  
		  for (var i=0; i<group.length; ++i) {
			  
			  var g = group[i];
			  for(var j=0; j<g.length; ++j){
				  if(g[j] === n.id){
					 n.group = i;
				      var l = hulls[i] || (hulls[i] = []);
				    
				    l.push([n.x-offset, n.y-offset]);
				    l.push([n.x-offset, n.y+offset]);
				    l.push([n.x+offset, n.y-offset]);
				    l.push([n.x+offset, n.y+offset]);
					  break;
				  }
			  }
		  }
	  }
	  
	  // create convex hulls
	  var hullset = [];
	  for (i in hulls) {
	    hullset.push({group: i, path: d3.geom.hull(hulls[i])});
	  }

	  return hullset;
}

</script>
</body>
</html>