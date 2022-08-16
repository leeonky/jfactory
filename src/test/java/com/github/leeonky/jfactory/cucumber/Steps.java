package com.github.leeonky.jfactory.cucumber;

import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyMap;

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

    @Given("the following spec class:")
    public void the_following_spec_class(String specClass) {
        integrationTestContext.specClass(specClass);
    }

    @Then("the result should:")
    public void the_result_should(String dal) {
        integrationTestContext.verifyBean(dal);
    }

    @When("create type {string} with traits {string}")
    public void create_type_with_traits(String type, String traits) {
        integrationTestContext.create(type, traits.split(" "), emptyMap());
    }

    @When("create {string}")
    public void create(String specAndTrait) {
        integrationTestContext.createSpec(specAndTrait.split(" "));
    }

    @When("create from spec {string} with:")
    public void create_from_spec_with(String spec, String traitSnippet) {
        integrationTestContext.createSpecWithSnippet(spec, traitSnippet);
    }

    @And("create {string} with property:")
    public void createWithProperty(String specTraits, List<Map<String, String>> properties) {
        integrationTestContext.createSpec(specTraits, properties.get(0));
    }

    @When("create type {string}")
    public void createType(String type) {
        integrationTestContext.create(type, new String[0], emptyMap());
    }

    @When("create type {string} with property:")
    public void createTypeWithProperty(String type, List<Map<String, String>> properties) {
        integrationTestContext.create(type, new String[0], properties.get(0));
    }

    @And("register factory:")
    public void registerFactory(String registerCode) {
        integrationTestContext.registerJfactory(registerCode);
    }

    @When("create type {string} with params")
    public void createTypeWithParams(String type, List<Map<String, String>> params) {
        integrationTestContext.create(type, new String[0], emptyMap(), params.get(0));
    }
}
