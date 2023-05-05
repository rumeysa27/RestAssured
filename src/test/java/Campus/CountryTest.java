package Campus;


import com.github.javafaker.Faker;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Cookies;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

public class CountryTest {
    Faker faker = new Faker();
    RequestSpecification reqSpec;
    String countryID;
    String countryName;
    Map<String, String> country = new HashMap<>();

    @BeforeClass
    public void Login() {
        baseURI = "https://test.mersys.io/";
        Map<String, String> userCredential = new HashMap<>();
        userCredential.put("username", "turkeyts");
        userCredential.put("password", "TechnoStudy123");
        userCredential.put("rememberMe", "true");
        Cookies cookies =
                given()
                        .contentType(ContentType.JSON)
                        .body(userCredential)
                        .when()
                        .post("/auth/login")
                        .then()
                        //  .log().all()
                        .statusCode(200)
                        .extract().response().getDetailedCookies();

        reqSpec = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .addCookies(cookies)
                .build();

    }

    @Test
    public void createCountry() {


        countryName = faker.address().country() + "x";
        country.put("name", countryName);
        country.put("code", faker.country().countryCode2());
        countryID =
                given()
                        .spec(reqSpec)
                        .body(country)
                        .log().body()
                        .when()
                        .post("/school-service/api/countries")
                        .then()
                        // .log().body()
                        .statusCode(201)
                        .extract().path("id")
        ;
        System.out.println("countryID = " + countryID);
    }


    @Test(dependsOnMethods = "createCountry")
    public void createCountryNegative() {

        country.put("name", countryName);
        country.put("code", faker.country().countryCode2());
        given()
                .spec(reqSpec)
                .body(country)
                .log().body()
                .when()
                .post("/school-service/api/countries")
                .then()
                .log().body()
                .statusCode(400)
                .body("message", containsString("already"))
        ;

    }


    @Test(dependsOnMethods = "createCountryNegative")
    public void updateCountry() {

        countryName = faker.country().name()+"x1";
        country.put("name", countryName);
        country.put("id", countryID);
        country.put("code", faker.country().countryCode2());
        given()
                .spec(reqSpec)
                .body(country)
                .log().body()
                .when()
                .put("/school-service/api/countries")
                .then()
                //   .log().body()
                .statusCode(200)
                .body("name",equalTo(countryName))
        ;


    }

    @Test(dependsOnMethods = "updateCountry")
    public void deleteCountry() {

        given()
                .spec(reqSpec)
                .pathParam("countryID",countryID)

                .when()
                .delete("/school-service/api/countries/{countryID}")
                .then()
                .statusCode(200)
        ;

    }

    @Test(dependsOnMethods = "deleteCountry")
    public void deleteCountryNegative() {
        given()
                .spec(reqSpec)
                .pathParam("countryID",countryID)

                .when()
                .delete("/school-service/api/countries/{countryID}")
                .then()
                .log().body()
                .statusCode(400)
                .body("message",equalTo("Country not found"))
        ;
    }


}