package com.epam.ftm.selenium.part3.chrome;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;

public abstract class Page {
	protected static WebDriver driver;
	
	public Page(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(this.driver, this);
	}
	
	public WebDriver getDriver() {
		return driver;
	}
	
	public static Boolean isElementPresent(By by) {
		return driver.findElements(by).size() > 0;
	}
	
	public static void executeScript(String script) {
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		jse.executeScript(script);
	}
	
	protected static String readFile(String fileName) throws IOException {
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
	
	public static void fluentlyWait() {
		Wait fw = new FluentWait(driver).withTimeout(10, TimeUnit.SECONDS)    
			    .pollingEvery(1, TimeUnit.SECONDS);
	}
}
