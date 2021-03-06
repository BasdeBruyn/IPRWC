package nl.bookshop.data.services;

import java.util.List;

public interface CrudService<T> {
    List<T> getAll();
    T getById(Integer id);
    T save(T t);
    void update(T t);
    void delete(T t);
    void delete(Integer id);
}
