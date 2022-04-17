package jp.ac.nig.ddbj.wabi.controller;

import java.util.LinkedHashMap;

abstract class AbstractReportException extends Exception {
    private LinkedHashMap<String, Object> report;

	
    protected AbstractReportException(LinkedHashMap<String, Object> report) {
        this.report = report;
    }

    public LinkedHashMap<String, Object> getReport() {
        return report;
    }


}
