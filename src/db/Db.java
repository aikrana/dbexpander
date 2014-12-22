/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package db;

import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.RandomStringUtils;
import static org.junit.Assert.*;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

/**
 *
 * @author arii
 */
public class Db {

  
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException {
        // TODO code application logic here
        
        
        //SETUP
        WebDriver driver;
        String baseUrl;
        boolean acceptNextAlert = true;
        StringBuffer verificationErrors = new StringBuffer();
//        FirefoxProfile profile = new FirefoxProfile();      
//        profile.setPreference("browser.link.open_newwindow.restriction", 1);
//        driver = new org.openqa.selenium.firefox.FirefoxDriver(profile);
        driver = new FirefoxDriver();
//        driver = new HtmlUnitDriver();
//        driver.manage().window().minimize();
        driver.manage().window().setPosition(new Point(-2000, 0));
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        baseUrl = "https://www.dropbox.com";
     

        //REGISTRO
        String name=RandomStringUtils.randomAlphabetic(6),
               shurname=RandomStringUtils.randomAlphabetic(9),
               password=RandomStringUtils.randomAlphanumeric(10), 
               mail=RandomStringUtils.randomAlphanumeric(10), 
               suffix="@gmail.com";
        
        driver.get(baseUrl + "/referrals/NTM2OTAyMjIyMjk?src=global9");
        for (int second = 0;; second++) {
            if (second >= 60) fail("timeout");
            
            //isElementPresent
            boolean ispresent;
            try {
                driver.findElement(By.xpath("//div[2]/div/div/form/div[2]/div[2]/input"));
                ispresent=true;
            } catch (NoSuchElementException e) {
                ispresent=false;
            }
            try { if (ispresent) break; } catch (Exception e) {}
            Thread.sleep(1000);
        }

        driver.findElement(By.xpath("//div[2]/div/div/form/div[2]/div[2]/input")).clear();
        driver.findElement(By.xpath("//div[2]/div/div/form/div[2]/div[2]/input")).sendKeys(name);
        driver.findElement(By.xpath("//div[2]/div/div/form/div[3]/div[2]/input")).clear();
        driver.findElement(By.xpath("//div[2]/div/div/form/div[3]/div[2]/input")).sendKeys(shurname);
        driver.findElement(By.xpath("//div[5]/div[2]/input")).clear();
        driver.findElement(By.xpath("//div[5]/div[2]/input")).sendKeys(password);
        driver.findElement(By.xpath("//div[2]/div/div/form/div[4]/div[2]/input")).clear();
        driver.findElement(By.xpath("//div[2]/div/div/form/div[4]/div[2]/input")).sendKeys(mail+suffix);
        driver.findElement(By.xpath("//div[6]/input")).click();
        driver.findElement(By.xpath("//div[2]/div/div/form/button")).click();
        
        String url="https://www.dropbox.com/gs";
        boolean registered=false;
        
        for (int second = 0;; second++) {
            if (second >= 10) fail("timeout");
            
            if (url.equals(driver.getCurrentUrl()))
                registered=true;
            
/*            try {
                assertEquals("https://www.dropbox.com/gs", driver.getCurrentUrl());
                registered=true;
            } catch (Error e) {
                verificationErrors.append(e.toString());
                registered=false;
            }
*/        
            try { if (registered) break; } catch (Exception e) {}
            Thread.sleep(1000);
        }   
        if (registered){
            driver.get(baseUrl + "/logout");
            System.out.println("Cuenta registrada");
            try {
              assertEquals("https://www.dropbox.com/login", driver.getCurrentUrl());
            } catch (Error e) {
              verificationErrors.append(e.toString());
            }
        }else
            System.out.println("Registro fallido");
        //TEARDOWN
        driver.quit();
        String verificationErrorString = verificationErrors.toString();
        if (!"".equals(verificationErrorString)) {
            fail(verificationErrorString);
        }


    
    }//end main
    
}//end class
