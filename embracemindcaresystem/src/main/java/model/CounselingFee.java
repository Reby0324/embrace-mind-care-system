package model;

public class CounselingFee {

    private int id;
    private String serviceName;
    private int durationMinutes;
    private int feeMin;
    private int feeMax;

    public CounselingFee() {
    }

    public CounselingFee(int id, String serviceName, int durationMinutes, int feeMin, int feeMax) {
        this.id = id;
        this.serviceName = serviceName;
        this.durationMinutes = durationMinutes;
        this.feeMin = feeMin;
        this.feeMax = feeMax;
    }

    public int getId() {
        return id;
    }

    public CounselingFee setId(int id) {
        this.id = id;
        return this;
    }

    public String getServiceName() {
        return serviceName;
    }

    public CounselingFee setServiceName(String serviceName) {
        this.serviceName = serviceName;
        return this;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public CounselingFee setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
        return this;
    }

    public int getFeeMin() {
        return feeMin;
    }

    public CounselingFee setFeeMin(int feeMin) {
        this.feeMin = feeMin;
        return this;
    }

    public int getFeeMax() {
        return feeMax;
    }

    public CounselingFee setFeeMax(int feeMax) {
        this.feeMax = feeMax;
        return this;
    }

    // 給 InstructionPanel 使用：顯示價格範圍
    public String getPriceRange() {
        return feeMin + " - " + feeMax;
    }

    // 你的 MySQL counseling_fees 表目前沒有 note 欄位，所以先回傳空字串
    public String getNote() {
        return "";
    }
}