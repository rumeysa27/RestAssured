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
import static org.hamcrest.Matchers.*;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class ToDoCitizenships {
    RequestSpecification recSpec;

    String citizenName;

    Faker randomUretici = new Faker();

  String citizenID;

    @BeforeClass
    public void Setup()  {
        baseURI="https://test.mersys.io";

        Map<String,String> kullaniciblglr=new HashMap<>();
       kullaniciblglr.put("username","turkeyts");
       kullaniciblglr.put("password","TechnoStudy123");
       kullaniciblglr.put("rememberMe","true");

        Cookies cookies=
                given()
                        .contentType(ContentType.JSON)
                        .body(kullaniciblglr)

                        .when()
                        .post("/auth/login")

                        .then()
                       // .log().body()
                        .statusCode(200)
                        .extract().response().getDetailedCookies()
                ;

      recSpec= new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .addCookies(cookies)
                .build(); // şu konuya bi bak

    }

    @Test
    public void createCitizen(){

        Map<String,String> citizen=new HashMap<>();
        citizenName = randomUretici.address().cityName() ;
        citizen.put("name", citizenName);
        citizen.put("shortName", randomUretici.name().nameWithMiddle());
        citizenID =

                given()
                        .spec(recSpec)
                        .body(citizen)
                        .log().body()


                        .when()
                        .post("/school-service/api/citizenships")

                        .then()
                         .log().body()
                        .statusCode(201)
                        .extract().path("id")
        ;
    }






    @Test(dependsOnMethods = "createCitizen")
    public void createCitizenNegative(){
        Map<String,String> citizen=new HashMap<>();

        citizen.put("name", citizenName);

        given()
                .spec(recSpec)
                .body(citizen)
                .log().body()

                .when()
                .post("/school-service/api/citizenships")

                .then()
                .statusCode(400)
                .body("message", containsString("already"))
                ;


    }

    @Test (dependsOnMethods = "createCitizenNegative")
    public void updateCitizen(){
        Map<String,String> citizen=new HashMap<>();

        citizenName = randomUretici.address().cityName();
        citizen.put("name", citizenName);
        citizen.put("id", citizenID);


        given()
                .spec(recSpec)
                .body(citizen)

                .when()
                .put("/school-service/api/citizenships")

                .then()
                .statusCode(200)
                .body("name", equalTo(citizenName))

        ;

    }

    @Test(dependsOnMethods = "updateCitizen")
    public void deleteCitizen(){
        given()
                .spec(recSpec)
                .pathParam("citizenID", citizenID)

                .when()
                .delete("/school-service/api/citizenships/{citizenID}")

                .then()
                .log().body()
                .statusCode(200)
        ;



    }
    @Test(dependsOnMethods = "deleteCitizen")
    public void deleteCitizenNegative(){

        given()
                .spec(recSpec)
                .pathParam("citizenID", citizenID)
                .log().uri()

                .when()
                .delete("/school-service/api/citizenships/{citizenID}")

                .then()
                .log().all()
                .statusCode(400)
                .body("message", equalTo("Citizenship not found"))
        ;


    }
}
