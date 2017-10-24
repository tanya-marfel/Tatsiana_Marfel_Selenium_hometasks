package com.epam.ftm.demo1;

import org.testng.Assert;
import org.testng.annotations.Test;

public class HelloWorldTestNG {
	
	Object object = new Object();
	String test = "I'm Tanya";
	
	
	@Test
	public void test1Failed() {
		System.out.println(test);
		Assert.assertTrue(test.contains("Ihar"), "No matching text is found. Please, try again.");
	}

	@Test
	public void test2() {
		System.out.println("I'am object: " + object.toString());
	}
}
