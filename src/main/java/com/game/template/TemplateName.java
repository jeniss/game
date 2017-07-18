package com.game.template;

/**
 * Define template name here , see classpath:template
 */
public enum TemplateName {
    MAIL_ERROR_MSG("mail_error_msg.ftl");

    private final String fileName;

    TemplateName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}
