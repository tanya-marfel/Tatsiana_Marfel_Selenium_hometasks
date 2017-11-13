package com.epam.ftm.selenium.part3.chrome;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;
import org.testng.AssertJUnit;

public class LoginPage extends Page {

	@FindBy(xpath = "//input[@id = 'mailbox:login']")
	private WebElement usernameField;

	@FindBy(xpath = "//input[@id = 'mailbox:password']")
	private WebElement passwdField;

	@FindBy(xpath = "//input[@type='submit']")
	private WebElement submit;

	@FindBy(xpath = "//a[@href='/messages/inbox/']")
	private static WebElement InboxFolder;
	
	public LoginPage(WebDriver driver) {
		super(driver);
	}

	public InboxPage loginToMailRu(String username, String passwd, int index) {
		// send credentials via enterCredentials method
		Select dropdown = new Select(driver.findElement(By.id("mailbox:domain")));
		usernameField.sendKeys(username);

		passwdField.sendKeys(passwd);

		dropdown.selectByIndex(index);

		submit.click();
		return new InboxPage(driver);
	}
	
	public static boolean isLoginSuccessful() {
		return InboxFolder != null;
	}

}
