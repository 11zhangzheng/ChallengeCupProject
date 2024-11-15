package com.cow.vo;

import com.cow.dao.ManagerDao;
import com.cow.entity.Manager;
import com.cow.entity.TraceInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
@Data
@Component
@NoArgsConstructor
@AllArgsConstructor
public class TraceInfoVO {
    @Autowired
    public ManagerDao ManagerDao;
    private  Integer managerId;
    private String managerName;
    private String phone;
    private String traceCode;
    private String productName;
    private String productImage;
    private String productDate;
    private String specification;
    private String productionAddress;
    public TraceInfoVO(TraceInfo traceInfo){
        this.managerId=traceInfo.getManagerId();
        this.traceCode=traceInfo.getTraceCode();
        this.productName=traceInfo.getProductName();
        this.productImage=traceInfo.getProductImage();
        this.productDate=traceInfo.getProductDate();
        this.specification=traceInfo.getSpecification();
        this.productionAddress=traceInfo.getProductionAddress();
//        Manager manager=ManagerDao.selectById(managerId);
//        this.managerName=manager.getManagerName();
//        this.phone=manager.getPhone();
    }

}
