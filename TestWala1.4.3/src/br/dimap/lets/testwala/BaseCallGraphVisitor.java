package br.dimap.lets.testwala;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ipa.cha.IClassHierarchy;

public abstract class BaseCallGraphVisitor
{
	private CallGraph callGraph;
	private Map<CGNode, TraverseState> nodesState;
	
	public BaseCallGraphVisitor (CallGraph callGraph)
	{
		this.callGraph = callGraph;
	}
	
	public void accept (CGNode node)
	{
		this.nodesState = new HashMap<>();
		
		this.nodesState.put(node, TraverseState.DISCOVERED);
		
		this.search(node);
	}
	
	private void search(CGNode parent)
	{
		this.preVisit(parent);
		this.nodesState.put(parent, TraverseState.PROCESSING);
		
		Iterator<CGNode> successorsIterator = callGraph.getSuccNodes(parent); 
		while ( successorsIterator.hasNext() )
		{
			CGNode child = successorsIterator.next();
			
			if ( this.nodesState.get(child) == null )
			{
				this.nodesState.put(child, TraverseState.DISCOVERED);
				
				this.search(child);
			}
		}
			
		this.postVisit(parent);
		this.nodesState.put(parent, TraverseState.DEAD);
	}

	public TraverseState getState (CGNode node)
	{
		TraverseState state = this.nodesState.get(node); 
		
		if (state == null)
		{
			return TraverseState.UNKNOWN;
		}
		else
		{
			return state;
		}
	}
	
	protected IClassHierarchy getClassHierarchy ()
	{
		return this.callGraph.getClassHierarchy();
	}
	
	public abstract void preVisit(CGNode node);
	public abstract void postVisit(CGNode node);
}
