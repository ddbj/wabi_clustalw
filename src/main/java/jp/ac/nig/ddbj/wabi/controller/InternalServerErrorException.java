package jp.ac.nig.ddbj.wabi.controller;

import java.util.LinkedHashMap;

/**
 * HTTP 500 Internal Server Error
 */
public class InternalServerErrorException extends AbstractReportException {
    public InternalServerErrorException(LinkedHashMap<String, Object> report) {
        super(report);
    }
}
