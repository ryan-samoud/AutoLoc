package tn.esprit.autoloc.service;

import java.util.List;

public interface IGenericService<T,ID> extends Icheck<T,ID>,Iread<T,ID>,Iwrite<T,ID>{
}
