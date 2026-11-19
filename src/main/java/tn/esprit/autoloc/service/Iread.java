package tn.esprit.autoloc.service;

import java.util.List;

public interface Iread<T,I>{
    T findById(I id);
    List<T> findAll();
}
