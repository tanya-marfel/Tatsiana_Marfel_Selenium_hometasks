package com.epam.ftm.selenium.part3.chrome;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

public class SentFolderPage extends Page {

	@FindBy(xpath = "//a[@href='/messages/sent/']")
	private static WebElement sentFolder;

	public SentFolderPage(WebDriver driver) {
		super(driver);
	}

	public static void openSendFolder() {
		fluentlyWait();
		sentFolder.click();

	}

	public static boolean isLetterSent(String subject) {
		String PRESENCE = "//div[contains(text()," + "'" + subject + "'" + ")]";
		return driver.findElement(By.xpath(PRESENCE))!=null;
	}

}
