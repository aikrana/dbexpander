

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.concurrent.TimeUnit;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
/**
 *
 * @author arii
 */
public class Testregdb1 {
private WebDriver driver;
  private String baseUrl;
  private boolean acceptNextAlert = true;
  private StringBuffer verificationErrors = new StringBuffer();

  @Before
  public void setUp() throws Exception {
    driver = new FirefoxDriver();
    baseUrl = "https://www.dropbox.com";
    driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
  }

  @Test
  public void testRegdb() throws Exception {
     driver.get(baseUrl + "/referrals/NTM2OTAyMjIyMjk?src=global9");
    for (int second = 0;; second++) {
    	if (second >= 60) fail("timeout");
    	try { if (isElementPresent(By.xpath("//div[2]/div/div/form/div[2]/div[2]/input"))) break; } catch (Exception e) {}
    	Thread.sleep(1000);
    }

    driver.findElement(By.xpath("//div[2]/div/div/form/div[2]/div[2]/input")).clear();
    driver.findElement(By.xpath("//div[2]/div/div/form/div[2]/div[2]/input")).sendKeys("NOMBRE");
    driver.findElement(By.xpath("//div[2]/div/div/form/div[3]/div[2]/input")).clear();
    driver.findElement(By.xpath("//div[2]/div/div/form/div[3]/div[2]/input")).sendKeys("APELLIDOS");
    driver.findElement(By.xpath("//div[5]/div[2]/input")).clear();
    driver.findElement(By.xpath("//div[5]/div[2]/input")).sendKeys("CONTRASEÑA");
    driver.findElement(By.xpath("//div[2]/div/div/form/div[4]/div[2]/input")).clear();
    driver.findElement(By.xpath("//div[2]/div/div/form/div[4]/div[2]/input")).sendKeys("c9eiiuhi@gmail.com");
    driver.findElement(By.xpath("//div[6]/input")).click();
    driver.findElement(By.xpath("//div[2]/div/div/form/button")).click();
    Thread.sleep(2000);
    
    try {
      assertEquals("https://www.dropbox.com/gs", driver.getCurrentUrl());
    } catch (Error e) {
      verificationErrors.append(e.toString());
    }

    driver.get(baseUrl + "/logout");
    try {
      assertEquals("https://www.dropbox.com/login", driver.getCurrentUrl());
    } catch (Error e) {
      verificationErrors.append(e.toString());
    }
  }

  @After
  public void tearDown() throws Exception {
    driver.quit();
    String verificationErrorString = verificationErrors.toString();
    if (!"".equals(verificationErrorString)) {
      fail(verificationErrorString);
    }
  }

  private boolean isElementPresent(By by) {
    try {
      driver.findElement(by);
      return true;
    } catch (NoSuchElementException e) {
      return false;
    }
  }

  private boolean isAlertPresent() {
    try {
      driver.switchTo().alert();
      return true;
    } catch (NoAlertPresentException e) {
      return false;
    }
  }

  private String closeAlertAndGetItsText() {
    try {
      Alert alert = driver.switchTo().alert();
      String alertText = alert.getText();
      if (acceptNextAlert) {
        alert.accept();
      } else {
        alert.dismiss();
      }
      return alertText;
    } finally {
      acceptNextAlert = true;
    }
  }
}