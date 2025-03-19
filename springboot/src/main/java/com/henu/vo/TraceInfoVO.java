package com.henu.vo;

import com.henu.dao.ManagerDao;
import com.henu.entity.Manager;
import com.henu.entity.TraceInfo;
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
    private static ManagerDao managerDao;
    private Integer managerId;
    private String managerName;
    private String phone;
    private String traceCode;
    private String productName;
    private String productImage;
    private String productDate;
    private String specification;
    private String productionAddress;
    public TraceInfoVO(TraceInfo traceInfo) {
        this.managerId = traceInfo.getManager();
        this.traceCode = traceInfo.getTraceCode();
        this.productName = traceInfo.getProductName();
        this.productImage = traceInfo.getProductImage();
        this.productDate = traceInfo.getProductDate();
        this.specification = traceInfo.getSpecification();
        this.productionAddress = traceInfo.getProductionAddress();

        if (managerDao != null) {
            Manager manager = managerDao.selectById(this.managerId);
            if (manager != null) {
                this.managerName = manager.getManagerName();
                this.phone = manager.getPhone();
            }
        }
    }

    @Autowired
    public void setManagerDao(ManagerDao managerDao) {
        TraceInfoVO.managerDao = managerDao;
    }
}
