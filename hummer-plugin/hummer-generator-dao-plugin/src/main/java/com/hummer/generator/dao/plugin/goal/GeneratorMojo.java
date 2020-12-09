package com.hummer.generator.dao.plugin.goal;

import com.hummer.generator.dao.plugin.generator.Generator;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * GeneratorGoal
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2020</p>
 * @date 2020/12/9 14:55
 */

@Mojo(name = "generator", defaultPhase = LifecyclePhase.COMPILE)
public class GeneratorMojo extends AbstractMojo {

    private static final Logger LOGGER = LoggerFactory.getLogger(GeneratorMojo.class);

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        LOGGER.debug("hummer-generator-dao-plugin execute start === ");
        generatorDao();
    }

    private void generatorDao() {
        try {
            Generator.main(null);
        } catch (Exception e) {
            LOGGER.error("hummer-generator-dao-plugin generator start === ", e);
        }
    }
}
