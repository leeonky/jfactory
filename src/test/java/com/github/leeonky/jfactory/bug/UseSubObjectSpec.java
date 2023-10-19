package com.github.leeonky.jfactory.bug;

import com.github.leeonky.jfactory.JFactory;
import com.github.leeonky.jfactory.Spec;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.github.leeonky.dal.Assertions.expect;

public class UseSubObjectSpec {
    public static class Order {
        public String name;
        public String des;
        public List<OrderLine> orderLines = new ArrayList<>();
    }

    public static class OrderLine {
        public Order order;
        public String name;
        public String des;
        public Product product;
    }

    public static class Product {
        public Category category;
        public OrderLine orderLine;
        public String name;
        public String des;
    }

    public static class Category {
        public String name;
        public String des;
    }

    public static class OrderSpec extends Spec<Order> {
        @Override
        public void main() {
            property("orderLines").reverseAssociation("order");
            property("orderLines[0]").is(OrderLineSpec.class);
            property("des").value("from-spec");
        }
    }

    public static class EmptyOrderSpec extends Spec<Order> {
        @Override
        public void main() {
            property("orderLines").reverseAssociation("order");
            property("des").value("from-spec");
        }
    }

    public static class OrderLineSpec extends Spec<OrderLine> {
        @Override
        public void main() {
            property("order").is(EmptyOrderSpec.class);
            property("product").is(ProductSpec.class);
            property("des").value("from-spec");

            //property("product").reverseAssociation("orderLine");
        }
    }

    public static class ProductSpec extends Spec<Product> {
        @Override
        public void main() {
            property("des").value("from-spec");
            property("category").is(CategorySpec.class);
        }
    }

    public static class CategorySpec extends Spec<Category> {
        @Override
        public void main() {
            property("des").value("from-spec");
        }
    }

    @Test
    void test() {
        JFactory jFactory = new JFactory();
//        jFactory.spec(OrderLineSpec.class).property("product.name", "input").create();
//        if (false)
//        extracted(jFactory);
    }

    private void extracted(JFactory jFactory) {
        expect(
                jFactory.spec(OrderSpec.class)
                        .property("orderLines[0].name", "input")
                        .create()).should("=''");
    }
}
