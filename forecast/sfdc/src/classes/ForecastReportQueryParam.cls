/**
 * Forecast Report Query Parameters To Use for Generating Forecast Reports
 *
 * @see SalesForecastData
 * @author auzzaman
 * @since 178.store
 */
public with sharing class ForecastReportQueryParam {

    public with sharing class ForecastReportQueryParamException extends Exception {}

    String fiscalYear;
    List<String> monthsInQrtr;
    Set<Id> ownerIds;

    public ForecastReportQueryParam() {

    }

    public String getFiscalYear() {
        return this.fiscalYear;
    }
    public void setFiscalYear(String fYear) {
        this.fiscalYear = fYear;
    }

    public List<String> getMonthsInQrtr() {
        return this.monthsInQrtr;
    }
    public void setMonthsInQrtr(List<String> months) {
        this.monthsInQrtr = months;
    }

    public set<Id> getOwnerIds() {
        return this.ownerIds;
    }
    public void setOwnerIds(Set<Id> rIds) {
        this.ownerIds = rIds;
    }

}
