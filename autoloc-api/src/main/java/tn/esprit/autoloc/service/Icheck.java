package tn.esprit.autoloc.service;

public interface Icheck<T>{
    boolean check(T entity, boolean isNew);
}
