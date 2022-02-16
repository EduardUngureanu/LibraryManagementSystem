package com.libmgrsys;

/**
 * The main run class, initializes a thread for the menu, starting from the login screen.
 */
public class Main
{
	public static void main(String[] args)
	{
		Thread thread = new Thread(LoginMenu::new);

		thread.start();
	}
}