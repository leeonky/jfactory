package com.github.leeonky.jfactory.cucumber;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class Steps {
    private IntegrationTestContext integrationTestContext = new IntegrationTestContext();

    @Before
    public void reset() {
        integrationTestContext = new IntegrationTestContext();
    }

    @Given("the following bean class:")
    public void the_following_bean_class(String classCode) {
        integrationTestContext.givenBean(classCode);
    }

    @Given("spec of type {string}")
    public void spec_of_type(String type, String specCode) {
        integrationTestContext.givenTypeSpec(type, specCode);
    }

    @Given("trait {string} of type {string}")
    public void trait_of_type(String trait, String type, String codeSnippet) {
        integrationTestContext.givenTypeTrait(type, trait, codeSnippet);
    }

    @When("create type {string} with traits {string}")
    public void create_type_with_traits(String type, String traits) {
        integrationTestContext.create(type, traits.split(","));
    }

    @Then("the result should:")
    public void the_result_should(String dal) {
        integrationTestContext.verifyBean(dal);
    }
}
