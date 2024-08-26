package com.jcortes.deco.app

import org.springframework.context.annotation.Configuration
import org.springframework.transaction.annotation.EnableTransactionManagement

@Configuration
@EnableTransactionManagement(proxyTargetClass = true)
class ServiceConfig {


}