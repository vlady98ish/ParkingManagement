package main.model.utils.services.data;

import java.util.Collections;
import java.util.List;


public abstract class AbstractService {
    public abstract <T> List<T> findAll();

    public abstract <T> T findById(Object id);

    public <T> List<T> findByFK(int id) {
        return Collections.emptyList();
    }

    public abstract boolean persist(Object o);

    public abstract void delete(Object o);

    public abstract boolean update(Object o);
}
