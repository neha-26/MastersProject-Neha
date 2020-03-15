package org.myproj.masterProj;

import java.util.ArrayList;

public class Parent {
	
	int i;
	public int getI() {
		return i;
	}
	public void setI(int i) {
		this.i = i;
	}
	String name;
	int depth;
	int size;
	//ArrayList<Parent> children = new ArrayList<Parent>();
	ArrayList<Parent> children;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getDepth() {
		return depth;
	}
	public void setDepth(int depth) {
		this.depth = depth;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public ArrayList<Parent> getChildren() {
		return children;
	}
	public void setChildren(ArrayList<Parent> children) {
		this.children = children;
	}
	@Override
	public String toString() {
		return "Parent [name=" + name + ", depth=" + depth + ", size=" + size + ", children=" + children + "]";
	}
	
	

}
