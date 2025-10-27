package org.cedon.kqnbot.bootstrap;

import org.cedon.kqnbot.template.TemplateResourceExtractor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class Initializer implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments arg) {
        TemplateResourceExtractor.init();
    }
}
