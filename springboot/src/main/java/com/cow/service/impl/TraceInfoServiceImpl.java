package com.cow.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cow.dao.TraceInfoDao;
import com.cow.entity.TraceInfo;
import com.cow.service.TraceInfoService;
import org.springframework.stereotype.Service;

@Service
public class TraceInfoServiceImpl extends ServiceImpl<TraceInfoDao, TraceInfo> implements TraceInfoService {

}
