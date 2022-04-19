package com.github.leeonky.jfactory.bug;

import com.github.leeonky.jfactory.JFactory;
import com.github.leeonky.jfactory.Spec;
import com.github.leeonky.util.BeanClass;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.github.leeonky.dal.Assertions.expect;

public class PropertyShouldOverridePropertyInSpec {
    private final JFactory jFactory = new JFactory() {{
        BeanClass.subTypesOf(Spec.class, "com.github.leeonky.jfactory.bug").forEach(c -> register((Class) c));
    }};

    @Test
    void property_should_override_property_in_spec() {
        jFactory.factory(ProductPriceBook.class).spec(instance -> instance.spec().property("priceBook")
                .byFactory(builder -> builder.property("code", "amazon")));

        ProductPriceBook productPriceBook = jFactory.type(ProductPriceBook.class).property("priceBook.code", "ebay").create();
        expect(productPriceBook).should("priceBook.code: 'ebay'");
    }

    @Getter
    @Setter
    @Accessors(chain = true)
    public static class PriceBook {
        private String name, code;
        private int rate;
    }

    @Getter
    @Setter
    @Accessors(chain = true)
    public static class Product {
        private String productName, sku;
        private List<ProductPriceBook> productPriceBooks = new ArrayList<>();
    }

    @Getter
    @Setter
    @Accessors(chain = true)
    public static class ProductPriceBook {
        private Product product;
        private PriceBook priceBook;
    }
}