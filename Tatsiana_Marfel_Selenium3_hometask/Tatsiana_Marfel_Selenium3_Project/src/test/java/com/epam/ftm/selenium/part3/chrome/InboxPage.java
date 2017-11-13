package com.epam.ftm.selenium.part3.chrome;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class InboxPage extends Page {
	@FindBy(xpath = "//a[@data-name=\"compose\"]")
	private WebElement composeLetter;
	
	@FindBy(id = "PH_logoutLink")
	private static WebElement signOutLink;
	
	
	
	public InboxPage(WebDriver driver) {
		super(driver);
	}

	public NewLetterPage composeLetter() {
		composeLetter.click();
		return new NewLetterPage(driver);
	}
	
	public static LoginPage signOut() {
		signOutLink.click();
		return new LoginPage(driver);
	}
}
