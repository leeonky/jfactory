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

用于为自动化测试准备测试数据夹具的工具库。可以指定测试数据的默认属性和关联数据。并可以预定义某些特质，然后创建具有某些特制的测试数据


#安装

通过Gradle添加依赖
``` groovy
    implementation 'com.github.leeonky:jfactory:0.0.1'
```

#快速开始
创建具有默认属性值的对象，实例代码默认使用[Lombok](https://projectlombok.org/)自动生成属性访问起代码。
```java
@Getter
@Setter
public class Bean {
    private String stringValue;
    private int intValue;

    public static void main(String[] args){
        JFactory jFactory = new JFactory();

        Bean bean = jFactory.create(Bean.class);
        // bean.stringValue is "stringValue#1"

        // bean.intValue is 1
        Bean bean2 = jFactory.create(Bean.class);
        // bean.stringValue is "stringValue#2"
        // bean.intValue is 2
    }
}
```
默认情况下属性值会根据属性名和属性所在类的类型的创建次数生成一个组合值，也可以在创建过程中给定一个输入值：
```java
@Getter
@Setter
public class Bean {
    private String stringValue;
    private int intValue;

    public static void main(String[] args){
        JFactory jFactory = new JFactory();

        Bean bean = jFactory.type(Bean.class).property("intValue", 100).create();
        // bean.intValue is 100
    }
}

```