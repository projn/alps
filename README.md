# ALPS说明

ALPS是一款基于Spring Boot和Spring Cloud的微服务集成框架，其实现目标是为了减少编写微服
务代码工作量。  

## 框架特性
1. 将每个微服务所特有的功能，如服务发现、健康监测等功能与业务功能加以分离，将微服务所共有的
功能抽象成为服务加载器，将业务逻辑变为模块；
2. 可以基于Swagger API文档自动生成业务模块代码；
3. 提供常用的第三方组建的使用方法，减少程序编码。

## 框架构成
### 微服务加载器 alpsmicoserivce
1. 提供了基于HTTP（REST API）、WebSocket（长连接消息推送）、
Message Queue（处理内外部消息调用）的服务模型；
2. 集成了Spring Cloud组件（健康监测-Actuator、服务发现治理-Consul、调用链-Zipkin、
配置中心-config、负载均衡-feign）；
3. 提供了自动化部署脚本。

### 服务模块程程器 alpsgenerator
1. 支持Swagger 2.0和Swagger 3.0 API标准；
2. 增加tag标签，生成器会根据tag后的标记生成不同业务模块，标记对应关系（http -- HTTP、  
ws -- WebSocket、msg -- RocketMQ Msg）；
3. 可根据API文档中呢绒自动添加注释和枚举类型声明。

### 三方组件bean配置包 alps-bean
1. 提供了常用的三方组件的配置，基于JavaConfig，该包会随着框架发展增加对各种组件支持。  

### 三方组件bean操作包alps-dao
1. 提供了常用的三方组件的访问方法。

### 单元测试包 alps-test
1. 集成了java单元测试常用模块。

## 使用方法

1. 根据自己业务需要下载对应的代码生成器，下载地址：https://github.com/projn/alps/releases；

2. 使用Swagger编写yaml格式的API文档，文档格式Swagger， 文档地址：https://swagger.io/resources/open-api/，本项目支持OSA 2.0和OSA 3.0编写的API，并在其基础上增加了一些衍生用法，具体使用方式可参见范例项目， 项目地址：https://github.com/projn/sample/tree/master/alps；

3. 根据需要编写模块生成配置文件generator_config.xml，具体格式参见范例项目，文件地址：https://raw.githubusercontent.com/projn/sample/master/alps/generator/generator_config-sample.xml；

4. 使用代码生成器模块生成配置文件生成模块代码，

   `java -jar alpsgenerator-X.X.X.jar /you/path/to/generator_config.xml`

5. 使用代码生成器中目录alpsdev-init中的ini.sh将编译模块所需要的jar安装到本地maven库或者远端Nexus maven库。

### 生成项目包含目录文件说明

   1. generator用来存放生成代码所需的配置文件和API文档；
   2. install中存放了项目部署安装做需要的Dockerfile范例以及安装包范例，其中文件install/alpsmicroservice-install/alpsmicroservice/alpsmicroservice-X.X.X.jar是模块加载器，用户也可使用该文件对已编写好的功能模块进行调试。模块加载器有两种使用方式，一种是加载本地配置启动，具体配置信息参见install/alpsmicroservice-install/config/single目录中文件，程序启动命令参见文件alpsmicroserviced-single；另一种是结合配置中心加载远端配置，具体配置信息参见install/alpsmicroservice-install/config/cloud目录中文件，程序启动命令参见文件alpsmicroserviced-cloud；
   3. maven-plugin-config中存放maven插件的配置文件，预制了checkstyle和findbugs配置文件；
   4. modules中存放了生成的功能模块；
   5. test中存放了调试模块功能所需的配置文件，用户可根据自己需要，将install/alpsmicroservice-install/alpsmicroservice/config中的配置文件拷贝到该目录并修改配置，从而实现单元测试和整体调试，具体使用方法参考范例项目。
   6. Jenkinsfile是预制的流水线文件，用户可使用Jenkins加载该流水线实现CI/CD，详情见代码；
   7. pom.xml是生成的maven工程文件，中间并没有添加生成的模块信息，请手动添加以实现以整体编译。

## CI/CD流水线搭建

该流程需借助PRON下popigai项目，项目地址：https://github.com/projn/popigai

1. 使用popigai项目中master分支中的openjdk-install和jenkins-install搭建部署环境用流水线，我们将该环境简称Deploy环境，该环境将用来搭建整套CI/CD所需环境，修改jenkins-install中的配置文件以制定Jenkins访问地址，安装好后在Deploy环境使用service jenkinsd start启动Jenkins，更多配置使用方法见install.sh和jenkinsd文件。启动Jenkins还需要安装流水线必须插件；

   ```
   Dashboard View/SSH Agent/github/git/gitlab/Publish Over SSH/SSH/BlueOcean/SSH Pipeline Steps plugin
   ```

2. 将popigai项目中repo和build分支代码拉到本地，对其中Jenkinsfile文件进行修改，将Build环境和Repo环境参数填好，然后将修改过的代码提交到自己本地的git或者gitlab中，打开Deploy环境中的Jenkins，使用BlueOcean创建流水线，选择repo和build代码库，运行流水线部署Build环境和Repo环境。Build环境包含openjdk、maven、jenkins、git、docker-ce、docker-compose等组件，用来编译打包代码；Repo环境包含nexus用来存储maven jar包和编译打包生成的二进制文件，以及Harbor用来存储编译打包生成的Docker镜像和Charts。
