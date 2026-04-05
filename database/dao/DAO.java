package database.dao;

import java.sql.SQLException;
import java.util.List;

/**
 * Interface gnrique que tous les DAO doivent implmenter.
 * ROLE 4  Interface partage avec tout le groupe.
 */
public interface DAO<T> {
    void save(T t)       throws SQLException;
    T    findById(int id) throws SQLException;
    List<T> findAll()    throws SQLException;
    void update(T t)     throws SQLException;
    void delete(int id)  throws SQLException;
}
