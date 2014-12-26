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
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

/**
 *
 * @author arii
 */
public class Db {

  
    /**
     * @param args the command line arguments
     */
    
    static void genAccount(String a[][], String mSuffix, int f) throws Exception{
        String account[] = {
        RandomStringUtils.randomAlphabetic(6), //name
        RandomStringUtils.randomAlphabetic(9), //shurname
        RandomStringUtils.randomAlphanumeric(10), //password
        (RandomStringUtils.randomAlphanumeric(10))+mSuffix}; //mail+mSuffix
        
        for (int i=0; i < a[f].length; i++)
            a[f][i]= account[i];
        
    }
    
    static boolean regAccount(String a[][], String referralUrl, int f, WebDriver driver) throws InterruptedException{
        
        String name=a[f][0], shurname=a[f][1], password=a[f][2], mail=a[f][3];
        boolean ispresent=false, registered=false;
        String gsUrl="https://www.dropbox.com/gs";
        
        driver.get(referralUrl);
        for (int second = 0;; second++) {
            if (second >= 30){ 
                System.out.println("Connection timeout");
                break;
            }
            
            //ispresent
            
            try {
                driver.findElement(By.xpath("//div[2]/div/div/form/div[2]/div[2]/input"));
                ispresent=true;
            } catch (NoSuchElementException e) {
                ispresent=false;
            }
            try { if (ispresent) break; } catch (Exception e) {}
            Thread.sleep(1000);
        }
        Thread.sleep(100);
        
        if (ispresent){
             driver.findElement(By.xpath("//div[2]/div/div/form/div[2]/div[2]/input")).clear();
             driver.findElement(By.xpath("//div[2]/div/div/form/div[2]/div[2]/input")).sendKeys(name);
             driver.findElement(By.xpath("//div[2]/div/div/form/div[3]/div[2]/input")).clear();
             driver.findElement(By.xpath("//div[2]/div/div/form/div[3]/div[2]/input")).sendKeys(shurname);
             driver.findElement(By.xpath("//div[5]/div[2]/input")).clear();
             driver.findElement(By.xpath("//div[5]/div[2]/input")).sendKeys(password);
             driver.findElement(By.xpath("//div[2]/div/div/form/div[4]/div[2]/input")).clear();
             driver.findElement(By.xpath("//div[2]/div/div/form/div[4]/div[2]/input")).sendKeys(mail);
             driver.findElement(By.xpath("//div[6]/input")).click(); //Check tos agree
             Thread.sleep(100);
             driver.findElement(By.xpath("//div[2]/div/div/form/button")).click();   //Submit register form 
             
             for (int second = 0;; second++) {
                 if (second >= 15){
                     System.out.println("Submitting form timeout");
                     break;
                 }

                 if (gsUrl.equals(driver.getCurrentUrl()))
                     registered=true;

                 try { if (registered) break; } catch (Exception e) {}
                 Thread.sleep(1000);
             }   
        }
       
        int n=f+1;
        if (registered)
            System.out.print("·");
        else
            System.out.print("x");
        
        driver.manage().deleteAllCookies();
        return registered;
        //returns true or false
    }
    
    public static void main(String[] args) throws InterruptedException, Exception {
        // TODO code application logic here
        
        
        //SETUP
        WebDriver driver;
        String referralUrl, accounts[][];
        String mSuffix = new String();
        
        double space=0, nAccounts;
        int needAccounts, creAccounts, f;
        StringBuffer verificationErrors = new StringBuffer();
        driver = new FirefoxDriver();
//        driver = new HtmlUnitDriver();
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        
        
        referralUrl = "https://www.dropbox.com/referrals/NTM2OTAyMjIyMjk?src=global9";
        mSuffix = "@gmail.com";
        space=16.250;
        space=(Math.floor(space * 2) / 2); //trunk number to .0 or .5

        needAccounts=(int) ((18-space)*2); //number of accounts needed
        creAccounts=0;  //number of accounts created
        accounts = new String [needAccounts][4]; //accounts credentials matrix
        
        //GENERATE ACCOUNTS
        
        for (f=0; f<accounts.length; f++)
            genAccount(accounts, mSuffix, f);
        
        
        //REGISTER
        int regAttempts=0;
        System.out.print("Registering accounts => ");
        for (f=0; f<accounts.length; f++){
            while (!regAccount(accounts, referralUrl, f, driver) && regAttempts<3){
                
                regAttempts++;
                genAccount(accounts, mSuffix, f);
            }
            if (regAttempts <3) creAccounts++;
            else System.out.println("Registration failed 3 consecutive times. Aborting");
            
        }//end for
        
        if (creAccounts==needAccounts)
            System.out.println("\n"+ creAccounts +" DROPBOX ACCOUNTS CREATED SUCCESFULLY!!  :D :D");
       
        //TEARDOWN
        driver.quit();
    
    }//end main
    
}//end class
