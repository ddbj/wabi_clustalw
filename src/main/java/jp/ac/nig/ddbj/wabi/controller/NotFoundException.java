package jp.ac.nig.ddbj.wabi.controller;

import java.util.LinkedHashMap;

/**
 * HTTP 404 Not Found
 */
public class NotFoundException extends AbstractReportException {
    public NotFoundException(LinkedHashMap<String, Object> report) {
        super(report);
    }
}
