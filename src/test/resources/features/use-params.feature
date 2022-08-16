Feature: create with params

  Scenario: create bean with params
    Given the following bean class:
    """
    public class Bean {
      public String str1, str2;
    }
    """
    And register factory:
    """
    jfactory.factory(Bean.class).spec(instance -> instance.spec()
      .property("str1").value((Object) instance.param("p1"))
      .property("str2").value((Object) instance.param("p2"))
    );
    """
    When create type "Bean" with params
      | p1  |
      | foo |
    Then the result should:
    """
    str1= foo
    """
    When create type "Bean" with params
      | p1    | p2    |
      | hello | world |
    Then the result should:
    """
    = {
      str1= hello
      str2= world
    }
    """

  Scenario: support default params
    Given the following bean class:
    """
    public class Bean {
      public String str1, str2;
    }
    """
    And register factory:
    """
    jfactory.factory(Bean.class).spec(instance -> instance.spec()
      .property("str1").value((Object) instance.param("p1", "default1"))
      .property("str2").value((Object) instance.param("p2", "default2"))
    );
    """
    When create type "Bean"
    Then the result should:
    """
    = {
      str1= default1
      str2= default2
    }
    """
