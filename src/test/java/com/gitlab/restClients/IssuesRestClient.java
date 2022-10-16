package com.gitlab.restClients;

import com.gitlab.models.Issue;
import com.gitlab.utils.AppPropeties;
import com.gitlab.utils.BaseTest;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.io.InputStream;
import java.util.Map;

import static io.restassured.RestAssured.expect;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.lessThan;
import static org.testng.Assert.assertEquals;

public class IssuesRestClient {

    private static final int projectId;

    static {
        RestAssured.baseURI = AppPropeties.getProperty("url");
        RestAssured.requestSpecification = given().header("PRIVATE-TOKEN", AppPropeties.getProperty("token"));
        RestAssured.responseSpecification = expect().time(lessThan(10000L));
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        projectId = Integer.parseInt(AppPropeties.getProperty("projectId"));
    }

    @Step
    public static int createIssueAndGetId(Issue issue) {
        RequestSpecification requestSpecification = given().queryParam("title", issue.getTitle());

        if (issue.getDescription() != null) {
            requestSpecification.queryParam("description", issue.getDescription());
        }

        if (issue.getIssue_type() != null) {
            requestSpecification.queryParam("issue_type", issue.getIssue_type());
        }

        Response response = requestSpecification.post(String.format("/projects/%s/issues", projectId));
        assertEquals(response.statusCode(), 201, "Issue hasn't been created");
        BaseTest.attachedInfo(response.asPrettyString());
        return response.jsonPath().get("iid");
    }

    @Step
    public static Issue readIssue(int issueIId, int statusCode) {
        Response response = given().get(String.format("/projects/%s/issues/%s", projectId, issueIId));
        response.then().statusCode(statusCode);
        BaseTest.attachedInfo(response.asPrettyString());
        return response.as(Issue.class);
    }

    @Step
    public static Issue[] getAllIssues() {
        Response response = given().get(String.format("/projects/%s/issues", projectId));
        response.then().statusCode(200);
        return response.as(Issue[].class);
    }

    @Step
    public static void readIssueAndCheckJSONSchema(int issueIId) {
        InputStream responseIssueSchema = IssuesRestClient.class.getClassLoader().getResourceAsStream("responseIssueSchema.json");
        Response response = given().get(String.format("/projects/%s/issues/%s", projectId, issueIId));
        assert responseIssueSchema != null;
        response.then().statusCode(200).and().assertThat().body(JsonSchemaValidator.matchesJsonSchema(responseIssueSchema));
    }

    @Step
    public static void updateIssue(Issue issue, Map<String, String> updateFields) {
        RequestSpecification requestSpecification = given();
        updateFields.forEach(requestSpecification::queryParam);
        Response response = requestSpecification.put(String.format("/projects/%s/issues/%s", projectId, issue.getIid()));
        assertEquals(response.statusCode(), 200, "Issue hasn't been updated");
    }

    @Step
    public static void deleteIssue(int issueIId) {
        given().delete(String.format("/projects/%s/issues/%s", projectId, issueIId)).then().assertThat().statusCode(204);
    }
}
