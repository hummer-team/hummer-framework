package com.hummer.dao.mybatis;

import org.apache.ibatis.io.VFS;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class CustomVFS extends VFS {

    private final ResourcePatternResolver resourceResolver =
            new PathMatchingResourcePatternResolver(getClass().getClassLoader());

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    protected List<String> list(URL url, String forPath) throws IOException {
        Resource[] resources = resourceResolver.getResources("classpath*:" + forPath + "/**/*.class");
        List<String> resourcePaths = new ArrayList<>();
        for (Resource resource : resources) {
            resourcePaths.add(preserveSubpackageName(resource.getURI(), forPath));
        }
        return resourcePaths;
    }

    private static String preserveSubpackageName(final URI uri, final String rootPath) {
        final String uriStr = uri.toString();
        final int start = uriStr.indexOf(rootPath);
        return uriStr.substring(start);
    }

}
