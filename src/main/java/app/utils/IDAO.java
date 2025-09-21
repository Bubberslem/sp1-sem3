package app.utils;

import java.util.List;

// Generic DAO interface defining CRUD operations. Used by specific DAO implementations for different entities.
public interface IDAO<T, I> {
    T create (T t);
    List<T> getAll();
    T getById(I  id);
    T update(T t);
    boolean delete(I id);
}