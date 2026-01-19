package com.fei.examsys.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.fei.examsys.mapper")
public class MybatisPlusConfiguration {

}