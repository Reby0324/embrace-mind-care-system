package dao;

import java.sql.Connection;
import java.time.LocalDate;
import model.Patient;

public interface PatientDao {
    int insert(Patient patient, Connection conn) throws Exception;
    Patient findByIdentity(String idNo, LocalDate birthDate, String phone, Connection conn) throws Exception;
}
