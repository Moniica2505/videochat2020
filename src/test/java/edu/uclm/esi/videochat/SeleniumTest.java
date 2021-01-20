package edu.uclm.esi.videochat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.After;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNot.not;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.annotation.Order;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import edu.uclm.esi.videochat.model.User;
import edu.uclm.esi.videochat.springdao.UserRepository;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Alert;
import org.openqa.selenium.Keys;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.net.MalformedURLException;
import java.net.URL;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class SeleniumTest {
	public WebDriver chrome, firefox, edge, firefox2, firefox3;
	private Map<String, Object> vars;
	JavascriptExecutor js;

	ArrayList<WebDriver> drivers = new ArrayList<>();

	@Autowired
	UserRepository usersRepo;

	@Before
	public void setUp() {
		System.setProperty("webdriver.chrome.driver", "C:\\Users\\monic\\Downloads\\chromedriver.exe");
		System.setProperty("webdriver.gecko.driver", "C:\\Users\\monic\\Downloads\\geckodriver.exe");
		System.setProperty("webdriver.edge.driver", "C:\\Users\\monic\\Downloads\\msedgedriver.exe");
		
		FirefoxOptions options = new FirefoxOptions();
		options.addPreference("permissions.default.camera", 1);

		firefox = new FirefoxDriver(options);
		firefox2 = new FirefoxDriver(options);
		firefox3 = new FirefoxDriver(options);

		vars = new HashMap<String, Object>();
	}

	@After
	public void tearDown() {
		for (int i = 0; i < drivers.size(); i++)
			drivers.get(i).close();
	}

	private void recibirLlamada(WebDriver driver, String nombre) {
		if (nombre == "Ana") {
			assertThat(driver.switchTo().alert().getText(), is("Te llama Pepe. ¿Contestar?\n"));
			driver.switchTo().alert().dismiss();
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} else if (nombre == "Lucas") {
			assertThat(driver.switchTo().alert().getText(), is("Te llama Ana. ¿Contestar?\n"));
			driver.switchTo().alert().accept();
		}
	}

	private void enviarLlamada(WebDriver driver, String nombre) {
		WebElement btnEnviarOfertaDePepeAAna = driver.findElement(By.xpath("/html/body/div/oj-module/div[1]/div[2]/div/div/div[3]/div[1]/div[2]/button"));
		WebElement btnEnviarOfertaDeAnaALucas = driver.findElement(By.xpath("/html/body/div/oj-module/div[1]/div[2]/div/div/div[3]/div[1]/div[3]/button"));

		if (nombre == "Pepe") {
			btnEnviarOfertaDePepeAAna.click();
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			recibirLlamada(firefox2, "Ana");
			assertThat(driver.switchTo().alert().getText(), is("Llamada de Ana rechazada"));
			driver.switchTo().alert().accept();
		} else if (nombre == "Ana") {
			btnEnviarOfertaDeAnaALucas.click();
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			recibirLlamada(firefox3, "Lucas");
		}
	}

	private void hacerLogin(WebDriver driver, String nombre, String pwd) {
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		WebElement cajaNombre = driver.findElement(By.id("inputNombre"));
		WebElement cajaPwd = driver.findElement(By.id("inputPwd"));
		WebElement btnEntrar = driver.findElement(By.id("btnEntrar"));
		cajaNombre.click();
		cajaNombre.sendKeys(nombre);
		cajaPwd.click();
		cajaPwd.sendKeys(pwd);
		btnEntrar.click();
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		assertThat(driver.findElement(By.id("Chat")).getText(), is("Fantástico videochat"));
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void registrar(WebDriver driver, String nombre, String email, String pwd, String urlFoto) {
		driver.get("https://localhost:7500/");

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		WebElement btnCrearCuentaLogin = driver.findElement(By.id("btnCrearCuentaLogin"));
		btnCrearCuentaLogin.click();

		WebElement cajaNombre = driver.findElement(By.id("nombre"));
		WebElement cajaEmail = driver.findElement(By.id("email"));
		WebElement cajaPwd1 = driver.findElement(By.id("pwd1"));
		WebElement cajaPwd2 = driver.findElement(By.id("pwd2"));
		WebElement seleccionArchivo = driver.findElement(By.id("img"));
		WebElement btnCrearCuenta = driver.findElement(By.id("btnCrearCuenta"));

		cajaNombre.click();
		cajaNombre.sendKeys(nombre);
		cajaEmail.click();
		cajaEmail.sendKeys(email);
		cajaPwd1.click();
		cajaPwd1.sendKeys(pwd);
		cajaPwd2.click();
		cajaPwd2.sendKeys(pwd);
		seleccionArchivo.sendKeys(urlFoto);
		btnCrearCuenta.click();

		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		assertThat(driver.switchTo().alert().getText(), is("Registrado correctamente"));
		driver.switchTo().alert().accept();

	}

	@Test
	public void test() {
		Optional<User> optUserPepe = usersRepo.findByName("Pepe");
		if (optUserPepe.isPresent()) {
			User user = optUserPepe.get();
			usersRepo.deleteById(user.getId());
		}
		Optional<User> optUserAna = usersRepo.findByName("Ana");
		if (optUserAna.isPresent()) {
			User user = optUserAna.get();
			usersRepo.deleteById(user.getId());
		}
		Optional<User> optUserLucas = usersRepo.findByName("Lucas");
		if (optUserLucas.isPresent()) {
			User user = optUserLucas.get();
			usersRepo.deleteById(user.getId());
		}

		registrar(firefox, "Pepe", "pepe@pepe.com", "pepe",
				"C:\\Users\\monic\\MEGA\\7º Año Ing. Informatica\\1er Cuatrimestre\\Tecnologías y Sistemas Web\\Practicas\\IconosVideochat\\iconoPepe.png");
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		hacerLogin(firefox, "Pepe", "pepe");
		
		registrar(firefox2, "Ana", "ana@ana.com", "ana",
				"C:\\Users\\monic\\MEGA\\7º Año Ing. Informatica\\1er Cuatrimestre\\Tecnologías y Sistemas Web\\Practicas\\IconosVideochat\\iconoAna.png");
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		hacerLogin(firefox2, "Ana", "ana");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		registrar(firefox3, "Lucas", "lucas@lucas.com", "lucas",
				"C:\\Users\\monic\\MEGA\\7º Año Ing. Informatica\\1er Cuatrimestre\\Tecnologías y Sistemas Web\\Practicas\\IconosVideochat\\icono.png");
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		hacerLogin(firefox3, "Lucas", "lucas");
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		enviarLlamada(firefox, "Pepe");
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		enviarLlamada(firefox2, "Ana");
	}
}
