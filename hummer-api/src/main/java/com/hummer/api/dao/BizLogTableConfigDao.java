package com.hummer.api.dao;


import com.hummer.api.po.BizLogTableConfigPo;
import com.hummer.dao.annotation.DaoAnnotation;

import java.util.Collection;

/**
 * @Author: lee
 * @version:1.0.0
 * @Date: 2018/12/18 15:58
 **/
@DaoAnnotation
public interface BizLogTableConfigDao {
    /**
     * query all table configuration
     *
     * @return BizLogTableConfigPo
     * @author liguo
     * @date 2018/12/18 15:59
     * @version 1.0.0
     **/
    Collection<BizLogTableConfigPo> getAllByTableName();
}
