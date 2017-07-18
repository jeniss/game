package com.game.template;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.IOException;
import java.util.Map;

/**
 * Created by jennifert on 7/18/2017.
 */
@Service(value = "templateService")
public class TemplateService {
    private static final Logger LOGGER = Logger.getLogger(TemplateService.class);

    @Autowired
    private Configuration freemarkerConfiguration;

    /**
     * Generate templateName with arguments
     *
     * @param templateFile File name of template
     * @param args         Arguments
     * @return Text generated
     */
    public String generate(TemplateName templateFile, Map<String, Object> args) throws IOException, TemplateException {
        Template template = freemarkerConfiguration.getTemplate(templateFile.getFileName());
        return FreeMarkerTemplateUtils.processTemplateIntoString(template, args);
    }

}
