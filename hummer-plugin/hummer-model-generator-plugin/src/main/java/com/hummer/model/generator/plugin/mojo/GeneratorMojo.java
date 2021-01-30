package com.hummer.model.generator.plugin.mojo;

import com.hummer.model.generator.plugin.generator.Generator;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * GeneratorMojo
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2021</p>
 * @date 2021/1/28 15:48
 */

@Mojo(name = "generatorMojo", defaultPhase = LifecyclePhase.COMPILE)
public class GeneratorMojo extends AbstractMojo {

    @Parameter(property = "configPath")
    private String configPath;


    @Override
    public void execute() {
        System.out.print("model generator start ... ");
        System.out.println(configPath);
        try {
            Generator.execute(configPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
