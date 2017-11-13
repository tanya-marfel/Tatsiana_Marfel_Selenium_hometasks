package com.epam.ftm.selenium.part3.chrome;

import java.io.IOException;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class DraftLetterPage extends Page {

	@FindBy(css = "div[data-name=\"saveDraft\"]")
	private WebElement saveDraft;

	@FindBy(xpath = "//div[@data-mnemo=\"saveStatus\"]")
	private WebElement status;

	@FindBy(id = "tinymce")
	private static WebElement letterBody;

	@FindBy(css = "div[data-name='send']")
	private static WebElement sendButton;
	
	@FindBy(id = "compose_to")
	private static WebElement addressees;
	
	private static final String ADDRESSEES_VALUE = "value";
	private static final String TO_SCRIPT = "document.getElementsByName('To')[0].setAttribute('type', 'text');";
	private static final String SUBJECT_SCRIPT = "document.getElementsByName('Subject')[0].setAttribute('type', 'text');";
	private static final String SUBJECT_VALUE = "data-subject";
	private static final String BODY_FRAME = "//iframe[@tabindex='10']";
	
	public DraftLetterPage(WebDriver driver) {
		super(driver);
	}

	public WebElement saveDraft() {
		saveDraft.click();
		showStatus();
		return status;
	}

	public void showStatus() {
		WebElement saveStatus = new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOf(status));
	}

	public static void sendLetter() {
		letterBody.click();
		letterBody.sendKeys(Keys.ENTER);
		driver.switchTo().defaultContent();
		sendButton.click();
	}
	
	public static boolean isAddresseeCorrect(String addressee) {
		executeScript(TO_SCRIPT);
		String list = addressees.getAttribute(ADDRESSEES_VALUE);
		return list.contains(addressee);
	}
	
	public static boolean isSubjectCorrect(String subject){
		final String currentUrl = "//a[@href='" + driver.getCurrentUrl() + "']";
		String subj = driver.findElement(By.xpath(currentUrl)).getAttribute(SUBJECT_VALUE);
		executeScript(SUBJECT_SCRIPT);
		return subj.equals(subject);		
	}
	
	public static boolean isLetterBodyCorrect(String filename) throws IOException{
		driver.switchTo().frame(driver.findElement(By.xpath(BODY_FRAME)));
		String bodyContent = letterBody.getText();
		return bodyContent.equals(new String(readFile(filename)));
	}

}
