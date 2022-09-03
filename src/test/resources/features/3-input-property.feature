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

    Scenario: merge trait
      Given the following bean class:
      """
      public class Product {
        public String status, name, color, weight;
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
        @Trait
        public void Red() {
          property("color").value("red");
        }
      }
      """
      When build:
      """
      jFactory.type(Store.class)
        .property("product(Broken Computer).price", 100)
        .property("product(Red Computer).weight", "5Kg")
        .create();
      """
      Then the result should:
      """
      product= {
        price= 100
        name= computer
        status= broken
        color= red
        weight= '5Kg'
      }
      """

    Scenario: to not allow merge different spec
      Given the following bean class:
      """
      public class Product {
        public String status, name, color, weight;
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
      }
      """
      And the following spec class:
      """
      public class Book extends Spec<Product> {
      }
      """
      When build:
      """
      jFactory.type(Store.class)
        .property("product(Computer).price", 100)
        .property("product(Book).price", "10")
        .create();
      """
      Then should raise error:
      """
      message= 'Cannot merge different spec `Book` and `Computer` for src.test.Store.product'
      """

  Rule: collection property

    Scenario: support input collection element property
      Given the following bean class:
      """
      public class Bean {
        public String[] strings;
      }
      """
      When build:
      """
      jFactory.type(Bean.class).property("strings[0]", "hello").create();
      """
      Then the result should:
      """
      strings= [ hello ]
      """

    Scenario: generate default value for un specified collection element
      Given the following bean class:
      """
      public class Bean {
        public String[] strings;
      }
      """
      When build:
      """
      jFactory.type(Bean.class).property("strings[1]", "hello").create();
      """
      Then the result should:
      """
      strings= [ 'strings#1[0]' hello ]
      """

    Scenario: generate default enum value for un specified collection element
      Given the following bean class:
      """
      public class Bean {
        public Enums[] enums;
        public enum Enums {
          A, B
        }
      }
      """
      When build:
      """
      jFactory.type(Bean.class).property("enums[1]", "A").create();
      """
      Then the result should:
      """
      enums: [ A A ]
      """

    Scenario: default value of bean type of collection element is null
      Given the following bean class:
      """
      public class Bean {
        public String value;
      }
      """
      And the following bean class:
      """
      public class Beans {
        public Bean[] beans;
      }
      """
      When build:
      """
      jFactory.type(Beans.class).property("beans[1].value", "hello").create();
      """
      Then the result should:
      """
      beans= [null {value= hello}]
      """

    Scenario: merge property in collection element
      Given the following bean class:
      """
      public class Bean {
        public String value1, value2;
      }
      """
      And the following bean class:
      """
      public class Beans {
        public Bean[] beans;
      }
      """
      When build:
      """
      jFactory.type(Beans.class)
        .property("beans[0].value1", "hello")
        .property("beans[0].value2", "world")
        .create();
      """
      Then the result should:
      """
      beans= [{
        value1= hello
        value2= world
      }]
      """

    Scenario: use spec and trait in collection element
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
        public Product[] products;
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
        jFactory.type(Store.class).property("products[0](Broken Computer).price", 100).create();
      """
      Then the result should:
      """
      products= [{
        price= 100
        name= computer
        status= broken
      }]
      """

    Scenario: different type of element property
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
        public Product[] products;
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
      jFactory.type(Store.class)
        .property("products[0](Broken Computer).price", 100)
        .property("products[1]", new Product(){{
          this.name = "book";
        }}).create();
      """
      Then the result should:
      """
      products: [{
        price= 100
        name= computer
        status= broken
      } {
        name= book
      }]
      """

    Scenario: override input property
      Given the following bean class:
      """
      public class Bean {
        public String value;
      }
      """
      And the following bean class:
      """
      public class Beans {
        public Bean[] beans;
      }
      """
      And the following bean class:
      """
      public class BeansList {
        public Beans[] beansList;
      }
      """
      When build:
      """
      jFactory.type(Beans.class)
        .property("beans[0]", null)
        .property("beans[0].value", "hello")
      .create();
      """
      Then the result should:
      """
      beans.value[]: [ hello ]
      """
      When build:
      """
      jFactory.type(Beans.class)
        .property("beans[0].value", "any string")
        .property("beans[0]", null)
      .create();
      """
      Then the result should:
      """
      beans: [ null ]
      """
      When build:
      """
      jFactory.type(BeansList.class)
        .property("beansList[0].beans[0].value", "any string")
        .property("beansList[0].beans", null)
      .create();
      """
      Then the result should:
      """
      beansList.beans[]: [ null ]
      """

  Rule: intently create

    Scenario: intently create sub object should always create object evan repo has one matched data
      Given the following bean class:
      """
      public class Bean {
        public String value;
      }
      """
      And the following bean class:
      """
      public class BeanWrapper {
        public Bean bean;
      }
      """
      When operate:
      """
      jFactory.type(BeanWrapper.class).property("bean.value", "hello").create();
      jFactory.type(BeanWrapper.class).property("bean!.value", "hello").create();
      """
      Then "jFactory.type(Bean.class).queryAll()" should
      """
      ::size= 2
      """

    Scenario: intently create sub object with null
      Given the following bean class:
      """
      public class Bean {
        public String value;
      }
      """
      And the following bean class:
      """
      public class BeanWrapper {
        public Bean bean;
      }
      """
      When operate:
      """
      jFactory.type(BeanWrapper.class).property("bean!.value", null).create();
      jFactory.type(BeanWrapper.class).property("bean!.value", null).create();
      """
      Then "jFactory.type(Bean.class).queryAll()" should
      """
      ::size= 2
      """

    Scenario: intently query should always return empty
      Given the following bean class:
      """
      public class Bean {
        public String value;
      }
      """
      And the following bean class:
      """
      public class BeanWrapper {
        public Bean bean;
      }
      """
      When operate:
      """
      jFactory.type(BeanWrapper.class).property("bean.value", "hello").create();
      """
      When build:
      """
      jFactory.type(BeanWrapper.class).property("bean!.value", "hello").queryAll();
      """
      Then the result should:
      """
      : []
      """

    Scenario: intently create can ignore
      Given the following bean class:
      """
      public class Bean {
        public String value;
      }
      """
      And the following bean class:
      """
      public class BeanWrapper {
        public Bean bean;
      }
      """
      And the following spec class:
      """
      @Global
      public class ABean extends Spec<Bean> {
        @Override
        public void main() {
          property("value").value("hello");
        }
      }
      """
      When build:
      """
      jFactory.type(BeanWrapper.class).property("bean(ABean)!", null).create();
      """
      Then the result should:
      """
      bean.value= hello
      """

    Scenario: should intently create when property has at least one intently flag
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
      When operate:
      """
      jFactory.type(BeanWrapper.class)
        .property("bean!.value1", "hello")
        .property("bean.value2", "world")
        .create();
      jFactory.type(BeanWrapper.class)
        .property("bean.value1", "hello")
        .property("bean!.value2", "world")
        .create();
      """
      Then "jFactory.type(Bean.class).queryAll()" should
      """
      ::size= 2
      """

  Rule: try to use a spec

    Scenario: should try use a spec in parent spec and override property when input and keep the value in spec which not specified from input property
      Given the following bean class:
      """
      public class Product {
        public String name, color;
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
      public class DefaultProduct extends Spec<Product> {
        @Override
        public void main() {
          property("name").value("product");
        }
        @Trait
        public void Red() {
          property("color").value("red");
        }
      }
      """
      And the following spec class:
      """
      public class AStore extends Spec<Store> {
        @Override
        public void main() {
          property("product").from(DefaultProduct.class).which(spec -> spec.Red());
        }
      }
      """
      When build:
      """
      jFactory.spec(AStore.class).property("product.price", "100").create();
      """
      Then the result should:
      """
      product= {
        name= product
        color= red
        price= 100
      }
      """

    Scenario: input property could override spec and trait in parent spec
      Given the following bean class:
      """
      public class Product {
        public String name, color;
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
      public class DefaultProduct extends Spec<Product> {
        @Override
        public void main() {
          property("name").value("product");
        }
        @Trait
        public void Red() {
          property("color").value("red");
        }
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
        public void Black() {
          property("color").value("black");
        }
      }
      """
      And the following spec class:
      """
      public class AStore extends Spec<Store> {
        @Override
        public void main() {
          property("product").from(DefaultProduct.class).which(spec -> spec.Red());
        }
      }
      """
      When build:
      """
      jFactory.spec(AStore.class).property("product(Black Computer).price", "100").create();
      """
      Then the result should:
      """
      product= {
        name= computer
        color= black
        price= 100
      }
      """

    Scenario: spec in property chain
      Given the following bean class:
      """
      public class Product {
        public String name, color;
        public int price;
      }
      """
      And the following bean class:
      """
      public class Store {
        public Product product;
      }
      """
      And the following bean class:
      """
      public class StoreGroup {
        public Store store;
      }
      """
      And the following spec class:
      """
      public class DefaultProduct extends Spec<Product> {
        @Override
        public void main() {
          property("name").value("product");
        }
        @Trait
        public void Red() {
          property("color").value("red");
        }
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
        public void Black() {
          property("color").value("black");
        }
      }
      """
      And the following spec class:
      """
      public class AStore extends Spec<Store> {
        @Override
        public void main() {
          property("product").from(DefaultProduct.class).which(spec -> spec.Red());
        }
      }
      """
      When build:
      """
      jFactory.type(StoreGroup.class).property("store(AStore).product(Black Computer).price", "100").create();
      """
      Then the result should:
      """
      store.product= {
        name= computer
        color= black
        price= 100
      }
      """
