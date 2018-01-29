package br.dimap.lets.testwala.example;

public class C
{
	public void m1()
	{
		m2();
	}

	public void m2()
	{
		m3(0);
	}
	
	private void m3(int i) throws RuntimeException {
		
		if ( i > 0 )
			throw new IllegalArgumentException();
		else
			throw new TestError();
		
	}
}

class TestError extends Error
{
	private static final long serialVersionUID = 1L;
}
