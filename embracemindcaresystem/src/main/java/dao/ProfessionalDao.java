package dao;

import java.util.List;
import model.Professional;

public interface ProfessionalDao {
    List<Professional> findAll();
    List<Professional> findByRole(String role);
    List<Professional> findByServiceType(int serviceTypeId);
}
