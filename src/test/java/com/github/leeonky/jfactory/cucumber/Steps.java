package com.github.leeonky.jfactory.cucumber;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class Steps {
    private IntegrationTestContext integrationTestContext = new IntegrationTestContext();

    @Given("the following bean class:")
    public void the_following_bean_class(String classCode) {
        integrationTestContext.givenBean(classCode);
    }

    @Given("the following spec class:")
    public void the_following_spec_class(String specClass) {
        integrationTestContext.specClass(specClass);
    }

    @Then("the result should:")
    public void the_result_should(String dal) {
        integrationTestContext.verifyBean(dal);
    }

    @When("create:")
    public void create(String builderSnippet) {
        integrationTestContext.create(builderSnippet);
    }

    @And("register:")
    public void register(String factorySnippet) {
        integrationTestContext.register(factorySnippet);
    }

    @When("execute:")
    public void execute(String exeSnippet) {
        integrationTestContext.execute(exeSnippet);
    }
}
