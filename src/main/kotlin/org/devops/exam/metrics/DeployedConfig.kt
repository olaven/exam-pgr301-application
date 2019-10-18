package org.devops.exam.metrics

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

//class will only run when profile "local" is set
@Profile("deployed")
@Configuration
class DeployedConfig
