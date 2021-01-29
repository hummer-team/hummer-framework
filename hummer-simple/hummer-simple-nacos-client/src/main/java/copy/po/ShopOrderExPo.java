package copy.po;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class ShopOrderExPo implements Serializable {
    @ApiModelProperty("订单ID")
    private Long ygfOrderId;

    @ApiModelProperty("店铺ID")
    private Integer shopId;

    @ApiModelProperty("客户端编码")
    private String clientCode;

    @ApiModelProperty("现金支付金额")
    private BigDecimal payCashAmount;

    @ApiModelProperty("现金找零金额")
    private BigDecimal returnCashAmount;

    @ApiModelProperty("订单备注")
    private String managerMemo;

    @ApiModelProperty("操作员用户ID")
    private Long manageUserId;

    @ApiModelProperty("创建时间")
    private Date createdDateTime;

    @ApiModelProperty("创建人")
    private Long createdUserId;

    @ApiModelProperty("最新修改时间")
    private Date lastModifiedDateTime;

    @ApiModelProperty("最新修改人")
    private Long lastModifiedUserId;

    @ApiModelProperty("删除状态")
    private Boolean isDeleted;

    private static final long serialVersionUID = 1L;

    public Long getYgfOrderId() {
        return ygfOrderId;
    }

    public void setYgfOrderId(Long ygfOrderId) {
        this.ygfOrderId = ygfOrderId;
    }

    public Integer getShopId() {
        return shopId;
    }

    public void setShopId(Integer shopId) {
        this.shopId = shopId;
    }

    public String getClientCode() {
        return clientCode;
    }

    public void setClientCode(String clientCode) {
        this.clientCode = clientCode;
    }

    public BigDecimal getPayCashAmount() {
        return payCashAmount;
    }

    public void setPayCashAmount(BigDecimal payCashAmount) {
        this.payCashAmount = payCashAmount;
    }

    public BigDecimal getReturnCashAmount() {
        return returnCashAmount;
    }

    public void setReturnCashAmount(BigDecimal returnCashAmount) {
        this.returnCashAmount = returnCashAmount;
    }

    public String getManagerMemo() {
        return managerMemo;
    }

    public void setManagerMemo(String managerMemo) {
        this.managerMemo = managerMemo;
    }

    public Long getManageUserId() {
        return manageUserId;
    }

    public void setManageUserId(Long manageUserId) {
        this.manageUserId = manageUserId;
    }

    public Date getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(Date createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public Long getCreatedUserId() {
        return createdUserId;
    }

    public void setCreatedUserId(Long createdUserId) {
        this.createdUserId = createdUserId;
    }

    public Date getLastModifiedDateTime() {
        return lastModifiedDateTime;
    }

    public void setLastModifiedDateTime(Date lastModifiedDateTime) {
        this.lastModifiedDateTime = lastModifiedDateTime;
    }

    public Long getLastModifiedUserId() {
        return lastModifiedUserId;
    }

    public void setLastModifiedUserId(Long lastModifiedUserId) {
        this.lastModifiedUserId = lastModifiedUserId;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", ygfOrderId=").append(ygfOrderId);
        sb.append(", shopId=").append(shopId);
        sb.append(", clientCode=").append(clientCode);
        sb.append(", payCashAmount=").append(payCashAmount);
        sb.append(", returnCashAmount=").append(returnCashAmount);
        sb.append(", managerMemo=").append(managerMemo);
        sb.append(", manageUserId=").append(manageUserId);
        sb.append(", createdDateTime=").append(createdDateTime);
        sb.append(", createdUserId=").append(createdUserId);
        sb.append(", lastModifiedDateTime=").append(lastModifiedDateTime);
        sb.append(", lastModifiedUserId=").append(lastModifiedUserId);
        sb.append(", isDeleted=").append(isDeleted);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}