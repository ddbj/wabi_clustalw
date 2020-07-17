package jp.ac.nig.ddbj.wabi.controller;

import java.util.LinkedHashMap;

/**
 * HTTP 400 Bad Request
 */
public class BadRequestException extends AbstractReportException {
    public BadRequestException(LinkedHashMap<String, Object> report) {
        super(report);
    }
}
