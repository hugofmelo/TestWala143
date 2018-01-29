package br.dimap.lets.testwala.example;

public class Example {

	public static void main(String[] args)
	{
		A t;
		C u;
		
		t = new A();
		
		try
		{
			t.m1();
		}
		catch (RuntimeException e)
		{
			e.printStackTrace();
		}
		
		t = new B();
		t.m1();
		
		u = new C();
		
		try
		{
			u.m1();
		}
		catch (IllegalArgumentException e)
		{
			e.printStackTrace();
		}
	}

}
