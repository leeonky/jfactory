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

用于生成测试数据的工具库。可以预定义数据的默认属性和关联属性，还可以定义数据Trait，然后创建具有某些Trait的测试数据

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

## 类型默认Spec
JFactory通过Spec定义对象各个属性值的产生策略：
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

Spec的详细定义都是通过如下代码实现，并且每一项配置仅表示属性的缺省值信息。
```java
property(stringValue).value("Hello")
```
Spec定义之所以说是缺省值信息，是因为可以在最终创建对象时通过直接指定属性值的方式覆盖原先的任何Spec定义
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
- 定义数据Trait

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
可以预先定义类型的一些具名Spec，然后在构造数据时组合使用：
```java
jFactory.factory(Product.class)
    .spec("成年", instance-> instance.spec().property("age").value(20))
    .spec("男性", instance-> instance.spec().property("gender").value("MALE"))
    );

Person person = jFactory.type(Product.class).traits("成年", "男性").create();
```

## 用Java类定义Spec
通过JFactory.factory(Class<?>)定义的Spec是这个类型的默认或者说全局Spec，如果一个类型需要有多种截然不同的Spec，则可以通过一个Java类来描述这个Spec：
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
其中main方法定义具体的Spec，@Trait注解表示定义Trait。JFactory提供了多种通过Spec类来创建对象的方式：
```java
jFactory.createAs(女人.class);
jFactory.createAs(女人.class, 女人::老年的);
jFactory.spec(女人.class).create();
jFactory.spec(女人.class, 女人::老年的).create();
```
也可以事先注册规Spec，然后通过字符串引用：
```java
jFactory.register(女人.class);

jFactory.createAs("老年的", "女人");
jFactory.spec("女人").traits("老年的").create();
```
需要注意的是，通过类来描述Spec类似于继承，Spec类都会继承类型的默认全局Spec。比如：
```java
public class Bean {
    private String stringValue;
}

public class ABean extends Spec<Bean> {
}

jFactory.factory(Bean.class).spec(instance-> instance.spec()
    .property(stringValue).value("Hello")
);

Bean bean = jFactory.createAs(ABean.class);
// bean.stringValue: "Hello"
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
假如需要构造一个“有效“的订单，这里所谓有效是指Order.product属性不能为null，但其实又不关心具体是什么产品，之所以有效是为了不影响当前测试关注点以外被测系统的运行。
通常通过依次创建对象然后手动关联。
```java
Product product = jFactory.create(Product.class);
Order order = jFactory.type(Order.class).property("product", product).create();
```
- 在Spec中指定子属性对象

JFactroy提供了简便创建关联对象的方法可以在一个create调用中创建出具有级联关系的Order实例：
```java
jFactory.factory(Order.class).spec(instance -> instance.spec()
        .property("product").asDefault());

Order order = jFactory.create(Order.class);
//order.product is a default sub created object
```
- 通过输入属性指定

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
这里的Spec和Trait只能通过字符串的形式指定。在搜索已创建的对象做关联时不会比较备选对象的Spec和Trait，也就是以上代码只能保证创建出的Order的product的name属性为book。Trait写在前（可以组合写多个），Spec写在最后，中间用空格或英文逗号分割。

而且Product对象在创建后也会先于Order对象保存进数据库。


- 强制创建子属性对象

可以用!强制创建子对象而不是查找关联已有对象：
```java
jFactory.type(Order.class).property("product(红色的 AProduct)!.name", "book").create();
```
这样无论是否创建过name为book的Product，上面的代码总是会新创建一个name为book的Product，并关联到Order。

- 引用当前对象

如果想在创建对象是引用对象自己，比如：
```java
public class Bean {
    private Bean self;
}
```
想要构造出bean.self = bean的场景，需要在Spec中引用当前所要创建的对象“实例”，不过这个实例是以Supplier<Object>形式提供的：
```java
jFactory.factory(Bean.class).spec(instance ->  instance.spec()
    .property("self").value(instance.reference())
);

Bean bean = jFactory.create(Bean.class);
// bean.self == bean
```

有些有父子关系的对象需要在子对象的某个属性引用父对象。比如：
```java
public class Farther {
    private Son son;
}

public class Son {
    private Farther farther;
}
```

如果想创建出一个Father对象father，并且father.son.father是father，就需要在son中反向引用父对象。
```java
jFactory.factory(Farther.class).spec(instance ->  instance.spec()
    .property("son").asDefault()
    .property("son").reverseAssociation("father")
);
```
并且建立反向引用后，父子对象在创建后保存到数据库的次序会发生改变，没有反向关联的对象会先保存子对象，有反向关联关系的情况下会先保存父对象，再保存子对象。