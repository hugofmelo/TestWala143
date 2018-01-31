package br.dimap.lets.testwala;

import java.util.Stack;

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.CallGraph;

public class CallGraphVisitor extends BaseCallGraphVisitor
{
	private int levels = 0;
	
	
	
	public CallGraphVisitor(CallGraph callGraph)
	{
		super(callGraph);
	}

	@Override
	public void preVisit(CGNode node)
	{
		for ( int i = 0 ; i < levels ; i++ )
			System.out.print("  ");	
		System.out.println(node.getMethod().toString());
		
		levels++;
	}

	@Override
	public void postVisit(CGNode node)
	{
		levels--;
	}

}
