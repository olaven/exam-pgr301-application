package org.devops.exam.metrics

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Profile("local")
@Configuration
class LocalConfig 