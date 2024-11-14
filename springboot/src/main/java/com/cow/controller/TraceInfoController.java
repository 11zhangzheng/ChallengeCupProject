package com.cow.controller;

import com.alibaba.fastjson.JSONObject;
import com.cow.dao.TraceInfoMapper;
import com.cow.entity.TraceInfo;
import com.cow.vo.Result;
import com.cow.vo.TraceInfoVO;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/trace")

public class TraceInfoController {
    @Autowired
    private TraceInfoMapper traceInfoMapper;
    @GetMapping("/getTraceInfoById")
    public Result getTraceInfoById(@Param("id") Integer id){
        TraceInfo traceInfo = traceInfoMapper.selectById(id);
        TraceInfoVO traceInfoVO = new TraceInfoVO(traceInfo);
        if (traceInfo != null){
            return Result.ok(traceInfoVO);
        }
        return Result.fail();
    }
}
