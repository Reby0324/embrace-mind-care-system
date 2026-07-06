package service.impl;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;

import dao.AppointmentDao;
import dao.CounselingFeeDao;
import dao.PatientDao;
import dao.ProfessionalDao;
import dao.ScheduleDao;
import dao.ServiceTypeDao;
import dao.impl.AppointmentDaoImpl;
import dao.impl.CounselingFeeDaoImpl;
import dao.impl.PatientDaoImpl;
import dao.impl.ProfessionalDaoImpl;
import dao.impl.ScheduleDaoImpl;
import dao.impl.ServiceTypeDaoImpl;
import exception.AppException;
import model.AppointmentView;
import model.CounselingFee;
import model.Patient;
import model.Professional;
import model.Schedule;
import model.ServiceType;
import service.AppointmentService;
import util.DbConnection;

public class AppointmentServiceImpl implements AppointmentService {

    private final ServiceTypeDao serviceTypeDao = new ServiceTypeDaoImpl();
    private final ProfessionalDao professionalDao = new ProfessionalDaoImpl();
    private final ScheduleDao scheduleDao = new ScheduleDaoImpl();
    private final PatientDao patientDao = new PatientDaoImpl();
    private final AppointmentDao appointmentDao = new AppointmentDaoImpl();
    private final CounselingFeeDao counselingFeeDao = new CounselingFeeDaoImpl();

    @Override
    public List<ServiceType> getServiceTypes() {
        return serviceTypeDao.findAll();
    }

    @Override
    public List<Professional> getProfessionals() {
        return professionalDao.findAll();
    }

    @Override
    public List<Professional> getProfessionalsByRole(String role) {
        if (role == null || role.trim().isEmpty() || "全部".equals(role)) {
            return professionalDao.findAll();
        }
        return professionalDao.findByRole(role);
    }

    @Override
    public List<Schedule> getSchedulesByDate(LocalDate date) {
        return scheduleDao.findByDate(date);
    }

    @Override
    public List<Schedule> getSchedulesByServiceType(int serviceTypeId) {
        return scheduleDao.findByServiceType(serviceTypeId);
    }

    @Override
    public List<Schedule> getSchedulesByProfessional(int professionalId) {
        return scheduleDao.findByProfessional(professionalId);
    }

    @Override
    public Schedule getScheduleById(int scheduleId) {
        return scheduleDao.findById(scheduleId);
    }

    @Override
    public int reserve(int scheduleId, String patientName, String idNo, LocalDate birthDate, String phone) throws AppException {
        validatePatient(patientName, idNo, birthDate, phone);

        Connection conn = null;
        try {
            conn = DbConnection.getConnection();
            conn.setAutoCommit(false);

            Schedule schedule = scheduleDao.findByIdForUpdate(scheduleId, conn);
            if (schedule == null) {
                throw new AppException("查無此預約時段");
            }
            if (!schedule.isActive()) {
                throw new AppException("此時段目前暫停預約");
            }
            if (schedule.getBookedCount() >= schedule.getQuota()) {
                throw new AppException("此時段已額滿，請選擇其他時段");
            }

            Patient patient = patientDao.findByIdentity(idNo.trim(), birthDate, phone.trim(), conn);
            int patientId;
            if (patient == null) {
                patient = new Patient()
                        .setName(patientName.trim())
                        .setIdNo(idNo.trim())
                        .setBirthDate(birthDate)
                        .setPhone(phone.trim());
                patientId = patientDao.insert(patient, conn);
            } else {
                patientId = patient.getId();
            }

            int appointmentNo = schedule.getBookedCount() + 1;
            appointmentDao.insert(patientId, scheduleId, appointmentNo, conn);
            scheduleDao.increaseBookedCount(scheduleId, conn);

            conn.commit();
            return appointmentNo;
        } catch (AppException e) {
            rollback(conn);
            throw e;
        } catch (Exception e) {
            rollback(conn);
            throw new AppException("預約失敗：" + e.getMessage());
        } finally {
            close(conn);
        }
    }

    @Override
    public List<AppointmentView> searchAppointments(String idNo, LocalDate birthDate, String phone) throws AppException {
        if (isBlank(idNo) || birthDate == null || isBlank(phone)) {
            throw new AppException("請輸入身分證字號、出生日期與手機號碼");
        }
        return appointmentDao.search(idNo.trim(), birthDate, phone.trim());
    }

    @Override
    public void cancelAppointment(int appointmentId) throws AppException {
        Connection conn = null;
        try {
            conn = DbConnection.getConnection();
            conn.setAutoCommit(false);

            AppointmentView view = appointmentDao.findById(appointmentId, conn);
            if (view == null) {
                throw new AppException("查無此預約紀錄");
            }
            if (!"預約完成".equals(view.getStatus())) {
                throw new AppException("此預約狀態不是預約完成，無法取消");
            }

            appointmentDao.cancel(appointmentId, conn);
            scheduleDao.decreaseBookedCount(view.getScheduleId(), conn);
            conn.commit();
        } catch (AppException e) {
            rollback(conn);
            throw e;
        } catch (Exception e) {
            rollback(conn);
            throw new AppException("取消失敗：" + e.getMessage());
        } finally {
            close(conn);
        }
    }

    @Override
    public List<CounselingFee> getCounselingFees() {
        return counselingFeeDao.findAll();
    }

    private void validatePatient(String patientName, String idNo, LocalDate birthDate, String phone) throws AppException {
        if (isBlank(patientName)) {
            throw new AppException("請輸入姓名");
        }
        if (isBlank(idNo)) {
            throw new AppException("請輸入身分證字號");
        }
        if (birthDate == null) {
            throw new AppException("請輸入出生日期");
        }
        if (isBlank(phone)) {
            throw new AppException("請輸入手機號碼");
        }
    }

    private boolean isBlank(String text) {
        return text == null || text.trim().isEmpty();
    }

    private void rollback(Connection conn) {
        try {
            if (conn != null) {
                conn.rollback();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void close(Connection conn) {
        try {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
