package com.fei.examsys.service.impl;

import com.fei.examsys.entity.ExamRecord;
import com.fei.examsys.mapper.ExamRecordMapper;
import com.fei.examsys.service.ExamService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


/**
 * 考试服务实现类
 */
@Service
@Slf4j
public class ExamServiceImpl extends ServiceImpl<ExamRecordMapper, ExamRecord> implements ExamService {

} 