package com.hummer.rest.model.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;

import java.util.Collections;
import java.util.List;

/**
 * ResourcePageRespDto
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2020</p>
 * @date 2020/6/3 14:04
 */
@ApiModel
@Data
public class ResourcePageRespDto<T> {

    public static final ResourcePageRespDto<?> EMPTY = new ResourcePageRespDto<>();

    @ApiModelProperty("当前页码")
    private Integer pageNumber;

    @ApiModelProperty("单页记录数")
    private Integer pageSize;

    @ApiModelProperty("总条数")
    private Integer totalCount = 0;

    @ApiModelProperty("总页码")
    private Integer totalPage;

    @ApiModelProperty("数据记录")
    private List<T> dataItem;

    public List<T> getDataItem() {
        return CollectionUtils.isEmpty(dataItem)
                ? Collections.emptyList()
                : dataItem;
    }

    public Integer getTotalPage() {
        if (totalPage != null) {
            return totalPage;
        }
        if (pageSize == null || totalCount == null) {
            return 0;
        }
        return totalCount % pageSize == 0 ? (totalCount / pageSize) : (totalCount / pageSize + 1);
    }
}

