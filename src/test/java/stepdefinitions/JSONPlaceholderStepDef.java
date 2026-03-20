package stepdefinitions;

import api.enumtypes.ResouceLinks;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.logging.log4j.Logger;
import utils.LogHelper;
import utils.RequestFactory;
import api.enumtypes.JsonPayload;
import api.payloads.Payload;
import utils.RuntimeStore;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertEquals;

/** Step Definitions for JSONPlaceholder API automation Encapsulates CRUD operations,
 * schema validation, response verification, and runtime memory handling*/

public class JSONPlaceholderStepDef {

    private static final Logger logger = LogHelper.getLogger(JSONPlaceholderStepDef.class);
    private RequestSpecification requestSpec = RequestFactory.getRequest();
    private Response response;
    private Payload payload;
    private RequestSpecification request;

    /** Prepares request payload with optional override values from DataTable */
    @Given("user adds {string} request payload with format {string}")
    public void userAddsRequestPayload(String payloadName, String format, DataTable dataTable) {
        Map<String, Object> overridevalues = dataTable.asMap(String.class, Object.class);
        overridevalues = utils.Injector.autoInject(overridevalues);
        payload = JsonPayload.valueOf(payloadName).getPayloadInstance();
        payload.overrideValues(overridevalues);
        request = given().spec(requestSpec).body(payload.getBody());
    }

    /** Executes the HTTP request against specified resource with given method */
    @When("user hits {string} resource with http method {string}")
    public void hitResource(String resourceName,String httpMethodName)
    {
        String resourceLink = ResouceLinks.Resource.valueOf(resourceName.toUpperCase()).getResourceName();
        logger.info(resourceLink);

        resourceLink = utils.Injector.pathInject(resourceLink);
        logger.info(resourceLink);

        /**Ensure request is never null*/
        if (request == null) {
            logger.info("Request was null, initializing with base RequestSpecification");
            request = given().spec(requestSpec);
        }

        try {
            response = request.when().request(httpMethodName, resourceLink);
        } catch (Exception e) {
            logger.warn("Request failed: " + e.getMessage());
            response = null;
        }

    }

    /** Validates HTTP response status code */
    @Then("verify status code is {int}")
    public void verifyStatusCode(int expectedStatusCode) {
        if (response == null) {
            logger.warn("Response is null. Cannot verify status code.");
            // Fail the test explicitly for safety
            assertEquals("Response was null, cannot verify status code.", expectedStatusCode, response.getStatusCode());
        } else {
            int actualStatus = response.getStatusCode();
            logger.info("Actual Status Code: " + actualStatus);
            assertEquals(expectedStatusCode, actualStatus);
        }
    }

    /** Stores a value from response JSON into runtime store */
    @And("store {string} as {string} in global memory")
    public void storeRuntime(String responseKey, String storedKey)
    {
        String responseValue = response.jsonPath().getString(responseKey);
        logger.info(responseValue);
        //stored value into RuntimeStore
        RuntimeStore.put(storedKey,responseValue);
    }

    /** Ensures a key exists in runtime store */
    @Given("user has {string} stored in global memory")
    public void userHasStoredValue(String storedKey) {
        Object value = RuntimeStore.get(storedKey);
        if(value == null) {
            throw new RuntimeException("Key '" + storedKey + "' not found in RuntimeStore");
        }
        logger.info("Loaded value from RuntimeStore: {}={}", storedKey, value);
    }

    /** Validates a response header contains expected value */
    @And("verify response header {string} contains {string}")
    public void verifyResponseHeader(String headerName, String expectedValue) {
        String actualHeader = response.getHeader(headerName);
        logger.info("Header {} = {}", headerName, actualHeader);
        assertEquals(true, actualHeader.contains(expectedValue));
    }

    /** Validates response time is below threshold */
    @And("verify response time is less than {int} ms")
    public void verifyResponseTime(int expectedTime) {
        long actualTime = response.getTime();
        logger.info("Response Time: " + actualTime);
        assertEquals(true, actualTime < expectedTime);
    }

    /** Stores provided ID into runtime store for later use */
    @Given("user want to get data for id {string} in global memory as {string}")
    public void userWantToGetDataForId(String idValue, String key) {
        RuntimeStore.put(key, idValue);
        logger.info("Stored in RuntimeStore: {} = {}", key, idValue);
    }

    /** Verifies a single field in JSON response */
    @And("verify response field {string} is {string}")
    public void verifyResponseField(String key, String expectedValue) {
        String actualValue = response.jsonPath().getString(key);
        logger.info("Field {} = {}", key, actualValue);
        assertEquals(expectedValue, actualValue);
    }

    /** Verifies a multiline field in JSON response */
    @And("verify response field {string} is:")
    public void verifyResponseFieldMultiline(String key, String expectedValue) {
        String actualValue = response.jsonPath().getString(key);

        String normalizedExpected = expectedValue.trim();
        String normalizedActual = actualValue.trim();

        logger.info("Expected: " + normalizedExpected);
        logger.info("Actual: " + normalizedActual);

        assertEquals(normalizedExpected, normalizedActual);
    }

    /** Validates response JSON matches schema file */
    @Then("verify response matches {string}")
    public void verifyResponseMatchesSchema(String schemaName) {

        if (response == null) {
            throw new RuntimeException("Response is null. Cannot validate schema.");
        }

        response.then().assertThat().body(matchesJsonSchemaInClasspath("schemas/" + schemaName + ".json"));
        logger.info("Schema validated successfully for: " + schemaName);
    }

    /** Validates response list size exceeds expected value */
    @And("verify response list size is greater than {int}")
    public void verifyResponseListSize(int expectedSize) {

        if (response == null) {
            throw new RuntimeException("Response is null. Cannot validate list size.");
        }

        int actualSize = response.jsonPath().getList("$").size();
        logger.info("Actual list size: " + actualSize);
        assertTrue(actualSize > expectedSize, "List size validation failed");
    }
}