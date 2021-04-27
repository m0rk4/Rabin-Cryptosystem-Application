package by.bsuir.m0rk4.it.task.third.entity;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ResultModel {
    private final StringProperty hexSource;
    private final StringProperty decimalSource;
    private final StringProperty operationType;
    private final StringProperty hexResult;
    private final StringProperty decimalResult;
    private final StringProperty meta;

    public ResultModel() {
        this.hexSource = new SimpleStringProperty();
        this.decimalSource = new SimpleStringProperty();
        this.operationType = new SimpleStringProperty();
        this.hexResult = new SimpleStringProperty();
        this.decimalResult = new SimpleStringProperty();
        this.meta = new SimpleStringProperty();
    }

    public String getMeta() {
        return meta.get();
    }

    public void setMeta(String meta) {
        this.meta.set(meta);
    }

    public String getDecimalSource() {
        return decimalSource.get();
    }

    public String getDecimalResult() {
        return decimalResult.get();
    }

    public String getHexResult() {
        return hexResult.get();
    }

    public String getHexSource() {
        return hexSource.get();
    }

    public String getOperationType() {
        return operationType.get();
    }

    public void setDecimalSource(String decimalSource) {
        this.decimalSource.set(decimalSource);
    }

    public void setDecimalResult(String decimalResult) {
        this.decimalResult.set(decimalResult);
    }

    public void setHexSource(String hexSource) {
        this.hexSource.set(hexSource);
    }

    public void setHexResult(String hexResult) {
        this.hexResult.set(hexResult);
    }

    public void setOperationType(String operationType) {
        this.operationType.set(operationType);
    }
}
