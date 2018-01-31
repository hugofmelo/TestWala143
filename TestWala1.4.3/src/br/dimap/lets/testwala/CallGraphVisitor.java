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

		Catches - A exceção é capturada e o fluxo é encerrado
		---- Para cada instrução, para cada exceção sinalizada, verifica se a exceção é capturada. Se sim, verifica se é relançada. Se não for, entra no CATCHES.
		Throws - Um novo fluxo surge a partir daquele nó
		---- Para cada throw, verifique se há um handler para ele
		Rethrows - A exceção é capturada e relançada
		Wrappings - A exceção é capturada, encapsulada em uma nova exceção (podendo ser do mesmo tipo) e relançada
		Propagates - A exceção não é capturada. Ela simplesmente é propagada.
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
				
				// Teste: é possível que uma instruçao que seja "throw" tenha outras exceções além da sinalizada? 
				System.out.println(instruction.toString());			
				
				for (TypeReference type : exceptionAnalysis.getExceptions(node, instruction))
				{
					System.out.println("\t" + type.toString());
					
					Pair<ISSABasicBlock,TypeReference> blockAndType = this.catchBlockAndTypeOfType(type, block, node);
					
					if (blockAndType == null) // Exceção propagada
					{
						propagatedExceptions.add(type);
					}
					else // Exceção capturada. Avaliar o bloco catch: a exceção pode ser relançada ou encapsulada
					{
						// verificar se relança ou encapsula
					}
					//System.out.println(blockC + ":" + instructionC + " - " + type.toString());
				}
				
				if ( instruction instanceof SSAThrowInstruction )
				{
					System.out.println("É throw");
				}
				else
				{
					System.out.println("Não é throw");
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
