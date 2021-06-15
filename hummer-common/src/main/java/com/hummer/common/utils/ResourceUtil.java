package com.hummer.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author edz
 */
@Slf4j
public class ResourceUtil {
    private static final String CLASSPATH_PREFIX = "classpath:";

    /**
     * Returns the URL of the resource on the classpath.
     *
     * @param resource The resource to find
     * @return The resource
     * @throws IOException If the resource cannot be found or read
     */
    public static URL getResourceUrl(String resource) throws IOException {
        if (resource.startsWith(CLASSPATH_PREFIX)) {
            String path = resource.substring(CLASSPATH_PREFIX.length());

            ClassLoader classLoader = ResourceUtil.class.getClassLoader();

            URL url = (classLoader != null ? classLoader.getResource(path) : ClassLoader.getSystemResource(path));
            if (url == null) {
                throw new FileNotFoundException("Resource " + resource + " does not exist");
            }

            return url;
        }

        try {
            return new URL(resource);
        } catch (MalformedURLException ex) {
            return new File(resource).toURI().toURL();
        }
    }

    /**
     * Returns the URL of the resource on the classpath.
     *
     * @param loader   The classloader used to load the resource
     * @param resource The resource to find
     * @return The resource
     * @throws IOException If the resource cannot be found or read
     */
    public static URL getResourceUrl(ClassLoader loader, String resource) throws IOException {
        URL url = null;
        if (loader != null) {
            url = loader.getResource(resource);
        }
        if (url == null) {
            url = ClassLoader.getSystemResource(resource);
        }
        if (url == null) {
            throw new IOException("Could not find resource " + resource);
        }
        return url;
    }

    /**
     * Returns a resource on the classpath as a Stream object.
     *
     * @param resource The resource to find
     * @return The resource
     * @throws IOException If the resource cannot be found or read
     */
    public static InputStream getResourceAsStream(String resource) throws IOException {
        ClassLoader loader = ResourceUtil.class.getClassLoader();
        return getResourceAsStream(loader, resource);
    }

    /**
     * Returns a resource on the classpath as a Stream object.
     *
     * @param loader   The classloader used to load the resource
     * @param resource The resource to find
     * @return The resource
     * @throws IOException If the resource cannot be found or read
     */
    public static InputStream getResourceAsStream(ClassLoader loader, String resource) throws IOException {
        InputStream in = null;
        if (loader != null) {
            in = loader.getResourceAsStream(resource);
        }
        if (in == null) {
            in = ClassLoader.getSystemResourceAsStream(resource);
        }
        if (in == null) {
            throw new IOException("Could not find resource " + resource);
        }
        return in;
    }

    /**
     * Returns a resource on the classpath as a Properties object.
     *
     * @param resource The resource to find
     * @return The resource
     * @throws IOException If the resource cannot be found or read
     */
    public static Properties getResourceAsProperties(String resource) throws IOException {
        ClassLoader loader = ResourceUtil.class.getClassLoader();
        return getResourceAsProperties(loader, resource);
    }

    /**
     * Returns a resource on the classpath as a Properties object.
     *
     * @param loader   The classloader used to load the resource
     * @param resource The resource to find
     * @return The resource
     * @throws IOException If the resource cannot be found or read
     */
    public static Properties getResourceAsProperties(ClassLoader loader, String resource) throws IOException {
        Properties props = new Properties();
        InputStream in = getResourceAsStream(loader, resource);
        props.load(in);
        in.close();
        return props;
    }

    /**
     * Returns a resource on the classpath as a Reader object.
     *
     * @param resource The resource to find
     * @return The resource
     * @throws IOException If the resource cannot be found or read
     */
    public static InputStreamReader getResourceAsReader(String resource, String charsetName) throws IOException {
        return new InputStreamReader(getResourceAsStream(resource), charsetName);
    }

    /**
     * Returns a resource on the classpath as a Reader object.
     *
     * @param loader   The classloader used to load the resource
     * @param resource The resource to find
     * @return The resource
     * @throws IOException If the resource cannot be found or read
     */
    public static Reader getResourceAsReader(ClassLoader loader, String resource, String charsetName)
            throws IOException {
        return new InputStreamReader(getResourceAsStream(loader, resource), charsetName);
    }

