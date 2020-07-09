package com.hummer.dao.monitor;

import com.alibaba.druid.stat.DruidStatManagerFacade;
import com.google.common.base.Strings;
import com.hummer.core.PropertiesContainer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author bingy
 */
@RestController
public class DruidStatController {
    @GetMapping("/druid/stat")
    public Object druidStat(@RequestParam("user") String user
            , @RequestParam("password") String password
            , @RequestParam(value = "includeSql", defaultValue = "false") String includeSql) {
        if (Strings.isNullOrEmpty(user) || Strings.isNullOrEmpty(password)) {
            return null;
        }
        String userName = PropertiesContainer.valueOfString("druid.stat.user", "admin");
        String pwd = PropertiesContainer.valueOfString("druid.stat.user.password", "123456");
        if (!user.equals(userName) || !password.equals(pwd)) {
            return null;
        }

        boolean showSql = Boolean.TRUE.equals(includeSql);

        return DruidStatManagerFacade
                .getInstance()
                .getDataSourceStatDataList(showSql);
    }
}
