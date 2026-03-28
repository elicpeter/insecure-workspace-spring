package com.example.insecurecollab.util;

import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.StringTemplateResolver;

import java.util.Map;

@Component
public class InsecureTemplateRenderer {

    public String renderUserSuppliedTemplate(String template, Map<String, Object> model) {
        StringTemplateResolver resolver = new StringTemplateResolver();
        resolver.setTemplateMode(TemplateMode.HTML);
        TemplateEngine engine = new TemplateEngine();
        engine.setTemplateResolver(resolver);
        Context context = new Context();
        context.setVariables(model);
        return engine.process(template, context);
    }
}
