package org.ychan.lablab.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;

    @Value("${app.upload-dir:uploads}")
    private String uploadDir;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns(
                        "/admin/**",
                        "/api/config/admin/**",
                        "/api/search/admin/**",
                        "/lab-news/add", "/lab-news/update", "/lab-news/delete/**",
                        "/notice/add", "/notice/update", "/notice/delete/**",
                        "/achievement/add", "/achievement/update", "/achievement/delete/**",
                        "/publication/add", "/publication/update", "/publication/delete/**",
                        "/topic-project/add", "/topic-project/update", "/topic-project/delete/**",
                        "/research/direction/add", "/research/direction/update", "/research/direction/delete/**",
                        "/team/scholar/add", "/team/scholar/update", "/team/scholar/delete/**",
                        "/area/add", "/area/update", "/area/delete/**", "/area/sort"
                )
                .excludePathPatterns("/admin/login", "/admin/forgot-password/send-code", "/admin/forgot-password/reset");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 与上传目录一致：相对路径按进程工作目录解析
        java.nio.file.Path base = java.nio.file.Paths.get(uploadDir).isAbsolute()
                ? java.nio.file.Paths.get(uploadDir)
                : java.nio.file.Paths.get(System.getProperty("user.dir"), uploadDir);
        String path = base.toAbsolutePath().normalize().toString().replace('\\', '/');
        String location = path.endsWith("/") ? "file:" + path : "file:" + path + "/";
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(location);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // 门户 5173 + 后台 5174（localhost / 127.0.0.1）
                .allowedOriginPatterns(
                        "http://localhost:5173", "http://127.0.0.1:5173",
                        "http://localhost:5174", "http://127.0.0.1:5174"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}