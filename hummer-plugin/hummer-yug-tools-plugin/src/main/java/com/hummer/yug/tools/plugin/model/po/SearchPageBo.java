package com.hummer.yug.tools.plugin.model.po;

import com.hummer.common.utils.ObjectCopyUtils;
import com.hummer.rest.model.request.ResourcePageReqDto;
import lombok.Getter;
import lombok.Setter;

/**
 * @author chen wei
 */
@Getter
@Setter
public class SearchPageBo<T> {

    private Integer pageNum;

    private Integer pageSize;

    private T data;

    public Integer getCurrentIndex() {
        if (pageNum == null || pageSize == null) {
            return null;
        }

        return (pageNum - 1) * pageSize;
    }


    public static <R, T> SearchPageBo<T> builder(ResourcePageReqDto<R> reqDto, T t) {
        SearchPageBo<T> pageBo = new SearchPageBo<>();
        pageBo.setPageNum(reqDto.getPageNumber());
        pageBo.setPageSize(reqDto.getPageSize());
        if (reqDto.getQueryObject() != null && t != null) {
            t = (T) ObjectCopyUtils.copy(reqDto.getQueryObject(), t.getClass());
        }
        pageBo.setData(t);
        return pageBo;

    }
}
