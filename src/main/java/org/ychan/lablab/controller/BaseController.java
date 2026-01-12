package org.ychan.lablab.controller;


import com.github.pagehelper.PageHelper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.ychan.lablab.common.result.PageResult;

import javax.swing.*;
import java.util.List;

public class BaseController {

    /**
     * 默认值pageNum 1, pageSize 10
     */
    public void startPage(){
        // 请求上下文中获取请求头
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if(attributes == null){
            PageHelper.startPage(1, 10);
            return;
        }
        startPage(attributes.getRequest());
    }

    /**
     *
     * @param request
     */
    public void startPage(HttpServletRequest request){

        int pageNum = 1;
        try {
            String pageNumStr = request.getParameter("pageNum");
            if (pageNumStr != null && !pageNumStr.trim().isEmpty()) {
                pageNum = Integer.parseInt(pageNumStr.trim());
            }
        } catch (NumberFormatException e) {
            pageNum = 1;
        }
        pageNum = Math.max(pageNum, 1);

        int pageSize = 10;
        try {
            String pageSizeStr = request.getParameter("pageSize");
            if (pageSizeStr != null && !pageSizeStr.trim().isEmpty()) {
                pageSize = Integer.parseInt(pageSizeStr.trim());
            }
        } catch (NumberFormatException e) {
            pageSize = 10;
        }
        pageSize = Math.max(Math.min(pageSize, 50), 1);

        PageHelper.startPage(pageNum, pageSize);
    }

}
