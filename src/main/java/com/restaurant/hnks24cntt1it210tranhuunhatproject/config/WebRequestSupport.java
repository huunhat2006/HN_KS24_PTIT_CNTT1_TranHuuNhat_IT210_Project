package com.restaurant.hnks24cntt1it210tranhuunhatproject.config;

import jakarta.servlet.http.HttpServletRequest;

public final class WebRequestSupport {

    private WebRequestSupport() {
    }

    public static boolean isAjaxRequest(HttpServletRequest request, String ajaxParam) {
        String requestedAjaxParam = ajaxParam != null ? ajaxParam : request.getParameter("ajax");
        return "true".equalsIgnoreCase(requestedAjaxParam)
                || "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"));
    }

    public static String view(String viewName, boolean ajax) {
        return ajax ? viewName + " :: content" : viewName;
    }
}


