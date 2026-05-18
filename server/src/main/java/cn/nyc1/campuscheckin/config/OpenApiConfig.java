package cn.nyc1.campuscheckin.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    public static final String AUTHORIZATION_SCHEME = "Authorization";

    @Bean
    public OpenAPI campusCheckinOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("CampusCheckin 智课签 API")
                        .version("1.0.0")
                        .description("校园课程考勤签到 APP 第一阶段 REST API，供 Android 客户端和 Apifox 调试使用。"))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("本机默认端口"),
                        new Server().url("http://localhost:8081").description("本机备用端口"),
                        new Server().url("http://10.0.2.2:8080").description("Android 模拟器访问宿主机默认端口"),
                        new Server().url("http://10.0.2.2:8081").description("Android 模拟器访问宿主机备用端口")
                ))
                .components(new Components()
                        .addSecuritySchemes(AUTHORIZATION_SCHEME,
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.APIKEY)
                                        .in(SecurityScheme.In.HEADER)
                                        .name("Authorization")
                                        .description("SA-Token 登录后返回的 token，直接填入 token 字符串。")));
    }
}
