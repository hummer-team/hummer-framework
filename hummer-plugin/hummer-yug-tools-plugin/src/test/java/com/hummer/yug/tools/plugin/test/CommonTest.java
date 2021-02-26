package com.hummer.yug.tools.plugin.test;

import com.hummer.common.utils.DateUtil;
import com.hummer.yug.tools.plugin.util.GeneratorUtil;
import com.hummer.yug.tools.plugin.util.GoodsUtil;
import org.junit.Test;

/**
 * description     java类作用描述
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2021</p>
 * @date 2021/2/24 17:07
 */
public class CommonTest {

    @Test
    public void test1() {
        System.out.println(GoodsUtil.confirmGoodsCanReturn(false, false, false
                , false, DateUtil.now()));
        System.out.println(DateUtil.startDateSubtractEndDateOfDay(DateUtil
                .parsingToDate("2021-01-24", DateUtil.DateTimeFormat.F2), DateUtil.now()));
    }

    @Test
    public void test2() {
        System.out.println(GeneratorUtil.generateCode(GeneratorUtil.GeneratorCodePre.YGF_TH_CODE_PREFIX_SHOP));
    }
}
