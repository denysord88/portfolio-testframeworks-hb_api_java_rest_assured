package tests.hb;

import com.homesteadbloom.controllers.BaseController;
import io.restassured.response.ValidatableResponse;
import org.testng.annotations.Test;
import tests.common.BaseTest;

import static io.restassured.RestAssured.given;
import static org.testng.Assert.assertEquals;

public class FirstTest extends BaseTest {
    @Test
    public void firstTest() {
        ValidatableResponse response =
                given(new BaseController().requestSpecification)
                        .when()
                        .get("/auth/me")
                        .then();
        String role = response.extract().jsonPath().getString("role");
        assertEquals(role, "admin", "The role of the user is not admin but " + role);
    }
}
