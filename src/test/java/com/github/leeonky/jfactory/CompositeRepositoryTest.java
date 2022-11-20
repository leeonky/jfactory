package com.github.leeonky.jfactory;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CompositeRepositoryTest {

    CompositeRepository compositeRepository = new CompositeRepository();

    @Nested
    class QueryByType {

        @Test
        void default_repository_is_memory_repo_when_no_registered_repo() {
            compositeRepository.save("hello");

            assertThat(compositeRepository.queryAll(String.class)).containsExactly("hello");
        }

        @Test
        void query_by_type_and_repo() {
            DataRepository repo = mock(DataRepository.class);
            Collection collection = mock(Collection.class);
            when(repo.queryAll(String.class)).thenReturn(collection);

            Predicate predicate = mock(Predicate.class);
            when(predicate.test(any())).thenReturn(true);

            compositeRepository.addRepository(predicate, repo);

            assertThat(compositeRepository.queryAll(String.class)).isEqualTo(collection);
            verify(predicate).test(String.class);
        }
    }

    @Nested
    class SaveByType {

        @Test
        void default_repository_is_memory_repo_when_no_registered_repo() {
            compositeRepository.save("hello");

            assertThat(compositeRepository.queryAll(String.class)).containsExactly("hello");
        }

        @Test
        void query_by_type_and_repo() {
            DataRepository repo = mock(DataRepository.class);
            Predicate predicate = mock(Predicate.class);
            when(predicate.test(any())).thenReturn(true);
            compositeRepository.addRepository(predicate, repo);

            compositeRepository.save("string");

            verify(predicate).test(String.class);
            verify(repo).save("string");
            assertThat(compositeRepository.queryAll(String.class)).isEmpty();
        }
    }

    @Nested
    class ClearAll {

        @Test
        void default_repository_is_memory_repo_when_no_registered_repo() {
            compositeRepository.save("hello");

            compositeRepository.clear();

            assertThat(compositeRepository.queryAll(String.class)).isEmpty();
        }

        @Test
        void clear_by_type_and_repo() {
            compositeRepository.save("hello");
            DataRepository repo = mock(DataRepository.class);

            compositeRepository.addRepository(type -> false, repo);
            compositeRepository.clear();

            assertThat(compositeRepository.queryAll(String.class)).isEmpty();
            verify(repo).clear();
        }
    }
}