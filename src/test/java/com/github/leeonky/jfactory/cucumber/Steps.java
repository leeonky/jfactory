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
    public void the_result_should(String dal) throws Throwable {
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

    @When("query:")
    public void query(String builderSnippet) {
        integrationTestContext.query(builderSnippet);
    }

    @When("query all:")
    public void queryAll(String builderSnippet) {
        integrationTestContext.queryAll(builderSnippet);
    }

    @And("operate:")
    public void operate(String operateSnippet) {
        integrationTestContext.operate(operateSnippet);
    }

    @Then("should raise error:")
    public void shouldRaiseError(String dal) {
        integrationTestContext.shouldThrow(dal);
    }

    @When("create as:")
    public void createAs(String createAs) {
        integrationTestContext.createAs(createAs);
    }

    @When("build:")
    public void build(String builderSnippet) {
        integrationTestContext.build(builderSnippet);
    }

    @Given("declaration jFactory =")
    public void declarationJFactory(String declaration) {
        integrationTestContext.declare(declaration);
    }

    @Given("declaration list =")
    public void declarationList(String listDeclaration) {
        integrationTestContext.declareList(listDeclaration);
    }

    @Then("the list in repo should:")
    public void theListInRepoShould(String dal) {
        integrationTestContext.listShould(dal);
    }
}
