import Model.Location;
import Model.Place;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class ZippoTest {

    @Test
    public void test(){

        given()
                // Hazırlık işlemleri : (token,send body, parametreler)

                .when()
                // endpoint (url), metodu

                .then()
        // assertion, test, data işlemleri
        ;
    }

    @Test
    public void statusCodeTest(){

        given()

                .when()
                .get("http://api.zippopotam.us/us/90210")

                .then()
                .log().body()    // dönen body json datat sı,   log.all()
                .statusCode(200) // dönüş kodu 200 mü
        ;
    }


    @Test
    public void contentTypeTest(){

        given()

                .when()
                .get("http://api.zippopotam.us/us/90210")

                .then()
                .log().body()    // dönen body json datat sı,   log.all()
                .statusCode(200) // dönüş kodu 200 mü
                .contentType(ContentType.JSON) // dönen sonuç JSON mı
        ;
    }

    @Test
    public void checkCountryInResponseBody(){


        given()

                .when()
                .get("http://api.zippopotam.us/us/90210")

                .then()
                .log().body()    // dönen body json datat sı,   log.all()
                .statusCode(200) // dönüş kodu 200 mü
                .body("country", equalTo("United States")) // body nin country değişkeni "United States" eşit Mİ

        // pm.response.json().id -> body.id
        ;
    }
//
//    PM                            RestAssured
//    body.country                  body("country")
//    body.'post code'              body("post code")
//    body.places[0].'place name'   body("places[0].'place name'")
//    body.places.'place name'      body("places.'place name'")
//    bütün place nameleri bir arraylist olarak verir
//    https://jsonpathfinder.com/

    @Test
    public void checkstateInResponseBody(){
//body kısmındaki bilgileri nasıl alıyoruz
// body den hemen sonraki elemanlara . diyerek ulaşıyoruz body.country gibi


        given()

                .when()
                .get("http://api.zippopotam.us/us/90210")

                .then()
                //.log().body()
                .statusCode(200)
                .body("places[0].state", equalTo("California"))//
        ;
    }

    @Test
    public void checkHasItemy(){
        given()

                .when()
                .get("http://api.zippopotam.us/tr/01000")

                .then()
                //.log().body()
                .statusCode(200)
                .body("places.'place name'", hasItem("Dörtağaç Köyü"))//has item contains gibi çalışıyo bunun içerisinde bu eleman var mı gibi
        // bütün place name lerin herhangi birinde Dörtağaç Köyü var mı
        ;
    }

    @Test
    public void bodyArrayHasSizeTest(){

        given()

                .when()
                .get("http://api.zippopotam.us/us/90210")

                .then()
                //.log().body()
                .statusCode(200)
                .body("places", hasSize(1))//verdiğimiz pathin uzunluğunu kontrol etmeye yarıyo
        ;
    }

    @Test
    public void combiningTest(){
        given()

                .when()
                .get("http://api.zippopotam.us/us/90210")

                .then()
                //.log().body()
                .statusCode(200)
                .body("places", hasSize(1))  // size ı 1 mi
                .body("places.state", hasItem("California")) // verilen path deki list bu item e sahip mi
                .body("places[0].'place name'", equalTo("Beverly Hills")) // verilen path deki değer buna eşit mi
        ;
    }


    @Test
    public void pathParamTest()
    {
        given()
                .pathParam("ulke","us")
                .pathParam("postaKod", 90210)
                .log().uri() // request Link

                .when()
                .get("http://api.zippopotam.us/{ulke}/{postaKod}")
                //url yi böyle {ulke}/{postaKod} parametre şeklinde vermeye pathParam deniliyo

                .then()
                .statusCode(200)
        //.log().body()
        ;
    }

    @Test
    public void queryParamTest() {
        // https://gorest.co.in/public/v1/users?page=3

        given()
                .param("page",1)  // ?page=1  şeklinde linke ekleniyor
                .log().uri() // request Link

                .when()
                .get("https://gorest.co.in/public/v1/users")  // ?page=1

                .then()
                .statusCode(200)
                .log().body()
        ;
    }

    @Test
    public void queryParamTest2()
    {
        // https://gorest.co.in/public/v1/users?page=3
        // bu linkteki 1 den 10 kadar sayfaları çağırdığınızda response daki donen page degerlerinin
        // çağrılan page nosu ile aynı olup olmadığını kontrol ediniz.

        for (int i = 1; i < 10; i++) {
            given()
                    .param("page",i)  // ?page=1  şeklinde linke ekleniyor
                    .log().uri() // request Link

                    .when()
                    .get("https://gorest.co.in/public/v1/users")  // ?page=1

                    .then()
                    .statusCode(200)
                    .log().body()
                    .body("meta.pagination.page", equalTo(i))
            ;
        }

    }


    RequestSpecification requestSpec;
    ResponseSpecification responseSpec;

    @BeforeClass
    public void Setup(){

        baseURI = "https://gorest.co.in/public/v1";

        requestSpec = new RequestSpecBuilder()
                .log(LogDetail.URI)
                .setContentType(ContentType.JSON)
                .build();

        responseSpec = new ResponseSpecBuilder()
                .expectContentType(ContentType.JSON)
                .expectStatusCode(200)
                .log(LogDetail.BODY)
                .build();
    }



    @Test
    public void requestResponseSpecificationn()
    {
        // https://gorest.co.in/public/v1/users?page=3

        given()
                .param("page",1)  // ?page=1  şeklinde linke ekleniyor
                .spec(requestSpec)

                .when()
                .get("/users")  // ?page=1

                .then()
                .spec(responseSpec)
        ;
    }

    @Test
    public void extractingJsonPath(){

        String countryName=
                given()
                        .when()
                        .get("http://api.zippopotam.us/us/90210")

                        .then()
                        .log().body()
                        .extract().path("country")
                ;

        System.out.println("countryName = " + countryName);
        Assert.assertEquals(countryName,"United States");
    }

    @Test
    public void extractingJsonPath2() {
        //placeName
        String placeName=
                given()
                        .when()
                        .get("http://api.zippopotam.us/us/90210")

                        .then()
                        .log().body()
                        .extract().path("places[0].'place name'")  // places[0]['place name']
                ;

        System.out.println("placeName = " + placeName);
        Assert.assertEquals(placeName,"Beverly Hills");
    }

    @Test
    public void extractingJsonPath3() {
        // https://gorest.co.in/public/v1/users  dönen değerdeki limit bilgisini yazdırınız.

        int limit=
                given()
                        .when()
                        .get("https://gorest.co.in/public/v1/users ")

                        .then()
                      //  .log().body()
                        .statusCode(200)
                        .extract().path("meta.pagination.limit")
                ;
        System.out.println("limit: "+ limit);


    }

    @Test
    public void extractingJsonPath4() {
        // https://gorest.co.in/public/v1/users  dönen değerdeki bütün idleri yazdırınız.

        List<Integer> idler=
                given()

                        .when()
                        .get("https://gorest.co.in/public/v1/users")

                        .then()
                        .statusCode(200)
                        .extract().path("data.id"); // bütün id leri ver
        ;

        System.out.println("idler = " + idler);
    }

    @Test
    public void extractingJsonPath5() {
        // https://gorest.co.in/public/v1/users  dönen değerdeki bütün name lei yazdırınız.

        List<String> names=
                given()

                        .when()
                        .get("https://gorest.co.in/public/v1/users")

                        .then()
                        .statusCode(200)
                        .extract().path("data.name"); // bütün id leri ver
        ;

        System.out.println("names = " + names);
    }

    @Test
    public void extractingJsonPathResponsAll(){
        // https://gorest.co.in/public/v1/users  dönen değerdeki bütün name lei yazdırınız.

        Response donenData =
                given()

                        .when()
                        .get("https://gorest.co.in/public/v1/users")

                        .then()
                        .statusCode(200)
                        // .log().body()
                        .extract().response(); // dönen tüm datayı verir.
        ;

        List<Integer> idler= donenData.path("data.id");
        List<String> names= donenData.path("data.name");
        int limit= donenData.path("meta.pagination.limit");

        System.out.println("idler = " + idler);
        System.out.println("names = " + names);
        System.out.println("limit = " + limit);

        Assert.assertTrue(names.contains("Dakshayani Pandey"));
        Assert.assertTrue(idler.contains(1203767));
        Assert.assertEquals(limit, 10, "test sonucu hatalı");
    }


    @Test
    public void extractJsonAll_POJO()
    {  // POJO: json nesnesi
        Location locationNesnesi=
                given()
                        .when()
                        .get("http://api.zippopotam.us/us/90210")

                        .then()
                        //.log().body()
                        .extract().body().as(Location.class)
                // // Location şablonuna
                ;

        System.out.println("locationNesnesi.getCountry() = " +
                locationNesnesi.getCountry());

        for(Place p: locationNesnesi.getPlaces())
            System.out.println("p = " + p);

        System.out.println(locationNesnesi.getPlaces().get(0).getPlacename());
    }

    @Test
    public void extractPOJO_Soru(){
        // aşağıdaki endpointte Dörtağaç köyüne ait diğer bilgileri yazdırınız

        Location adana=
        given()

                .when()
                .get("http://api.zippopotam.us/tr/01000")

                .then()
                //.log().body()
                .statusCode(200)
                .extract().body().as(Location.class)
        ;
        for (Place p: adana.getPlaces())
            if (p.getPlacename().equalsIgnoreCase("Dörtağaç Köyü")){
                System.out.println("p="+p);
            }
    }





    }















