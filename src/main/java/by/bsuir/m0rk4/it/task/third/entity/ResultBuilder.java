package by.bsuir.m0rk4.it.task.third.entity;

public class ResultBuilder {
    private ResultModel resultModel;

    public void reset() {
        resultModel = new ResultModel();
    }

    public ResultModel getResultModel() {
        return resultModel;
    }

    public ResultBuilder buildHexResult(String hexResult) {
        resultModel.setHexResult(hexResult);
        return this;
    }

    public ResultBuilder buildDecimalResult(String decimalResult) {
        resultModel.setDecimalResult(decimalResult);
        return this;
    }

    public ResultBuilder buildOperationType(String operation) {
        resultModel.setOperationType(operation);
        return this;
    }

    public ResultBuilder buildHexSource(String hexSource) {
        resultModel.setHexSource(hexSource);
        return this;
    }

    public ResultBuilder buildDecimalSource(String decimalSource) {
        resultModel.setDecimalSource(decimalSource);
        return this;
    }

    public ResultBuilder buildMeta(String meta) {
        resultModel.setMeta(meta);
        return this;
    }
}
