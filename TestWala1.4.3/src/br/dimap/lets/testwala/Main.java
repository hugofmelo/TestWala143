package br.dimap.lets.testwala;

import java.io.File;
import java.io.IOException;

import com.ibm.wala.analysis.exceptionanalysis.ExceptionAnalysis;
import com.ibm.wala.ipa.callgraph.AnalysisCache;
import com.ibm.wala.ipa.callgraph.AnalysisCacheImpl;
import com.ibm.wala.ipa.callgraph.AnalysisOptions;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ipa.callgraph.CallGraphBuilderCancelException;
import com.ibm.wala.ipa.callgraph.Entrypoint;
import com.ibm.wala.ipa.callgraph.impl.Util;
import com.ibm.wala.ipa.callgraph.propagation.SSAPropagationCallGraphBuilder;
import com.ibm.wala.ipa.callgraph.propagation.cfa.nCFABuilder;
import com.ibm.wala.ipa.cha.ClassHierarchy;
import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.ipa.cha.ClassHierarchyFactory;
import com.ibm.wala.util.config.AnalysisScopeReader;

public class Main
{
	private static final String applicationRootPath = "./bin/br/dimap/lets/testwala/example/";

	public static void main (String args[]) throws ClassHierarchyException, IllegalArgumentException, CallGraphBuilderCancelException, IOException
	{
		// Setando o AnalysisScope
		AnalysisScope analysisScope = AnalysisScopeReader.makeJavaBinaryAnalysisScope(applicationRootPath, new File("Java60RegressionExclusions.txt"));

		// Criando o ClassHierarchy
		ClassHierarchy classHierarchy = ClassHierarchyFactory.make(analysisScope);
		

		// Criando os EntryPoints
		Iterable<Entrypoint> entrypoints = Util.makeMainEntrypoints(analysisScope, classHierarchy);
		
		// Criando AnalysisOptions
		AnalysisOptions options = new AnalysisOptions(analysisScope, entrypoints);
		options.setReflectionOptions(AnalysisOptions.ReflectionOptions.FULL);

		Util.addDefaultSelectors(options, classHierarchy);
		Util.addDefaultBypassLogic(options, analysisScope, Util.class.getClassLoader(), classHierarchy);

		// builder com sensibilidade ao contexto na sinalização de exceções.
		AnalysisCache cache = new AnalysisCacheImpl();
		SSAPropagationCallGraphBuilder builder = new nCFABuilder(1,classHierarchy,options,cache,null,null);

		CallGraph callGraph = builder.makeCallGraph(options,null);
		
		//System.err.println(CallGraphStats.getStats(callGraph));

	    //DFS.iterateFinishTime(callGraph);
	    //DFSDiscoverTimeIterator<CGNode> iterator = DFS.iterateDiscoverTime(callGraph);
		
		callGraph.getFakeRootNode();
		
		CGNode node = callGraph.getFakeRootNode();
		node = callGraph.getSuccNodes(node).next(); 
		node = callGraph.getSuccNodes(node).next();
		System.out.println(node.getMethod().getSignature());
		
		ExceptionAnalysis exceptionAnalysis = new ExceptionAnalysis(callGraph, null, classHierarchy);
		
		/*
		while ( iterator.hasNext() )
		{
			CGNode node = iterator.next();
			
			System.out.println(node.getMethod().getSignature());
		}
		*/
	}
}
