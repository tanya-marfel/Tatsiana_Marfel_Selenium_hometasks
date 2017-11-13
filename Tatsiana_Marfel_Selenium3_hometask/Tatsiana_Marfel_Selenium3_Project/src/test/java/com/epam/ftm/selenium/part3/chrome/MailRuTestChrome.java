package com.epam.ftm.selenium.part3.chrome;

import org.testng.annotations.Test;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.AssertJUnit;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.AssertJUnit;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class MailRuTestChrome {

	private static final String URL = "https://mail.ru";
	private static final String USERNAME = "tanulina";
	private static final String PASSWD = "6185509tanulina";
	private static String ADDRESSEE = "tatsianamarfel@gmail.com";

	private static String SUBJECT = "Test letter";

	private static String FILENAME = "src/main/resources/LoremIpsum.txt";

	private WebDriver driver;

	@BeforeClass(description = "Launch browser")
	public void launchBrowser() {
		// launching the browser for the test before the first method is
		// executed
		System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver");
		driver = new ChromeDriver();
	}

	@BeforeClass(dependsOnMethods = "launchBrowser", description = "Add implicit wait and maximize window")
	public void addImplicitWait() {
		// setting standard timeout
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		// driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
		// navigating to test url
		driver.get(URL);
		driver.manage().window().maximize();
	}

	@Test(description = "Sign in to MailRu account", priority = 0)
	public void loginToMailRu() {

		// send credentials via enterCredentials method
		new LoginPage(driver).loginToMailRu(USERNAME, PASSWD, 1);
		AssertJUnit.assertTrue("Please, make sure the entered credentials are correct.", LoginPage.isLoginSuccessful());
		System.out.println("Login passed successfully.");
	}

	@Test(description = "Compose new letter", priority = 1)
	public void createLetter() throws IOException {
		// clicking a button for creating a new letter
		new InboxPage(driver).composeLetter().addContent(ADDRESSEE, SUBJECT, FILENAME);
		System.out.println("Content is added to the letter");
		// invoking a method to fill in addressee, subject and body fields for
		// the new
		// letter
		// text for a body text is read from a file

	}

	@Test(description = "Save the mail as a draft.", priority = 2, dependsOnMethods = "createLetter")
	private void saveDraft() {
		// saving the mail created in the previous test by clicking Save button
		new DraftLetterPage(driver).saveDraft();
		System.out.println("Composed letter is saved as a draft");

	}

	@Test(description = "Verify, that the mail presents in ‘Drafts’ folder.", priority = 3, dependsOnMethods = "saveDraft")
	private void draftExist() {
		new DraftsFolderPage(driver);
		// checking that the draft exist in the Drafts folder and navigating to
		// this
		// letter
		DraftsFolderPage.openDraftsFolder();
		AssertJUnit.assertTrue(DraftsFolderPage.isLetterPresent(SUBJECT));
		System.out.println("Composed letter is int he drafts folder");
		DraftsFolderPage.openLetter(SUBJECT);
	}

	@Test(description = "Verify the draft content, send the letter and check it is no longer in the Drafts folder", priority = 4)
	private void verifyContent() throws IOException, InterruptedException {
		// verifying the letter content by comparing initial and current values
		// since subject and addressee text is hidden JavaScriptExecutor is
		// applied
		// for addressee only contains is used because the mail address is
		// replaced by
		// the name,
		// thus tatsianamarfel@gmail.com becomes tatsianamarfel

		AssertJUnit.assertTrue(DraftLetterPage.isAddresseeCorrect(ADDRESSEE));
		System.out.println("Addressee is correct");

		AssertJUnit.assertTrue(DraftLetterPage.isSubjectCorrect(SUBJECT));
		System.out.println("Subject is exact");

		AssertJUnit.assertTrue(DraftLetterPage.isLetterBodyCorrect(FILENAME));
		System.out.println("Letter body is identical");
		// sending the letter by clicking Send button
		// additional line breaks are sent because without that MailRu considers
		// letter
		// as empty and issues additional warning

		DraftLetterPage.sendLetter();
		System.out.println("Letter is sent");
		// checking that there is no letter in the Drafts folder by searching
		// for the
		// draft with the specified href

		DraftsFolderPage.openDraftsFolder();

		AssertJUnit.assertTrue(DraftsFolderPage.isLetterCached(SUBJECT));

	}

	@Test(description = "Verify, that the mail is in ‘Sent’ folder.", priority = 5)
	public void verifyMessageSent() {
		// checking that the letter appeared in the Sent folder
		new SentFolderPage(driver).openSendFolder();
		AssertJUnit.assertTrue(SentFolderPage.isLetterSent(SUBJECT));
		System.out.println("Letter is present in the Drafts folder.");
	}

	@Test(description = "Sign out", priority = 6)
	public void signOut() {
		// signing out of the mail box
		InboxPage.signOut();
		System.out.println("Logout was successsful");
	}

	@AfterClass(description = "Stop Browser")
	public void stopBrowser() {
		driver.quit();
	}

}
