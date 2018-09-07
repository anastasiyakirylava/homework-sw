package core.HomeworkSession8;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class SouthWestAdvancedSearhTest {
	
	public static WebDriver Driver;
	public static WebDriverWait Wait;
	
	@BeforeTest
	public static void setUp( ) {
		
	System.setProperty("webdriver.chrome.driver", "C:\\Users\\kirylana\\Drivers\\chromedriver.exe");
  Driver = new ChromeDriver();
	Wait = new WebDriverWait(Driver, 50);}
  @Test
  public void southWestAdvancedSearch() throws InterruptedException {
	  

		Driver.get("https://www.southwest.com/");
		Driver.manage().window().maximize();
		
		// click advanced search button		
		Driver.findElement(By.xpath("//a[text()='Advanced search']")).click();
		
		
		// select one way trip
		Wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@value='oneway']")));
		Driver.findElement(By.xpath("//input[@value='oneway']")).click();
		
		//type SAN into departure airport
		Driver.findElement(By.xpath("//input[@id='originationAirportCode']")).click();
		Driver.findElement(By.xpath("//input[@id='originationAirportCode']")).clear();
		Driver.findElement(By.xpath("//input[@id='originationAirportCode']")).sendKeys("SAN");
		
		//type SFO into arrival airport
		Driver.findElement(By.xpath("//input[@id='destinationAirportCode']")).click();
		Driver.findElement(By.xpath("//input[@id='destinationAirportCode']")).clear();
		Driver.findElement(By.xpath("//input[@id='destinationAirportCode']")).sendKeys("SFO");
		
		//click on the calendar icon
		Driver.findElement(By.xpath("(//div[@class='input--icon-separator'])[1]")).click();
		
		//get todays date
		Date now = new Date();
		
		//format it into day of the week only
		SimpleDateFormat simpleDateformat = new SimpleDateFormat("EEEE");
		
		//use todays day of the week in the xpath to make it dynamic
		//index [2] means that we will click on the second button that has our day-of-the-week in the attribute aka 7 days ahead from now,
         //because we have 7 days ahead of today in the task
		String data = simpleDateformat.format(now);
		Driver.findElement(By.xpath("(//button[contains(@aria-label,'" + data + "')])[2]")).click();
		
						
		// variable for the day of the week when we will depart (day of the week, MMM dd, yyyy)
		String ExpectedDay = Driver.findElement(By.xpath("(//div[@class='form-control--message'])[2]")).getText();
		
		//select "departure from noon to 6 pm" in drop down menu	
		
	Wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='departureTimeOfDay']")));
		Driver.findElement(By.xpath("//input[@id='departureTimeOfDay']")).click();
	Thread.sleep(1000);
		Wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//li[@id='departureTimeOfDay--item-2']")));
		Driver.findElement(By.xpath("//li[@id='departureTimeOfDay--item-2']")).click();
		Thread.sleep(1000);
		//click on the passenger select button, so we can change it's value
		Wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='adultPassengersCount']")));
		Driver.findElement(By.xpath("//input[@id='adultPassengersCount']")).click();
		
		//get the number of passengers set by default, and convert it to int
		String adulttxt = Driver.findElement(By.xpath("//input[@id='adultPassengersCount']")).getAttribute("aria-valuenow");
		int adultnumber = Integer.parseInt(adulttxt);
				
		//Choose one passenger if it's not set up by default	
		for (;adultnumber < 1; adultnumber++)
		{
		Driver.findElement(By.xpath("//span[@icon='swa-icon_plus-circle']")).click();	
		}	

		for (;adultnumber > 1; adultnumber--)
		{
		Driver.findElement(By.xpath("//span[@icon='swa-icon_minus-circle']")).click();	
		}	
		
		
		//click submit button
		Driver.findElement(By.xpath("//button[@id='form-mixin--submit-button']")).click();
		
		//wait till new page will load
		Wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='sticky sticky_header']"))); //sticky sticky header is super funny=))
		Thread.sleep(3000);
		
		//------------------------------------------------------------------------------------------------------------
		
		// Verifying if the results in the list of flights are valid: 
		// 1) Contain the right hour of departure (0-6 o'clock)
		// 2) Contain the right time of the day (PM)
		
		//create variable for every departure time in the results  
		By myDate = By.xpath("//div[@type='origination']");
		
		//create list of elements based on previous variable
		java.util.List<WebElement> rows =Driver.findElements(myDate);
		
		//2 variables to store the value of invalid results (if so)
		int invalidhour = 0;
		int invalidtime = 0;
		
		// for every departure time in the list of the results
		for(WebElement row : rows) {
			
			//get text from every element 
			String deptime = row.getText();
			
			//Divide the first char from that text which is value of of hours (minutes doesn't matter for this scenario)
			char firstchar = deptime.charAt(0);
			
			//convert char to string
			int depthour = Character.getNumericValue(firstchar);
			
			
			//verify that departure hour is in between noon and 6 (time can't be negative, so we can say < 6)
			if (depthour > 6)
			{
				invalidhour++;
			}
			
			//verify that departure time is in PM (as all afternoon hours are)
			if (deptime.contains("AM"))
			{
				invalidtime++;
			}
			System.out.println(row.getText());
						}
		
		//verify that both hour is between noon and 6, and time is in pm.
		boolean TimeCheck = (invalidhour==0) & (invalidtime==0);
		
		//--------------------------------------------------------------------------------------
//Verify if the day selected in the calendar on the page with the flight results is the one that we've chosen previously in the date picker 
		
		//get text from selected day on the results page (format MMM dd), [3]-because the selected date is always number 3 element
		String ActualDay = Driver.findElement(By.xpath("(//span[@class='calendar-strip--date'])[3]")).getText();
		
		//verify that our expected day (see line 74-75) contains text from actual day variable 
		boolean DayCheck = ExpectedDay.contains(ActualDay);
		
		
		//last step : assert that both conditions have been met (day is correct & time is correct)
		Assert.assertEquals(DayCheck&TimeCheck, true);
		
		
		// Just to keep track of what went wrong (if so)
		System.out.println(TimeCheck);
		System.out.println(DayCheck);
		System.out.println("Invalid departure hour = " + invalidhour);		System.out.println("Invalid departure day time = " + invalidtime);
		
	}
  
@AfterTest
public static void tearDown()

{
	Driver.manage().deleteAllCookies();
	Driver.quit();
}
	  
  }

