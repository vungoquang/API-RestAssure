package aut;

import api.AuthAPI;
import com.amazonaws.util.json.JSONException;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import utils.CommonUtils;
import utils.Assertion;
import utils.PropertyUtils;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.Properties;

import static org.hamcrest.Matchers.*;
import static utils.Assertion.*;

public class AuthTC {
    Properties loadData;
    public AuthTC() throws IOException {
        this.loadData= PropertyUtils.readPropertiesFile("src/test/java/data/data.properties");
    }
    @BeforeClass(alwaysRun=true)
    @Parameters({ "baseUrl" })
    public void setUp(String baseUrl) {
        RestAssured.baseURI = baseUrl;
    }

    @Test(priority = 1)
    public void sendRequestVerifyEmail() throws JSONException {
        Response response = AuthAPI.requestVerifyEmail(loadData.getProperty("Email"));
        assertResponseCode("status code is incorrect", response, equalTo(Assertion.APIResponseCode.OK));
        assertThat(response, "message is incorrect", response.path("message"), equalTo("Captcha to verify your email was sent"));
    }


    @Test(priority = 2)
    public void verifyEmail() throws JSONException, InterruptedException, MessagingException, IOException {
        String token= utils.GmailClient.getMFACode(loadData.getProperty("Subject"),loadData.getProperty("Body"),loadData.getProperty("Email"));
        Response response = AuthAPI.verifyEmail(loadData.getProperty("Email"),token);
        assertResponseCode("status code is incorrect", response, equalTo(APIResponseCode.OK));
        assertThat(response, "message is incorrect", response.path("message"), equalTo("Your email has been verified successfully"));

    }

    @Test(priority = 3)
    public void registerAccount() throws JSONException, IOException {
        Response response = AuthAPI.register(loadData.getProperty("Email"),loadData.getProperty("Password"));
        assertResponseCode("status code is incorrect", response, equalTo(APIResponseCode.OK));
        assertThat(response, "accessToken is not returned or null", response.path("accessToken"), is(notNullValue()));
        assertThat(response, "expiredAt is not returned or null", response.path("accessTokenExpiresAt"), is(notNullValue()));
        assertThat(response, "expiredIn is not returned or null", response.path("accessTokenExpiresIn"), is(notNullValue()));
        assertThat(response, "refreshToken is not returned or null", response.path("refreshToken"), is( notNullValue()));
        CommonUtils writeFile= new CommonUtils();
        writeFile.writeJsonfile(response,"src/test/java/data/userInfo.json");
    }
}
