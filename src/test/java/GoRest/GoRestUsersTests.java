package GoRest;

import com.github.javafaker.Faker;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class GoRestUsersTests {
    Faker randomUretici = new Faker();
    int userID;

    RequestSpecification reqSpec;

    @BeforeClass
    public void setup() {

        baseURI = "https://gorest.co.in/public/v2/users";
        //baseURI ="https://test.gorest.co.in/public/v2/users/";

        reqSpec = new RequestSpecBuilder()
                .addHeader("Authorization", "Bearer a41f62d10ace47aa058490a9bdeba0d4500060eefea9488bc97a68a4dcf90e4f")
                .setContentType(ContentType.JSON)
                .setBaseUri(baseURI)
                .build();

    }


    @Test(enabled = false)
    public void createUserJson() {
        // POST https://gorest.co.in/public/v2/users
        // "Authorization: Bearer a41f62d10ace47aa058490a9bdeba0d4500060eefea9488bc97a68a4dcf90e4f")"
        // {"name":"{{$randomFullName}}", "gender":"male", "email":"{{$randomEmail}}", "status":"active"}
        String rndFullname = randomUretici.name().fullName();
        String rndEmail = randomUretici.internet().emailAddress();

        userID =
                given()
                        .spec(reqSpec)
                        .body("{\"name\":\"" + rndFullname + "\", \"gender\":\"female\", \"email\":\"" + rndEmail + "\", \"status\":\"active\"}")
                        //.log().uri()
                        //.log().body()

                        .when()
                        .post("")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .contentType(ContentType.JSON)
                        .extract().path("id");
    }

    @Test
    public void createUserMap() {

        System.out.println("baseURI = " + baseURI);
        String rndFullname = randomUretici.name().fullName();
        String rndEmail = randomUretici.internet().emailAddress();

        Map<String, String> newUser = new HashMap<>();
        newUser.put("name", rndFullname);
        newUser.put("gender", "male");
        newUser.put("email", rndEmail);
        newUser.put("status", "active");

        userID =
                given()
                        .header("Authorization", "Bearer a41f62d10ace47aa058490a9bdeba0d4500060eefea9488bc97a68a4dcf90e4f")
                        .contentType(ContentType.JSON)
                        .body(newUser)
                        .log().uri()
                        //.log().body()

                        .when()
                        .post("")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .contentType(ContentType.JSON)
                        .extract().path("id");
    }

    @Test(enabled = false)
    public void createUserClass() {
        String rndFullname = randomUretici.name().fullName();
        String rndEmail = randomUretici.internet().emailAddress();

        User newUser = new User();
        newUser.name = rndFullname;
        newUser.gender = "male";
        newUser.email = rndEmail;
        newUser.status = "active";

        userID =
                given()
                        .spec(reqSpec)
                        .body(newUser)
                        //.log().uri()
                        //.log().body()

                        .when()
                        .post("")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .contentType(ContentType.JSON)
                        .extract().path("id");
    }

    @Test(dependsOnMethods = "createUserMap")
    public void getUserByID() {

        given()
                .spec(reqSpec)

                .when()
                .get("" + userID)

                .then()
                .log().body()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", equalTo(userID))
        ;
    }

    @Test(dependsOnMethods = "getUserByID")
    public void updateUser() {

        Map<String, String> updateUser = new HashMap<>();
        updateUser.put("name", "Rumeysa Bakir");

        given()
                .spec(reqSpec)
                .body(updateUser)

                .when()
                .put("" + userID)

                .then()
                .log().body()
                .statusCode(200)
                .body("id", equalTo(userID))
                .body("name", equalTo("Rumeysa Bakir"))
        ;
    }

    //TODO
    @Test(dependsOnMethods = "updateUser")
    public void deleteUser() {
        given()
                .spec(reqSpec)
                .when()
                .delete(""+userID)

                .then()
                .log().all()
                .statusCode(204)
                ;




    }

    //TODO
    @Test(dependsOnMethods = "deleteUser")
    public void deleteUserNegative() {
        given()
                .spec(reqSpec)
                .when()
                .delete(""+userID)

                .then()
                .log().all()
                .statusCode(404)
        ;


    }


}
