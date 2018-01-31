package br.dimap.lets.testwala;

import java.util.ArrayList;
import java.util.List;

import com.ibm.wala.types.TypeReference;
import com.ibm.wala.util.collections.Pair;

public class ExceptionalInterface
{
	/*
	Catches - A exceção é capturada e o fluxo é encerrado
	Para cada instrução, para cada exceção sinalizada, verifica se a exceção é capturada. Se sim, verifica se é relançada. Se não for, entra no CATCHES.
	Throws - Um novo fluxo surge a partir daquele nó
	Rethrows - A exceção é capturada e relançada
	Wrappings - A exceção é capturada, encapsulada em uma nova exceção (podendo ser do mesmo tipo) e relançada
	Propagates - A exceção não é capturada. Ela simplesmente é propagada.
	*/
	private List<TypeReference> propagated;
	private List<Pair<TypeReference, TypeReference>> caught;
	private List<TypeReference> thrown;
	private List<TypeReference> rethrown;
	private List<Pair<TypeReference, TypeReference>> wrapped;
	
	public ExceptionalInterface ()
	{
		this.propagated = new ArrayList<>();
		this.caught = new ArrayList<>();
		this.thrown = new ArrayList<>();
		this.rethrown = new ArrayList<>();
		this.wrapped = new ArrayList<>();
	}

	public List<TypeReference> getPropagated() {
		return propagated;
	}

	public void setPropagated(List<TypeReference> propagated) {
		this.propagated = propagated;
	}

	public List<Pair<TypeReference, TypeReference>> getCaught() {
		return caught;
	}

	public void setCaught(List<Pair<TypeReference, TypeReference>> caught) {
		this.caught = caught;
	}

	public List<TypeReference> getThrown() {
		return thrown;
	}

	public void setThrown(List<TypeReference> thrown) {
		this.thrown = thrown;
	}

	public List<TypeReference> getRethrown() {
		return rethrown;
	}

	public void setRethrown(List<TypeReference> rethrown) {
		this.rethrown = rethrown;
	}

	public List<Pair<TypeReference, TypeReference>> getWrapped() {
		return wrapped;
	}

	public void setWrapped(List<Pair<TypeReference, TypeReference>> wrapped) {
		this.wrapped = wrapped;
	}
	
	
}
