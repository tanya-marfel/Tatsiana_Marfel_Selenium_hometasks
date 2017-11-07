package com.epam.ftm.selenium.part2.safari;

import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.opera.OperaDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.AssertJUnit;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class MailRuTestOpera {

	private static final String URL = "https://mail.ru";
	private static final String USERNAME = "";
	private static final String PASSWD = "";
	private static final String EMAIL_FIELD = "//input[@id = 'mailbox:login']";
	private static final String PASSWD_FIELD = "//input[@id = 'mailbox:password']";
	private static final String SUBMIT = "//input[@type='submit']";
	private static final String DROPDOWN = "mailbox:domain";
	private static String ADDRESSEE = "tatsianamarfel@gmail.com";
	private static String ADDRESSEE_FIELD = "//textarea[@tabindex='4']";
	private static String SUBJECT = "Test letter";
	private static String SUBJECT_FIELD = "//input[@name='Subject']";
	private static final String BODY_FIELD = "tinymce";
	private static String FILENAME = "src/main/resources/LoremIpsum.txt";
	private static final String SEND = "div[data-name='send']";
	private static final String DRAFT = "div[data-name=\"saveDraft\"]";
	private static final String STATUS = "//div[@data-mnemo=\"saveStatus\"]";
	private static final String DRAFTS = "//a[@href='/messages/drafts/']";
	private static final String SENT = "//a[@href='/messages/sent/']";
	private static final String PRESENCE = "//a[@data-subejct='" + SUBJECT + "'" + "]";
	private static final String TO_SCRIPT = "document.getElementsByName('To')[0].setAttribute('type', 'text');";
	private static final String INBOX_FOLDER = "//a[@href='/messages/inbox/']";
	private static final String COMPOSE = "//a[@data-name=\"compose\"]";
	private static final String SUBJECT_SCRIPT = "document.getElementsByName('Subject')[0].setAttribute('type', 'text');";
	private static final String BODY_FRAME = "//iframe[@tabindex='10']";
	private static final String LOGOUT = "PH_logoutLink";
	private static final String ADDRESSEES = "compose_to";
	private static final String ADDRESSEES_VALUE = "value";
	private static final String SUBJECT_VALUE = "data-subject";

	private WebDriver driver;

	@BeforeClass(description = "Launch browser")
	public void launchBrowser() {
		// launching the browser for the test before the first method is executed
		System.setProperty("webdriver.opera.driver", "src/main/resources/operadriver");
		driver = new OperaDriver();
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
		enterCredentials(USERNAME, PASSWD);
		Select dropdown = new Select(driver.findElement(By.id(DROPDOWN)));
		dropdown.selectByIndex(1);
		driver.findElement(By.xpath(SUBMIT)).click();
		AssertJUnit.assertTrue(isElementPresent(By.xpath(INBOX_FOLDER)));
	}

	@Test(description = "Compose new letter", priority = 1)
	public void createLetter() throws IOException {
		// clicking a button for creating a new letter
		driver.findElement(By.xpath(COMPOSE)).click();
		// invoking a method to fill in addressee, subject and body fields for the new
		// letter
		// text for a body text is read from a file
		writeLetter(ADDRESSEE, SUBJECT, FILENAME);

	}

	@Test(description = "Save the mail as a draft.", priority = 2)
	private void saveDraft() {
		// saving the mail created in the previous test by clicking Save button
		WebElement save = driver.findElement(By.cssSelector(DRAFT));
		save.click();
		save = new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfElementLocated(By.xpath(STATUS)));

	}

	@Test(description = "Verify, that the mail presents in ‘Drafts’ folder.", priority = 3)
	private void draftExist() {
		// checking that the draft exist in the Drafts folder and navigating to this
		// letter
		driver.findElement(By.xpath(DRAFTS)).click();
		AssertJUnit.assertTrue(isElementPresent(By.xpath(PRESENCE)));
		WebElement draft = new WebDriverWait(driver, 10)
				.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(PRESENCE)));
		draft.click();
	}

	@Test(description = "Verify the draft content, send the letter and check it is no longer in the Drafts folder", priority = 4)
	private void verifyContent() throws IOException, InterruptedException {
		// verifying the letter content by comparing initial and current values
		// since subject and addressee text is hidden JavaScriptExecutor is applied
		// for addressee only contains is used because the mail address is replaced by
		// the name,
		// thus tatsianamarfel@gmail.com becomes tatsianamarfel
		WebElement draft = driver.findElement(By.xpath(PRESENCE));
		draft.click();
		String addressee = driver.findElement(By.id(ADDRESSEES)).getAttribute(ADDRESSEES_VALUE);
		final String currentUrl = "//a[@href='" + driver.getCurrentUrl() + "']";
		String subject = driver.findElement(By.xpath(currentUrl)).getAttribute(SUBJECT_VALUE);
		executeScript(TO_SCRIPT);
		assertTrue(addressee.contains(ADDRESSEE));
		executeScript(SUBJECT_SCRIPT);
		assertTrue(subject.equals(SUBJECT));
		driver.switchTo().frame(driver.findElement(By.xpath(BODY_FRAME)));
		String body = driver.findElement(By.id(BODY_FIELD)).getText();
		assertTrue(body.equals(new String(readFile(FILENAME))));

		// sending the letter by clicking Send button
		// additional line breaks are sent because without that MailRu considers letter
		// as empty and issues additional warning

		WebElement letterBody = driver.findElement(By.id(BODY_FIELD));
		letterBody.click();
		letterBody.sendKeys(Keys.ENTER);
		driver.switchTo().defaultContent();
		sendLetter(SEND);

		// checking that there is no letter in the Drafts folder by searching for the
		// draft with the specified href
		WebElement draftsFolder = new WebDriverWait(driver, 10)
				.until(ExpectedConditions.presenceOfElementLocated(By.xpath(DRAFTS)));
		draftsFolder.click();
		AssertJUnit.assertFalse(isElementPresent(By.xpath(currentUrl)));
	}

	@Test(description = "Verify, that the mail is in ‘Sent’ folder.", priority = 5)
	public void verifyMessageSent() {
		// checking that the letter appeared in the Sent folder
		WebElement sentFolder = new WebDriverWait(driver, 10)
				.until(ExpectedConditions.presenceOfElementLocated(By.xpath(SENT)));
		sentFolder.click();
		AssertJUnit.assertTrue(isElementPresent(By.xpath(PRESENCE)));
	}

	@Test(description = "Sign out", priority = 6)
	public void signOut() {
		// signing out of the mail box

		driver.findElement(By.id(LOGOUT)).click();
	}

	private void enterCredentials(String username, String password) {
		WebElement usernameInput = driver.findElement(By.xpath(EMAIL_FIELD));
		usernameInput.sendKeys(username);
		WebElement passwordInput = driver.findElement(By.xpath(PASSWD_FIELD));
		passwordInput.sendKeys(password);

	}

	public boolean isElementPresent(By by) {
		// Custom implementation for is ElementPresent
		return !driver.findElements(by).isEmpty();
	}

	public void writeLetter(String addressee, String subject, String fileName) throws IOException {
		String line = null;
		try {
			WebElement address = driver.findElement(By.xpath(ADDRESSEE_FIELD));
			address.click();
			address.sendKeys(ADDRESSEE);
			address.sendKeys(Keys.RETURN);
		} catch (org.openqa.selenium.StaleElementReferenceException ex) {
			WebElement address = driver.findElement(By.xpath(ADDRESSEE_FIELD));
			address.click();
			address.sendKeys(ADDRESSEE);
			address.sendKeys(Keys.RETURN);
		} finally {
			WebElement subj = driver.findElement(By.xpath(SUBJECT_FIELD));
			subj.click();
			subj.sendKeys(SUBJECT);
			driver.switchTo().frame(driver.findElement(By.xpath(BODY_FRAME)));
			WebElement letterBody = driver.findElement(By.id(BODY_FIELD));
			letterBody.click();
			letterBody.sendKeys(readFile(fileName));
			driver.switchTo().defaultContent();
		}

	}

	private void executeScript(String script) {
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		jse.executeScript(script);
	}

	private String readFile(String fileName) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(fileName));
		return new String(encoded);
	}

	public void sendLetter(String send) {
		int attempts = 0;
		while (attempts < 2) {
			try {
				driver.findElement(By.cssSelector(send)).click();
				break;
			} catch (StaleElementReferenceException e) {
			}
			attempts++;
		}

	}
}
