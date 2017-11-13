package com.epam.ftm.selenium.part3.chrome;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class DraftsFolderPage extends Page {

	@FindBy(xpath = "//a[@href='/messages/drafts/']")
	private static WebElement draftsFolder;

	public DraftsFolderPage(WebDriver driver) {
		super(driver);
	}

	public static void openDraftsFolder() {
		fluentlyWait();
		draftsFolder.click();
		// return this;
	}

	public static DraftLetterPage openLetter(String subject) {
		String letter = "//div[contains(text()," + "'" + subject + "'" + ")]";
		WebElement letterDraft = driver.findElement(By.xpath(letter));
		letterDraft.click();
		return new DraftLetterPage(driver);
	}

	public static boolean isLetterCached(String subject) {
		fluentlyWait();
		String findLetter = "//div[contains(@data-cache-key, *)]//a[contains(@data-subject," + "'" + subject + "')]";
		WebElement letter = driver
				.findElement(By.xpath(findLetter));
		try {

			return letter != null;
		} catch (org.openqa.selenium.StaleElementReferenceException ex) {
			return letter != null;
		}
	}
	
	public static boolean isLetterPresent(String subject) {
		String letter = "//div[contains(text()," + "'" + subject + "'" + ")]";
		return !driver.findElements(By.xpath(letter)).isEmpty();
	}
}
