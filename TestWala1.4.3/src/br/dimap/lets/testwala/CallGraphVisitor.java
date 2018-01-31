package br.dimap.lets.testwala;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import com.ibm.wala.analysis.exceptionanalysis.ExceptionAnalysis;
import com.ibm.wala.analysis.exceptionanalysis.IntraproceduralExceptionAnalysis;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ssa.ISSABasicBlock;
import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.ssa.SSAThrowInstruction;
import com.ibm.wala.types.TypeReference;
import com.ibm.wala.util.collections.Pair;

public class CallGraphVisitor extends BaseCallGraphVisitor
{
	private int levels = 0;
	private AnalysisScope analysisScope;
	private ExceptionAnalysis exceptionAnalysis;
	private Map<CGNode, ExceptionalInterface> exceptionalInterfaces;

	public CallGraphVisitor(CallGraph callGraph, AnalysisScope analysisScope, ExceptionAnalysis exceptionAnalysis)
	{
		super(callGraph);
		this.analysisScope = analysisScope;
		this.exceptionalInterfaces = new HashMap<>();
		this.exceptionAnalysis = exceptionAnalysis;
		this.exceptionAnalysis.solve();
	}

	@Override
	public void preVisit(CGNode node)
	{
		//for ( int i = 0 ; i < levels ; i++ )
		//	System.out.print("  ");	
		//System.out.println(node.getMethod().toString());

		//levels++;

		/*
		exceptionAnalysis.getCGNodeExceptions(node)
		exceptionAnalysis.hasUncaughtExceptions(node, block)
		exceptionAnalysis.catchesException(node, throwBlock, catchBlock)
		exceptionAnalysis.getExceptions(node, instruction)
		IntraproceduralExceptionAnalysis.getThrowingInstruction(throwBlock)
		catchBlock.getCaughtExceptionTypes()

		Catches - A exce��o � capturada e o fluxo � encerrado
		---- Para cada instru��o, para cada exce��o sinalizada, verifica se a exce��o � capturada. Se sim, verifica se � relan�ada. Se n�o for, entra no CATCHES.
		Throws - Um novo fluxo surge a partir daquele n�
		---- Para cada throw, verifique se h� um handler para ele
		Rethrows - A exce��o � capturada e relan�ada
		Wrappings - A exce��o � capturada, encapsulada em uma nova exce��o (podendo ser do mesmo tipo) e relan�ada
		Propagates - A exce��o n�o � capturada. Ela simplesmente � propagada.
		 */


		// CATCHES
		List<TypeReference> caughtExceptions = new ArrayList<>();
		List<TypeReference> propagatedExceptions = new ArrayList<>();
		
		Iterator<ISSABasicBlock> blockIterator = node.getIR().getBlocks();
		int blockC = 1;
		while ( blockIterator.hasNext() )
		{
			ISSABasicBlock block = blockIterator.next();

			Iterator<SSAInstruction> instructionIterator = block.iterator();
			int instructionC = 1;
			while ( instructionIterator.hasNext() )
			{
				SSAInstruction instruction = instructionIterator.next();
				
				// Teste: � poss�vel que uma instru�ao que seja "throw" tenha outras exce��es al�m da sinalizada? 
				System.out.println(instruction.toString());			
				
				for (TypeReference type : exceptionAnalysis.getExceptions(node, instruction))
				{
					System.out.println("\t" + type.toString());
					
					Pair<ISSABasicBlock,TypeReference> blockAndType = this.catchBlockAndTypeOfType(type, block, node);
					
					if (blockAndType == null) // Exce��o propagada
					{
						propagatedExceptions.add(type);
					}
					else // Exce��o capturada. Avaliar o bloco catch: a exce��o pode ser relan�ada ou encapsulada
					{
						// verificar se relan�a ou encapsula
					}
					//System.out.println(blockC + ":" + instructionC + " - " + type.toString());
				}
				
				if ( instruction instanceof SSAThrowInstruction )
				{
					System.out.println("� throw");
				}
				else
				{
					System.out.println("N�o � throw");
				}
				System.out.println();

				instructionC++;
			}

			blockC++;
		}
		
		//System.out.println(node.getMethod().toString());
		//for ( TypeReference type : caughtExceptions )
		//{
		//	System.out.println("\t" + type.toString());
		//}
		//System.out.println();
		/*
			for ( TypeReference type : exceptionAnalysis.getCGNodeExceptions(node) )
			{
				System.out.println("\t" + type);
			}
		 */
		//System.out.println();
	}

	@Override
	public void postVisit(CGNode node)
	{
		//levels--;


	}

	private boolean isApplicationNode (CGNode node)
	{
		return analysisScope.isApplicationLoader(node.getMethod().getDeclaringClass().getClassLoader());
	}

	private Pair<ISSABasicBlock,TypeReference> catchBlockAndTypeOfType(TypeReference thrownException, ISSABasicBlock signalerBlock, CGNode node)
	{
		boolean isCaught = false;
		for (ISSABasicBlock catchBlock : node.getIR().getControlFlowGraph().getExceptionalSuccessors(signalerBlock))
		{
			
			Iterator<TypeReference> caughtExceptions = catchBlock.getCaughtExceptionTypes();
			while (caughtExceptions.hasNext() && !isCaught)
			{
				TypeReference caughtException = caughtExceptions.next();
				if (this.getClassHierarchy().isAssignableFrom(this.getClassHierarchy().lookupClass(caughtException), this.getClassHierarchy().lookupClass(thrownException)))
				{
					return Pair.make(catchBlock, caughtException);
				}				
			}
		}

		return null;
	}

}
