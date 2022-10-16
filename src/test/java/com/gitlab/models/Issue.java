package com.gitlab.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.javafaker.Faker;
import com.github.javafaker.service.FakeValuesService;
import com.github.javafaker.service.RandomService;
import lombok.*;
import org.joda.time.DateTime;

import java.sql.Time;
import java.util.List;
import java.util.Locale;
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@Data
public class Issue {
    private int assignee_id;
    private int[] assignee_ids;
    private boolean confidential;
    private String created_at;
    private String description;
    private String discussion_to_resolve;
    private String due_date;
    private int epic_id;
    private int epic_iid;
    private Object id;
    private Object iid;
    private String issue_type;
    private List<String> labels;
    private int merge_request_to_resolve_discussions_of;
    private int milestone_id;
    private String title;
    private int weight;
    private int project_id;

    public static Issue generateRandomIssue() {
        Issue issue = new Issue();
        FakeValuesService fakeValuesService = new FakeValuesService(
                new Locale("en-GB"), new RandomService());
        issue.setTitle(fakeValuesService.regexify("ISSUE SUBJECT [a-z1-9]{40}"));
        issue.setDescription(String.format("%s %s",new Faker().lorem().characters(10), DateTime.now()));
        issue.setIssue_type("issue");

        return issue;
    }
}