import GoRest.User;
import Model.Location;
import Model.Place;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static io.restassured.RestAssured.*;

public class PathAndJsonPath {

    @Test
    public void extractingPath() {
        // "post code": "90210",

        int postCode =
                given()

                        .when()
                        .get("http://api.zippopotam.us/us/90210")

                        .then()
                        .log().body()
                        .extract().path("'post code'");
        ;
/** postcode int e çevirdiğimiz için hata verdi path kısmına neyse onu vermen lazım stringi inte çevirmezsin */
        System.out.println("postCode = " + postCode);
    }

    @Test
    public void extractingJosnPath() {
        // "post code": "90210",
        int postCode =
                given()

                        .when()
                        .get("http://api.zippopotam.us/us/90210")

                        .then()
                        .log().body()
                        .extract().jsonPath().getInt("'post code'")  /** getint dediğin zaman kendisi çevirip veriyo hata verme şansını azaltıyo     */
               /** "'post code'" burada arada boşulk olduğu için tırnakları öyle kullandık   */
                // tip dönüşümü otomatik, uygun tip verilmeli
                ;
        System.out.println("postCode = " + postCode);
    }

    @Test
    public void getUsers() {
        Response response =
                given()

                        .when()
                        .get("https://gorest.co.in/public/v2/users")

                        .then()
                        //.log().body()
                        .extract().response();
        ;

        int idPath = response.path("[2].id");
        int idJsonPath = response.jsonPath().getInt("[2].id");

        System.out.println("idPath = " + idPath);
        System.out.println("idJsonPath = " + idJsonPath);

        User[] usersPath= response.as(User[].class); // as nesne dönüşümünde (POJO) dizi destekli
        List<User> usersJsonPath= response.jsonPath().getList("", User.class); // JsonPath ise List olarak verebiliyor

        System.out.println("usersPath = " + Arrays.toString(usersPath)); // path de arrays olmadan listeleyemyoruz
        System.out.println("usersJsonPath = " + usersJsonPath);// ama jsonpath zaten bize listelişekilde verdiği için arrays e burda gerek yok

    }


    @Test
    public void getUsersV1()
    {
        Response body=
                given()

                        .when()
                        .get("https://gorest.co.in/public/v1/users")

                        .then()
                        //.log().body()
                        .extract().response();
        ;

        List<User> dataUsers= body.jsonPath().getList("data", User.class);
        // JSONPATH bir response içindeki bir parçayı nesneye dönüştürebiliriz.
        System.out.println("dataUsers = " + dataUsers);

        // Daha önceki örneklerde (as) Clas dönüşümleri için tüm yapıya karşılık gelen
        // gereken tüm classları yazarak dönüştürmek istediğimiz elemanlara ulaşıyorduk.

        // Burada ise(JsonPath) aradaki bir veriyi clasa dönüştürerek bir list olarak almamıza
        // imkan veren JSONPATH i kullandık.Böylece tek class ile veri alınmış oldu
        // diğer class lara gerek kalmadan

        // path : class veya tip dönüşümüne imkan veremeyen direk veriyi verir. List<String> gibi
        // jsonPath : class dönüşümüne ve tip dönüşümüne izin vererek , veriyi istediğimiz formatta verir.
    }

    @Test
    public void getZipCode(){
        Response response=
                given()

                        .when()
                        .get("http://api.zippopotam.us/us/90210")

                        .then()
                        //.log().body()
                        .extract().response()
                ;

        Location locPathAs = response.as(Location.class); // Bütün classları yazmak zorundasın
        System.out.println("locPathAs.get = " + locPathAs.getPlaces());

        List<Place> places= response.jsonPath().getList("places", Place.class); // nokta atışı istediğimiz nesneyi aldık.
        System.out.println("places = " + places);
    }




}