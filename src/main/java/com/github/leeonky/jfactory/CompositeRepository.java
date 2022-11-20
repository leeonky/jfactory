package com.github.leeonky.jfactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

public class CompositeRepository implements DataRepository {
    private final List<TypedPrecedingRepo> repos = new ArrayList<>();
    private final MemoryDataRepository defaultRepo = new MemoryDataRepository();

    public CompositeRepository addRepository(Predicate<Class<?>> when, DataRepository repo) {
        repos.add(new TypedPrecedingRepo(when, repo));
        return this;
    }

    private static class TypedPrecedingRepo {
        private final Predicate<Class<?>> when;
        private final DataRepository repository;

        private TypedPrecedingRepo(Predicate<Class<?>> when, DataRepository repository) {
            this.when = when;
            this.repository = repository;
        }

        public boolean matches(Class<?> type) {
            return when.test(type);
        }

        public DataRepository getRepository() {
            return repository;
        }
    }

    @Override
    public <T> Collection<T> queryAll(Class<T> type) {
        return fetchRepo(type).queryAll(type);
    }

    private DataRepository fetchRepo(Class<?> type) {
        return repos.stream().filter(r -> r.matches(type)).findFirst()
                .map(TypedPrecedingRepo::getRepository).orElse(defaultRepo);
    }

    @Override
    public void save(Object object) {
        fetchRepo(object.getClass()).save(object);
    }

    @Override
    public void clear() {
        repos.stream().map(TypedPrecedingRepo::getRepository).forEach(DataRepository::clear);
        defaultRepo.clear();
    }
}
