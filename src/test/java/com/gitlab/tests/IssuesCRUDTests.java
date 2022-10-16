package com.gitlab.tests;

import com.github.javafaker.Faker;
import com.gitlab.models.Issue;
import com.gitlab.restClients.IssuesRestClient;
import com.gitlab.utils.BaseTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

public class IssuesCRUDTests extends BaseTest {

    /**
     * We create an object of class Issue with some properties, then send it by Rest Client.
     * From the response we save issueID which will be used to retrieve a created Issue.
     * Then after receiving issue ID from the server we compare it parameters.
     * Here we partially check READ operation.
     */
    @Test
    public void positiveCreate() {
        Issue issue = Issue.generateRandomIssue();
        issue.setIid(IssuesRestClient.createIssueAndGetId(issue));

        Issue createdIssue = IssuesRestClient.readIssue((int) issue.getIid(),200);
        assertEquals(createdIssue.getTitle(), issue.getTitle(), "Wrong title of the issue");
        assertEquals(createdIssue.getDescription(), issue.getDescription(), "Wrong description of the issue");
        assertEquals(createdIssue.getIssue_type(), issue.getIssue_type(), "Wrong issue type of the issue");
    }

    /**
     * As in previous tests we checked fields in the response of getting issues. Here we check jsonschema.
     * It's inside of readIssueAndCheckJSONSchema method
     */
    @Test
    public void positiveRead() {
        Issue issue = Issue.generateRandomIssue();
        issue.setIid(IssuesRestClient.createIssueAndGetId(issue));
        IssuesRestClient.readIssueAndCheckJSONSchema((int) issue.getIid());
    }

    /**
     * We create an issue object (some values will be randomly generated), then we get it's ID then we create it.
     * We update values of it and send update.
     * By READ issue we a checking then updated object is returned.
     */
    @Test
    public void positiveUpdate() {
        Issue issue = Issue.generateRandomIssue();
        issue.setIid(IssuesRestClient.createIssueAndGetId(issue));

        String updatedTitle = String.format("Updated title %s", new Faker().app().name());
        String updatedDescription = String.format("Updated description %s", new Faker().lorem().fixedString(100));

        issue.setTitle(updatedTitle);
        issue.setDescription(updatedDescription);

        Map<String, String> map = new HashMap<>();
        map.put("title", updatedTitle);
        map.put("description", updatedDescription);

        IssuesRestClient.updateIssue(issue, map);

        //Get from the server updated issue and validate it.
        Issue updatedIssue = IssuesRestClient.readIssue((int) issue.getIid(),200);
        assertEquals(updatedIssue.getTitle(), issue.getTitle(), "Wrong title of the issue");
        assertEquals(updatedIssue.getDescription(), issue.getDescription(), "Wrong description of the issue");
    }

    /**
     * In this test case we test DELETE operation by creating an issue, then we check it appeared in response
     */
    @Test
    public void positiveDelete() {
        Issue issue = Issue.generateRandomIssue();
        issue.setIid(IssuesRestClient.createIssueAndGetId(issue));

        Issue createdIssue = IssuesRestClient.readIssue((int) issue.getIid(), 200);
        assertEquals(createdIssue.getTitle(), issue.getTitle(), "Wrong title of the issue");

        IssuesRestClient.deleteIssue((int) issue.getIid());
        Issue deleteIssue = IssuesRestClient.readIssue((int) issue.getIid(), 404);

        assertNull(deleteIssue.getTitle());
        assertNull(deleteIssue.getDescription());
        assertNull(deleteIssue.getIid());
    }


    @DataProvider(name = "readIssues")
    public Object[][] dataProvider() {
        return new Object [][] {
                {0},
                {1000}
        };
    }

    @Test(dataProvider = "readIssues",priority = 1)
    public void negativeRead(int data) {
        IssuesRestClient.readIssue(data,404);
    }
}