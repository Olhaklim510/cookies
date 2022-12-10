package com.company.cookies;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.WebApplicationTemplateResolver;
import org.thymeleaf.web.servlet.JavaxServletWebApplication;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@WebServlet(value = "/time/")
public class ThymeleafTimeServlet extends HttpServlet {
    private TemplateEngine engine;
    private final static DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss 'UTC'x");

    @Override
    public void init(ServletConfig config) throws ServletException {

        JavaxServletWebApplication application = JavaxServletWebApplication.buildApplication(config.getServletContext());
        this.engine = new TemplateEngine();

        final WebApplicationTemplateResolver resolver = new WebApplicationTemplateResolver(application);
        resolver.setPrefix("/WEB-INF/templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML5");
        resolver.setOrder(this.engine.getTemplateResolvers().size());
        resolver.setCacheable(false);
        this.engine.addTemplateResolver(resolver);
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String timezone;
        if (req.getParameter("timezone") != null && !req.getParameter("timezone").equals("UTC")) {
            timezone = parseTimeZone(req);
            resp.addCookie(new Cookie("lastTimezone", timezone));
        } else {
            timezone = Optional.ofNullable(req.getCookies()).stream()
                    .filter(Objects::nonNull)
                    .flatMap(Arrays::stream)
                    .filter(cookie -> "lastTimezone".equals(cookie.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse("UTC");
        }

        resp.setContentType("text/html");

        String initTime = OffsetDateTime.now().atZoneSameInstant(ZoneId.of(timezone)).toOffsetDateTime().format(FORMAT);


        Context simpleContext = new Context(
                req.getLocale(),
                Map.of("initTime", initTime)
        );

        engine.process("time", simpleContext, resp.getWriter());
        resp.getWriter().close();
    }

    private String parseTimeZone(HttpServletRequest request) {
        if (request.getParameterMap().containsKey("timezone")) {
            return request.getParameter("timezone");
        }
        return "UTC";
    }
}
