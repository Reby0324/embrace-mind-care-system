package dao;

import java.util.List;
import model.ServiceType;

public interface ServiceTypeDao {
    List<ServiceType> findAll();
}
