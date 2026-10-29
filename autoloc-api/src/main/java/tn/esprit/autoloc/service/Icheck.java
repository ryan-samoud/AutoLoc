package tn.esprit.autoloc.service;

public interface Icheck<T,ID>{
    boolean check(T entity, boolean isNew);
}
