package com.github.leeonky.jfactory.bug;

import com.github.leeonky.jfactory.JFactory;
import com.github.leeonky.jfactory.Spec;
import com.github.leeonky.util.Classes;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.github.leeonky.dal.Assertions.expect;
import static org.assertj.core.api.Assertions.assertThat;

public class PropertyShouldOverridePropertyInSpec {
    private final JFactory jFactory = new JFactory() {{
        Classes.subTypesOf(Spec.class, "com.github.leeonky.jfactory.bug").forEach(c -> register((Class) c));
    }};

    @Test
    void property_should_override_property_in_spec() {
        jFactory.factory(ProductPriceBook.class).spec(instance -> instance.spec().property("priceBook")
                .byFactory(builder -> builder.property("code", "amazon")));

        ProductPriceBook productPriceBook = jFactory.type(ProductPriceBook.class).property("priceBook.code", "ebay").create();
        expect(productPriceBook).should("priceBook.code: 'ebay'");
    }

    @Test
    void should_merge_property_in_spec_and_input_when_property_inside_spec_is_a_query_build_and_use_merged_property_to_query() {
        jFactory.type(PriceBook.class).property("code", "amazon").create();
        jFactory.type(PriceBook.class).property("name", "book").create();
        PriceBook priceBook = jFactory.type(PriceBook.class).property("code", "amazon").property("name", "book").create();

        jFactory.factory(ProductPriceBook.class).spec(instance -> instance.spec().property("priceBook")
                .byFactory(builder -> builder.property("code", "amazon")));

        ProductPriceBook book1 = jFactory.type(ProductPriceBook.class).property("priceBook.name", "book").create();

        assertThat(book1.priceBook).isSameAs(priceBook);
    }

    @Test
    void should_merge_property_in_spec_and_input_when_property_inside_spec_is_a_query_and_create_with_merged_property() {
        jFactory.factory(ProductPriceBook.class).spec(instance -> instance.spec().property("priceBook")
                .byFactory(builder -> builder.property("code", "amazon")));

        ProductPriceBook book1 = jFactory.type(ProductPriceBook.class).property("priceBook.name", "book1").create();
        ProductPriceBook book2 = jFactory.type(ProductPriceBook.class).property("priceBook.name", "book2").create();

        expect(book1.priceBook).should(": {code: amazon name: book1}");
        expect(book2.priceBook).should(": {code: amazon name: book2}");
    }

    @Test
    void should_use_spec_in_property_override_origin_builder_spec() {
        jFactory.factory(ProductPriceBook.class).spec(instance -> instance.spec().property("priceBook")
                .from(PriceBookSpecInSpec.class).and(builder -> builder));

        ProductPriceBook productPriceBook = jFactory.type(ProductPriceBook.class).property("priceBook(PriceBookSpecInProperty).rate", 10).create();
        assertThat(productPriceBook.priceBook.getName()).isNotEqualTo("from-spec");
        expect(productPriceBook.priceBook).match("{code: amazon rate: 10}");
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

    public static class PriceBookSpecInSpec extends Spec<PriceBook> {

        @Override
        public void main() {
            property("name").value("from-spec");
        }
    }

    public static class PriceBookSpecInProperty extends Spec<PriceBook> {

        @Override
        public void main() {
            property("code").value("amazon");
        }
    }
}