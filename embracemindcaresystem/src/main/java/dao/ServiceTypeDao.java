package dao;

import java.util.List;
import model.ServiceType;

public interface ServiceTypeDao {
    List<ServiceType> findAll();
    void insert(ServiceType serviceType) throws Exception;
}