    /**
     * Returns a resource on the classpath as a File object.
     *
     * @param resource The resource to find
     * @return The resource
     * @throws IOException If the resource cannot be found or read
     */
    public static File getResourceAsFile(String resource) throws IOException {
        return new File(getResourceUrl(resource).getFile());
    }

    /**
     * Returns a resource on the classpath as a File object.
     *
     * @param url The resource url to find
     * @return The resource
     */
    public static File getResourceAsFile(URL url) {
        return new File(url.getFile());
    }

    /**
     * Returns a resource on the classpath as a File object.
     *
     * @param loader   The classloader used to load the resource
     * @param resource The resource to find
     * @return The resource
     * @throws IOException If the resource cannot be found or read
     */
    public static File getResourceAsFile(ClassLoader loader, String resource) throws IOException {
        return new File(getResourceUrl(loader, resource).getFile());
    }

    /**
     * get package include class
     *
     * @param packageName package name
     * @return
     */
    public static Set<Class<?>> getClassesByPackageName(String packageName) {
        Set<Class<?>> classes = new HashSet<>(16);
        String packageDirName = packageName.replace('.', '/');
        Enumeration<URL> dirs;
        try {
            dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
            while (dirs.hasMoreElements()) {
                URL url = dirs.nextElement();
                String protocol = url.getProtocol();
                if ("file".equals(protocol)) {
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    findAndAddClassesInPackageByFile(packageName.replace("/", ".")
                            , filePath, true, classes);
                } else if ("jar".equals(protocol)) {
                    JarFile jar;
                    try {
                        jar = ((JarURLConnection) url.openConnection()).getJarFile();
                        Enumeration<JarEntry> entries = jar.entries();
                        while (entries.hasMoreElements()) {
                            JarEntry entry = entries.nextElement();
                            String name = entry.getName();
                            if (name.charAt(0) == '/') {
                                name = name.substring(1);
                            }
                            if (name.startsWith(packageDirName)) {
                                int idx = name.lastIndexOf('/');
                                if (idx != -1) {
                                    packageName = name.substring(0, idx).replace('/', '.');
                                }
                                if ((idx != -1) || true) {
                                    if (name.endsWith(".class") && !entry.isDirectory()) {
                                        String className = name.substring(packageName.length() + 1, name.length() - 6);
                                        try {
                                            classes.add(Class.forName(packageName + '.' + className));
                                        } catch (ClassNotFoundException e) {
                                            log.error("getClassesByPackageName fail", e);
                                            System.exit(-1);
                                        }
                                    }
                                }
                            }
                        }
                    } catch (IOException e) {
                        log.error("getClassesByPackageName fail", e);
                        System.exit(-1);
                    }
                }
            }
        } catch (IOException e) {
            log.error("getClassesByPackageName fail", e);
            System.exit(-1);
        }

        return classes;
    }

    public static void findAndAddClassesInPackageByFile(String packageName
            , String packagePath
            , final boolean recursive
            , Set<Class<?>> classes) {
        File dir = new File(packagePath);
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        File[] dirfiles = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return (recursive && file.isDirectory()) || (file.getName().endsWith(".class"));
            }
        });
        for (File file : dirfiles) {
            if (file.isDirectory()) {
                findAndAddClassesInPackageByFile(packageName + "." + file.getName(),
                        file.getAbsolutePath(),
                        recursive,
                        classes);
            } else {
                String className = file.getName().substring(0, file.getName().length() - 6);
                try {
                    classes.add(Class.forName(packageName + '.' + className));
                } catch (ClassNotFoundException e) {
                    log.error("getClassesByPackageName fail", e);
                }
            }
        }
    }

    public static Object tryInstance(Class<?> cl, Object... args) {
        try {
            Constructor c0 = cl.getDeclaredConstructor();
            c0.setAccessible(true);
            return c0.newInstance(args);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T tryInstance(Class<?> cl, Class<?> target, Object... args) {
        return (T) tryInstance(cl, args);
    }

    public static <S> ServiceLoader<S> getServiceLoader(Class<S> clazz) {
        return ServiceLoader.load(clazz, clazz.getClassLoader());
    }
}
