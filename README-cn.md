# JFactory

[![travis-ci](https://travis-ci.org/leeonky/jfactory.svg?branch=master)](https://travis-ci.org/leeonky/jfactory)
[![coveralls](https://img.shields.io/coveralls/github/leeonky/jfactory/master.svg)](https://coveralls.io/github/leeonky/jfactory)
[![Mutation testing badge](https://img.shields.io/endpoint?style=flat&url=https%3A%2F%2Fbadge-api.stryker-mutator.io%2Fgithub.com%2Fleeonky%2Fjfactory%2Fmaster)](https://dashboard.stryker-mutator.io/reports/github.com/leeonky/jfactory/master)
[![Lost commit](https://img.shields.io/github/last-commit/leeonky/jfactory.svg)](https://github.com/leeonky/jfactory)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.leeonky/jfactory.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.leeonky/jfactory)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/6fd6832505594ed09070add129b570a6)](https://www.codacy.com/gh/leeonky/jfactory/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=leeonky/jfactory&amp;utm_campaign=Badge_Grade)
[![Maintainability](https://api.codeclimate.com/v1/badges/62a8a3826b05eefd1f3b/maintainability)](https://codeclimate.com/github/leeonky/jfactory/maintainability)
[![Code Climate issues](https://img.shields.io/codeclimate/issues/leeonky/jfactory.svg)](https://codeclimate.com/github/leeonky/jfactory/maintainability)
[![Code Climate maintainability (percentage)](https://img.shields.io/codeclimate/maintainability-percentage/leeonky/jfactory.svg)](https://codeclimate.com/github/leeonky/jfactory/maintainability)

---

用于为自动化测试准备测试数据夹具的工具库。可以指定测试数据的默认属性和关联数据。并可以预定义某些特质，然后创建具有某些特质的测试数据


# 安装

通过Gradle添加依赖
``` groovy
    implementation 'com.github.leeonky:jfactory:0.0.1'
```

# 快速开始
创建具有默认属性值的对象，实例代码默认使用[Lombok](https://projectlombok.org/)自动生成属性访问起代码。
```java
@Getter
@Setter
public class Bean {
    private String stringValue;
    private int intValue;
}

JFactory jFactory = new JFactory();

Bean bean = jFactory.create(Bean.class);
// bean.stringValue: "stringValue#1"
// bean.intValue: 1
Bean bean2 = jFactory.create(Bean.class);
// bean.stringValue: "stringValue#2"
// bean.intValue: 2
```

默认情况下属性值会根据属性名和属性所在类的类型的创建次数生成一个组合值，也可以在创建过程中给定一个输入值：
```java
public class Bean {
    private String stringValue;
    private int intValue;
}

Bean bean = jFactory.type(Bean.class).property("intValue", 100).create();
// bean.intValue: 100
```

# 自定义创建

## 类型默认规格
可以通过类型的默认规格指定某个数据类型的构造过程：
```java
public class Bean {
    private String stringValue;
    private long nowEpochSecond;
}

jFactory.factory(Bean.class).spec(instance-> instance.spec()
    .property(stringValue).value("Hello")
    .property(nowEpochSecond).value(() -> Instant.now().getEpochSecond())
);

Bean bean = jFactory.create(Bean.class);
// bean.stringValue: Hello
// bean.nowEpochSecond is epoch second
```

规格的定义都是通过如下代码实现，并且每一条规格仅表示属性的缺省值信息，后边会陆续介绍一些更高级的规格定义方法。
```java
instance.spec().property(stringValue).value("Hello")
```
规格定义之所以说是缺省值信息，是因为可以在最终创建对象时通过直接指定属性值的方式覆盖原先的任何规格定义
```java
Bean bean = jFactory.type(Bean.class).propery("stringValue", "Bye").create();
// bean.stringValue: "Bye"
```
- 指定类型的构造器

如果有些数据类型没有默认构造器，则可以提供一个对象构造器：
```java
public class Bean {
    private int i;
    public Bean(int i) {
        this.i = i;
    }
};

jFactory.factory(Bean.class).constructor(instance-> new Bean(instance.getSequence()))
jFactory.create(Bean.class);
```
- 定义数据特质

有时从测试的表达性而言，我们往往更关心创建具有某些特征的数据，而不是数据具有某些值的细节。比如：

```java
public class Person {
    private int age;
    private String gender;
};

Person person = jFactory.type(Product.class)
    .property("age", 20)
    .property("gender", "MALE").create()
```
可以预先定义类型的一些有名特征，然后在构造数据时组合使用：
```java
jFactory.factory(Product.class)
    .spec("成年", instance-> instance.spec().property("age").value(20))
    .spec("男性", instance-> instance.spec().property("gender").value("MALE"))
    );

Person person = jFactory.type(Product.class).traits("成年", "男性").create();
```

## 用Java类定义规格
通过JFactory.factory(Class<?>)定义的规格是这个类型的默认规格或者说全局规格，如果一个类型需要有多种截然不同的规格，则可以通过一个Java类来描述这个规格：
```java
public class Person {
    private int age;
    private String gender;
};

public class 女人 extends Spec<Person> {

    @Override
    public void main() {
        property("gender").value("FEMALE");
    }

    @Trait
    public Woman 老年的() {
        property("age").value(80);
        return this;
    }
};

public class 男人 extends Spec<Person> {

    @Override
    public void main() {
        property("gender").value("MALE");
    }
}

jFactory.spec(女人.class).traits("老年的").create()
```
其中main方法定义具体的规格，@Trait注解表示定义特质。JFactory提供了多种通过规格类来创建对象的方式：
```java
jFactory.createAs(女人.class);
jFactory.createAs(女人.class, 女人::老年的);
jFactory.spec(女人.class).create();
jFactory.spec(女人.class, 女人::老年的).create();
```
也可以事先注册规格类，然后通过字符串引用规格：
```java
jFactory.register(女人.class);

jFactory.createAs("老年的", "女人");
jFactory.spec("女人").traits("老年的").create();
```

## 保存/查询创建过的数据
JFactory会用一个实现了DataRepository接口的数据库按类型存储创建过的所有数据。并且支持按照条件查找出曾经创建过的对象
```java
public class Bean {
    private String stringValue;
}

Bean bean1 = jFactory.type(Bean.class).property("stringValue", "str1").create();
Bean bean2 = jFactory.type(Bean.class).property("stringValue", "str2").create();

Collection<Bean> queryAll = jFactory.type(Bean.class).queryAll();
Bean query1 = jFactory.type(Bean.class).property("stringValue", "str1").query();
Bean query2 = jFactory.type(Bean.class).property("stringValue", "str1").query();
// queryAll is [bean1, bean2]
// query1 == bean1
// query2 == bean2

jFactory.getDataRepository().clear();
// clear DB
```

## 创建关联对象
在有些测试场景，往往需要一个相对完整，但又不太在意细节的数据，比如有如下两个类型：
```java
public class Product {
    private String name;
    private String color;
}

public class Order {
    private Product product;
}
```
假如需要构造一个“有效“的订单，这里所谓有效是指Order.product属性不能为null，但其实又不关心具体是什么产品，之所以有效是为了不影响当前测试关注点以外被侧系统的运行。
一般可以通过依次创建对象来实现手动关联。
```java
Product product = jFactory.create(Product.class);
Order order = jFactory.type(Order.class).property("product", product).create();
```

但JFactroy提供了简便创建关联对象的方法可以在一个create调用中创建出具有级联关系的Order实例：
```java
jFactory.factory(Order.class).spec(instance -> instance.spec()
        .property("product").asDefault());

Order order = jFactory.create(Order.class);
//order.product is a default sub created object
```
还有一种方法是可以在创建时直接指定子对象的某个属性值来得到关联对象：
```java
jFactory.type(Order.class).property("product.name", "book").create();
```
与前者不同的是，JFactory首先尝试在曾经创建过的所有对象中线性搜索有没有满足name为book的product，如果有就把那个product赋值给Order的product属性，如果没有找到就自动构建一个name为book的product，总之一定会给Order关联到一个name为book的Product。

在指定属性值时，也可以在property中指定子属性对象创建的规格和特质：
```java
public class AProduct extends Spec<Product> {

    @Trait
    public AProduct 红色的() {
        property("color").value("red");
    }
}
jFactory.register(AProduct.class);
jFactory.type(Order.class).property("product(红色的 AProduct).name", "book").create();
```
注意：
- 这里的规格和特质只能通过字符串的形式指定，
- 在搜索已创建的对象做关联时不会比较备选对象的规格和特质，也就是以上代码只能保证创建出的Order的product的name属性为book。
- 特质写在前（可以组合写多个），规格名写在最后，中间用空格或英文逗号分割。

也可以用!强制创建子对象而不是查找关联已有对象：
```java
jFactory.type(Order.class).property("product(红色的 AProduct)!.name", "book").create();
```
