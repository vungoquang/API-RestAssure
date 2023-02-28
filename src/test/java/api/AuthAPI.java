package api;

import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;
import io.restassured.RestAssured;
import io.restassured.config.SSLConfig;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

import io.restassured.response.ValidatableResponse;
import org.hamcrest.Matchers;

import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;

public class AuthAPI {

    /**
     * request Verify Email
     * @param email
     * @return response
     */
    public static Response requestVerifyEmail(String email) throws JSONException {
        JSONObject data = new JSONObject();
        data.put("email",email);
        RestAssured.config = RestAssured.config().sslConfig(SSLConfig.sslConfig().allowAllHostnames());
        Response response = given().basePath("request-verify-email")
                .contentType("application/json")
                .body(data.toString())
                .when().log().all()
                .post();
        response.then().log().all();
        // Getting ValidatableResponse time
        ValidatableResponse valRes = response.then();
        valRes.time(Matchers.lessThan(10L), TimeUnit.SECONDS);
        return response;
    }

    /**
     * verify email with token
     * @param email
     * @param token
     * @return response
     */
    public static Response verifyEmail(String email, String token) throws JSONException {
        JSONObject data = new JSONObject();
        data.put("email", email);
        data.put("token", token);
        RestAssured.config = RestAssured.config().sslConfig(SSLConfig.sslConfig().allowAllHostnames());
        Response response = given().basePath("verify-email")
                .contentType("application/json")
                .body(data.toString())
                .when().log().all()
                .post();
        response.then().log().all();
        // Getting ValidatableResponse time
        ValidatableResponse valRes = response.then();
        valRes.time(Matchers.lessThan(10L), TimeUnit.SECONDS);
        return response;
    }

    /**
     * register Mavis account
     * @param email
     * @param password
     * @return
     */
    public static Response register(String email, String password) throws JSONException {
        JSONObject data = new JSONObject();
        data.put("password",password);
        data.put("email",email);
        RestAssured.config = RestAssured.config().sslConfig(SSLConfig.sslConfig().allowAllHostnames());
        Response response = given().basePath("register")
                .contentType("application/json")
                .body(data.toString())
                .when().log().all()
                .post();
        response.then().log().all();
        // Getting ValidatableResponse time
        ValidatableResponse valRes = response.then();
        valRes.time(Matchers.lessThan(10L), TimeUnit.SECONDS);
        return response;
    }

}
