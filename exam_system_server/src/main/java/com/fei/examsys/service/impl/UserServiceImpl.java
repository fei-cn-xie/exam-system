package com.fei.examsys.service.impl;

import com.fei.examsys.entity.User;
import com.fei.examsys.mapper.UserMapper;
import com.fei.examsys.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.springframework.stereotype.Service;

/**
 * 用户Service实现类
 * 实现用户相关的业务逻辑
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

} 