package br.dimap.lets.testwala.example;

import java.io.IOException;

public class A {
	public void m1 ()
	{
		m2();
		/*
		try
		{
			m2();
		}
		catch (Exception e)
		{
			try
			{
				throw new Exception(e);
				//throw new IOException(e);
			}
			catch (Throwable e1)
			{
				
			}
		}
		*/
		//throw new IllegalStateException();
	}
	
	public void m2 ()
	{
		throw createException(0);
	}

	private RuntimeException createException(int i)
	{
		if (i == 0)
		{
			throw new IllegalStateException();
		}
		else
		{
			return new RuntimeException();
		}
	}
}


