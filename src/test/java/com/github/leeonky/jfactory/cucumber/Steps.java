package com.github.leeonky.jfactory.cucumber;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class Steps {
    private IntegrationTestContext integrationTestContext;

    @Before
    public void reset() {
        integrationTestContext = new IntegrationTestContext();
    }

    @After
    public void releaseCompiler() {
        integrationTestContext.releaseCompiler();
    }


    @Given("the following bean class:")
    public void the_following_bean_class(String classCode) {
        integrationTestContext.givenBean(classCode);
    }

    @Given("the following spec class:")
    public void the_following_spec_class(String specClass) {
        integrationTestContext.specClass(specClass);
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

    @And("register:")
    public void register(String factorySnippet) {
        integrationTestContext.register(factorySnippet);
    }

    @And("operate:")
    public void operate(String operateSnippet) {
        integrationTestContext.register(operateSnippet);
    }

    @When("build:")
    public void build(String builderSnippet) {
        integrationTestContext.build(builderSnippet);
    }

    @Then("{string} should")
    public void should(String code, String dal) throws Throwable {
        integrationTestContext.build(code + ";");
        integrationTestContext.verify(dal);
    }

    @Then("the result should:")
    public void the_result_should(String dal) throws Throwable {
        integrationTestContext.verify(dal);
    }

    @Then("should raise error:")
    public void shouldRaiseError(String dal) {
        integrationTestContext.shouldThrow(dal);
    }
}
