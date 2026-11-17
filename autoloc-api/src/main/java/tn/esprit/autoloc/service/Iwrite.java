package tn.esprit.autoloc.service;

public interface Iwrite<T,I>{
    T create(T entity);
    T update(I id, T entity);
    void deleteById(I id);
}
