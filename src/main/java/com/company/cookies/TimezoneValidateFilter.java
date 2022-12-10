package com.company.cookies;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.ZoneId;
import java.util.Set;

@WebFilter(value = "/*")
public class TimezoneValidateFilter extends HttpFilter {
    @Override
    public void doFilter(HttpServletRequest req, HttpServletResponse resp, FilterChain chain) throws IOException, ServletException {
        String timezone = req.getParameter("timezone");

        Set <String> validIDs= ZoneId.getAvailableZoneIds();

        if (timezone == null || validIDs.contains(timezone.replace("UTC","Etc/GMT"))) {
            chain.doFilter(req, resp);
        } else {
            resp.setStatus(400);

            resp.setContentType("application/json");
            resp.getWriter().write("{\"Error 400\": \"Invalid timezone\"}");
            resp.getWriter().close();
        }
    }
}
