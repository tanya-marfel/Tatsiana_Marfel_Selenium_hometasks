package com.epam.ftm.selenium.part3.chrome;

import java.io.IOException;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class NewLetterPage extends Page {
	@FindBy(xpath = "//textarea[@tabindex='4']")
	private WebElement addresseeField;

	@FindBy(xpath = "//input[@name='Subject']")
	private WebElement subjectField;

	@FindBy(id = "tinymce")
	private WebElement letterBody;

	@FindBy(xpath = "//iframe[@tabindex='10']")
	private WebElement bodyFrame;

	public NewLetterPage(WebDriver driver) {
		super(driver);
	}

	public DraftLetterPage addContent(String addressee, String subject, String fileName) throws IOException {
		addresseeField.click();
		addresseeField.sendKeys(addressee);
		addresseeField.sendKeys(Keys.RETURN);

		subjectField.click();
		subjectField.sendKeys(subject);

		driver.switchTo().frame(bodyFrame);
		letterBody.sendKeys(readFile(fileName));
		driver.switchTo().defaultContent();

		return new DraftLetterPage(driver);
	}

}
