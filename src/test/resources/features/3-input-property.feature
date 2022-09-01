Feature: input property

  Rule: input properties for sub object

    Scenario: support multi properties in nested property creation and query
      Given the following bean class:
      """
      public class Bean {
        public String value1, value2;
      }
      """
      And the following bean class:
      """
      public class BeanWrapper {
        public Bean bean;
      }
      """
      And the following bean class:
      """
      public class BeanWrapperWrapper {
        public BeanWrapper beanWrapper;
      }
      """
      When build:
      """
      jFactory.type(BeanWrapperWrapper.class)
        .property("beanWrapper.bean.value1", "hello")
        .property("beanWrapper.bean.value2", "world")
        .create();
      """
      Then the result should:
      """
      beanWrapper.bean= {
        value1= hello
        value2= world
      }
      """
      And operate:
      """
      jFactory.type(BeanWrapperWrapper.class)
        .property("beanWrapper.bean.value1", "hello")
        .property("beanWrapper.bean.value2", "world")
        .create();
      """
      When build:
      """
      jFactory.type(BeanWrapperWrapper.class)
        .property("beanWrapper.bean.value1", "hello")
        .property("beanWrapper.bean.value2", "world")
        .query();
      """
      Then the result should:
      """
      beanWrapper.bean= {
        value1= hello
        value2= world
      }
      """

    Scenario: support specify trait and spec
      Given the following bean class:
      """
      public class Product {
        public String status, name;
        public int price;
      }
      """
      And the following bean class:
      """
      public class Store {
        public Product product;
      }
      """
      And the following spec class:
      """
      public class Computer extends Spec<Product> {
        @Override
        public void main() {
          property("name").value("computer");
        }

        @Trait
        public void Broken() {
          property("status").value("broken");
        }
      }
      """
      When build:
      """
      jFactory.type(Store.class).property("product(Broken Computer).price", 100).create();
      """
      Then the result should:
      """
      product= {
        price= 100
        name= computer
        status= broken
      }
      """

    Scenario: merge with has spec and no spec
      Given the following bean class:
      """
      public class Product {
        public String status, name;
        public int price;
      }
      """
      And the following bean class:
      """
      public class Store {
        public Product product;
      }
      """
      And the following spec class:
      """
      public class Computer extends Spec<Product> {
        @Override
        public void main() {
          property("name").value("computer");
        }
      }
      """
      When build:
      """
      jFactory.type(Store.class)
        .property("product(Computer).price", 100)
        .property("product.status", "new")
        .create();
      """
      Then the result should:
      """
      product= {
        price= 100
        name= computer
        status= new
      }
      """

#  TODO support merge trait
#    Scenario: merge trait
#      Given the following bean class:
#      """
#      public class Product {
#        public String status, name, color, weight;
#        public int price;
#      }
#      """
#      And the following bean class:
#      """
#      public class Store {
#        public Product product;
#      }
#      """
#      And the following spec class:
#      """
#      public class Computer extends Spec<Product> {
#        @Override
#        public void main() {
#          property("name").value("computer");
#        }
#        @Trait
#        public void Broken() {
#          property("status").value("broken");
#        }
#        @Trait
#        public void Red() {
#          property("color").value("red");
#        }
#      }
#      """
#      When build:
#      """
#      jFactory.type(Store.class)
#        .property("product(Broken Computer).price", 100)
#        .property("product(Red Computer).weight", "5Kg")
#        .create();
#      """
#      Then the result should:
#      """
#      product= {
#        price= 100
#        name= computer
#        status= new
#        color= red
#        weight= '5Kg'
#      }
#      """
