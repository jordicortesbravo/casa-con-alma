SHELL=/bin/sh

listfiles = $(wildcard $(strip $1)$(strip $2)) $(foreach d,$(wildcard $(strip $1)*),$(call listfiles,$d/,$(strip $2)))

ifdef OS
	TRUE := powershell out-null
	USER := user
	UID := 1000
	GID := 1000
	cp = powershell (Copy-Item $1 -Destination $2)
	cpr = powershell (Copy-Item $1 -Destination $2 -Recurse -force)
	rm = powershell (Remove-Item -Force -Recurse -ErrorAction Ignore $1)
	touch = powershell "if(Test-Path $1) { (Get-ChildItem $1).LastWriteTime = Get-Date } else { [void](New-Item -ItemType file $1) }"
	sed = powershell "(Get-Content $1).replace('$(strip $2)', '$(strip $3)') | Set-Content $1"
	mkdir = powershell "[void](New-Item $1 -ItemType Directory -ErrorAction SilentlyContinue)"
	shellv = $(shell powershell "Set-Item -Path env:$(strip $1) -Value '$(strip $2)'; $3")
	taskId = $(shell powershell "Write-Output $1 | ForEach-Object { $$_.Split('/')[2] }")
	xmlparse = $(shell powershell "xml $1")
else
	TRUE := true
	USER := $(shell whoami)
	UID := $(shell id -u)
	GID := $(shell id -g)
	cp = cp $1 $2
	cpr = cp -R $1 $2
	rm = rm -rf $1
	touch = touch $1
	sed = sed -i 's>$(strip $2)>$(strip $3)>g' $(strip $1)
	mkdir = mkdir -p $1
    shellv = $(shell export $(strip $1)="$(strip $2)"; $3)
	taskId = $(shell echo $1 | cut -d'/' -f 3)
	xmlparse = $(shell xmlstarlet $1)

	UNAME_S := $(shell uname -s)
	ifeq ($(UNAME_S),Darwin)
        sed = sed -i .back 's,$(strip $2),$(strip $3),g' $(strip $1)
    endif
endif

BASE_PATH := $(dir $(abspath $(lastword $(MAKEFILE_LIST))))
BUILD_DIR := $(BASE_PATH)target
IAC_DIR := $(BASE_PATH)iac
DOCKER_DIR := $(BASE_PATH)docker
AWS_DIR := $(BUILD_DIR)/aws
AWS_PROFILE := YE-Devops

SONAR_URL ?= https://sonarqube.yaencontre.com

PROJECT_NAME := $(call xmlparse, select --template --value-of _:project/_:artifactId $(BASE_PATH)pom.xml)
PROJECT_VERSION := $(call xmlparse, select --template --value-of _:project/_:properties/_:revision $(BASE_PATH)pom.xml)
DOCKER_IMAGE := $(PROJECT_NAME):$(PROJECT_VERSION)
JAR_FILE :=  $(PROJECT_NAME)-$(PROJECT_VERSION).jar
LOCAL_URL := http://localhost:8080/content-service/actuator

PROJECT_NAME_COMPOSE_DEV := $(PROJECT_NAME)-dev
PROJECT_NAME_COMPOSE_LOCAL := $(PROJECT_NAME)-local

DEFAULT_CONTAINER_DEV := localstack
DEFAULT_CONTAINER_LOCAL := app
DEFAULT_CONTAINER_AWS := app

### Default stages
all: help

.PHONY: clean
clean:
	@echo $@
	@$(call rm, $(BUILD_DIR))
	@$(call rm, $(IAC_DIR)/node_modules)

.PHONY: clean/all
clean/all: clean dev/destroy local/destroy
	@echo $@
	@docker image remove $(DOCKER_IMAGE) 2> error.log || $(TRUE)
	@$(call rm, error.log)
	@$(call rm, $(BUILD_DIR))

# AWS configuration
aws/configure/yedevops $(AWS_DIR)/configure.yedevops.flag:
	@echo $@
	@$(call mkdir, $(AWS_DIR))
ifdef CI
	@aws configure set region $(AWS_DEFAULT_REGION) --profile YE-Devops
	@aws configure set aws_access_key_id $(YE_DEVOPS_AWS_ACCESS_KEY_ID) --profile YE-Devops
	@aws configure set aws_secret_access_key $(YE_DEVOPS_AWS_SECRET_ACCESS_KEY) --profile YE-Devops
endif
	@$(call touch, $@)

.PHONY: login
login $(BUILD_DIR)/settings.xml: $(AWS_DIR)/configure.yedevops.flag pom.xml settings.xml
	@echo $@
	@$(eval CODEARTIFACT_AUTH_TOKEN := $(shell aws codeartifact get-authorization-token --domain jht --query authorizationToken --duration-seconds 900 --output text $(AWS_PROFILE)))
	@$(call mkdir, $(BUILD_DIR))
	@$(call cp, settings.xml, $(BUILD_DIR)/settings.xml)
	@$(call sed, $(BUILD_DIR)/settings.xml, $${CODEARTIFACT_AUTH_TOKEN}, $(CODEARTIFACT_AUTH_TOKEN))

### Build
SKIP_TESTS ?= $(if $(filter yes, $(skip-tests)),-Dmaven.test.skip=true,)
build: $(BUILD_DIR)/$(JAR_FILE)
$(BUILD_DIR)/$(JAR_FILE): $(call listfiles, src/, *.*) $(BUILD_DIR)/settings.xml
	@echo $@
	@mvn package --settings $(BUILD_DIR)/settings.xml --batch-mode $(SKIP_TESTS)
	@$(call cp, $(BASE_PATH)$(PROJECT_NAME)-public/target/$(PROJECT_NAME)-public-$(PROJECT_VERSION).jar, $(BUILD_DIR)/$(JAR_FILE))

libs/deploy: AWS_PROFILE :=
libs/deploy: $(BUILD_DIR)/settings.xml
	@echo $@
	@mvn deploy --settings $(BUILD_DIR)/settings.xml --batch-mode $(SKIP_TESTS)

### Test
.PHONY: test
test: $(BUILD_DIR)/settings.xml
	@echo $@
	@export TESTCONTAINERS_RYUK_DISABLED=true && mvn test --settings $(BUILD_DIR)/settings.xml --batch-mode

.PHONY: verify
verify: $(BUILD_DIR)/settings.xml
ifeq (,$(SONAR_LOGIN))
	$(error Sonar login not found. Check your SONAR_LOGIN env variable)
endif
	@echo $@
	@export TESTCONTAINERS_RYUK_DISABLED=true && mvn verify sonar:sonar --settings $(BUILD_DIR)/settings.xml --batch-mode -Dsonar.host.url=$(SONAR_URL) -Dsonar.qualitygate.wait=true -Dsonar.login=$(SONAR_LOGIN)

### Docker
build/dockerfile: $(BUILD_DIR)/Dockerfile
$(BUILD_DIR)/Dockerfile: $(DOCKER_DIR)/Dockerfile pom.xml
	@echo $@
	@$(call mkdir, $(BUILD_DIR))
	@$(call cp, $(DOCKER_DIR)/Dockerfile, $(BUILD_DIR))
	@$(call sed, $(BUILD_DIR)/Dockerfile, $${JAR_FILE}, $(JAR_FILE))

build/image: $(BUILD_DIR)/build-image.flag
$(BUILD_DIR)/build-image.flag: $(BUILD_DIR)/$(JAR_FILE) $(BUILD_DIR)/Dockerfile
	@echo $@
	@docker build --tag $(DOCKER_IMAGE) $(BUILD_DIR)
	@$(call touch, $@)

build/dev-compose: $(BUILD_DIR)/docker-compose-dev.yml
$(BUILD_DIR)/docker-compose-dev.yml: $(DOCKER_DIR)/docker-compose-dev.yml
	@echo $@
	@$(call mkdir, $(BUILD_DIR))
	@$(call cp, $(DOCKER_DIR)/docker-compose-dev.yml, $(BUILD_DIR))

build/local-compose: $(BUILD_DIR)/docker-compose-local.yml
$(BUILD_DIR)/docker-compose-local.yml: $(DOCKER_DIR)/docker-compose-local.yml
	@echo $@
	@$(call mkdir, $(BUILD_DIR))
	@$(call mkdir, $(BUILD_DIR))
	@$(call sed, $(BUILD_DIR)/docker-compose-local.yml, $${DOCKER_IMAGE}, $(DOCKER_IMAGE))

### Dev environment
dev dev/start: DOCKER_CMD := up -d --build
dev/stop: DOCKER_CMD := stop
dev/destroy: DOCKER_CMD := down
dev/status: DOCKER_CMD := ps
dev/inspect: container ?= $(DEFAULT_CONTAINER_DEV)
dev/inspect: DOCKER_CMD := exec $(container) sh
dev/logs: DOCKER_CMD := logs
dev/inspect dev/logs: dev/start
dev dev/start dev/inspect dev/logs dev/status dev/stop dev/destroy: build/dev-compose
	@echo $@
	@echo $(BUILD_DIR)/docker-compose-dev.yml
	@docker-compose --file $(BUILD_DIR)/docker-compose-dev.yml --project-name $(PROJECT_NAME_COMPOSE_DEV) $(DOCKER_CMD)

### Local environment
local local/start: build/image build/local-compose
	@echo $@
	@docker-compose --file $(BUILD_DIR)/docker-compose-local.yml --project-name $(PROJECT_NAME_COMPOSE_LOCAL) up -d
	@echo $(LOCAL_URL)

local/stop: DOCKER_CMD := stop
local/destroy: DOCKER_CMD := down
local/status: DOCKER_CMD := ps
local/inspect: container ?= $(DEFAULT_CONTAINER_LOCAL)
local/inspect: DOCKER_CMD := exec $(container) sh
local/logs: DOCKER_CMD := logs
local/inspect local/logs: local/start
local/inspect local/logs local/status local/stop local/destroy: build/local-compose
	@echo $@
	@docker-compose --file $(BUILD_DIR)/docker-compose-local.yml --project-name $(PROJECT_NAME_COMPOSE_LOCAL) $(DOCKER_CMD)

### IAC
stack ?= dev
STACK ?= $(stack)
localPortNumber ?= 9999
LOCAL_PORT_NUMBER ?= $(localPortNumber)
INTERACTIVE ?= $(if $(filter no, $(interactive)),--yes --skip-preview --non-interactive,)
BUILD_STAGE ?= $(if $(filter yes, $(skip-build)),,build)

aws/deps: $(IAC_DIR)/node_modules/.package-lock.json
$(IAC_DIR)/node_modules/.package-lock.json: $(AWS_DIR)/configure.yedevops.flag $(IAC_DIR)/package.json
	@echo $@
	@(npm run co:login --prefix $(IAC_DIR) && npm install --prefix $(IAC_DIR))
	@$(call touch, $@)

aws/deps/version:
	@npm run co:login --prefix $(IAC_DIR) && ncu --color --cwd $(IAC_DIR)

aws/preview: PULUMI_CMD = preview
aws aws/start aws/stop aws/deploy: PULUMI_CMD = up $(INTERACTIVE)
aws/destroy: PULUMI_CMD=destroy $(INTERACTIVE)
aws/refresh: PULUMI_CMD=refresh

aws aws/preview aws/deploy: build/dockerfile $(BUILD_STAGE)
aws aws/preview aws/deploy aws/destroy aws/refresh: aws/deps
	@pulumi $(PULUMI_CMD) --stack $(STACK) --cwd $(IAC_DIR)

aws/start: DESIRED_COUNT = 1
aws/stop: DESIRED_COUNT = 0
aws/start aws/stop: aws/deps
	$(eval AWS_REGION := $(shell pulumi config get aws:region --cwd $(IAC_DIR) --stack $(STACK)))
	$(eval CLUSTER_NAME := $(shell pulumi stack output ecsClusterName --cwd $(IAC_DIR) --stack $(STACK)))
	$(eval SERVICE_NAME := $(shell pulumi stack output ecsServiceName --cwd $(IAC_DIR) --stack $(STACK)))
	@aws ecs update-service --cluster $(CLUSTER_NAME) --service $(SERVICE_NAME) --desired-count $(DESIRED_COUNT) --region $(AWS_REGION)

### AWS utils
.PHONY: aws/inspect
aws/inspect: container ?= $(DEFAULT_CONTAINER_AWS)
aws/inspect:
	$(eval AWS_REGION := $(shell pulumi config get aws:region --cwd $(IAC_DIR) --stack $(STACK)))
	$(eval CLUSTER_NAME := $(shell pulumi stack output ecsClusterName --cwd $(IAC_DIR) --stack $(STACK)))
	$(eval TASK_ARN := $(shell aws ecs list-tasks --cluster $(CLUSTER_NAME) --region $(AWS_REGION) --query 'taskArns[0]' --output=text))
	@aws ecs execute-command --cluster $(CLUSTER_NAME) --task $(TASK_ARN) --region $(AWS_REGION) --container $(container) --command "/bin/sh" --interactive

.PHONY: aws/logs
aws/logs:
	$(eval AWS_REGION := $(shell pulumi config get aws:region --cwd $(IAC_DIR) --stack $(STACK)))
	$(eval LOG_GROUP := $(shell pulumi stack output ecsLogGroup --cwd $(IAC_DIR) --stack $(STACK)))
	@aws logs tail $(LOG_GROUP) --since 30m --follow --region $(AWS_REGION)

.PHONY: aws/session/jmx aws/session/http aws/session/rds
aws/session/jmx: SESSION_PARAMETERS = "localPortNumber=$(LOCAL_PORT_NUMBER),portNumber=9999"
aws/session/http: SESSION_PARAMETERS = "localPortNumber=8080,portNumber=8080"
aws/session/jmx aws/session/http: DOCUMENT_NAME = "AWS-StartPortForwardingSession"
aws/session/rds: SESSION_PARAMETERS = "host=$(RDS_CLUSTER_ENDPOINT),localPortNumber=5432, portNumber=5432"
aws/session/rds: DOCUMENT_NAME = "AWS-StartPortForwardingSessionToRemoteHost"
aws/session/jmx aws/session/http aws/session/rds:
	$(eval AWS_REGION := $(shell pulumi config get aws:region --cwd $(IAC_DIR) --stack $(STACK)))
	$(eval CLUSTER_NAME := $(shell pulumi stack output ecsClusterName --cwd $(IAC_DIR) --stack $(STACK)))
	$(eval RDS_CLUSTER_ENDPOINT := $(shell pulumi stack output databaseClusterEndpoint --cwd $(IAC_DIR) --stack $(STACK)))
	$(eval TASK_ARN := $(shell aws ecs list-tasks --cluster $(CLUSTER_NAME) --region $(AWS_REGION) --query 'taskArns[0]' --output=text))
	$(eval CONTAINER_ID := $(shell aws ecs describe-tasks --cluster $(CLUSTER_NAME) --tasks $(TASK_ARN) --query 'tasks[].containers[?name == `app`]'.runtimeId --region $(AWS_REGION) --output=text))
	$(eval TASK_ID := $(call taskId, $(TASK_ARN)))
	@aws ssm start-session --target ecs:$(CLUSTER_NAME)_$(TASK_ID)_$(CONTAINER_ID) --document-name $(DOCUMENT_NAME) --parameters $(SESSION_PARAMETERS) --region $(AWS_REGION)

################################################################################################
# Project name, version and help
project-name name:
	@echo $(PROJECT_NAME)

version:
	@echo $(PROJECT_VERSION)

jar-file:
	@echo $(JAR_FILE)

define HELP_TEXT
Usage: make target [parameter=value]

Targets:
    clean                     clean build files
    clean/all                 stops local environments and clean build files

    login                     login to AWS CodeArtifact
    build                     build the jar file
    build/image               build the docker image
    libs/deploy               build and deploy jar files into repository
    test                      test project
    verify                    test and push data to sonar

    dev | dev/start           starts the development environment
    dev/stop                  stops the development environment
    dev/destroy               stops and destroy the development environment
    dev/inspect               goes into local service with prompt. Use container=nginx to change it. Default: localstack.
    dev/status                show docker compose status
    dev/logs                  show containers logs

    local | local/start       starts the local environment
    local/stop                stops the local environment
    local/destroy             stops and destroy the local environment
    local/inspect             goes into local service with prompt. Use container=nginx to change it. Default: app.
    local/status              show docker compose status
    local/logs                show containers logs

    aws/deps                  download dependencies
    aws/deps/version          show updatable dependencies

    aws | aws/deploy          deploys the AWS environment
    aws/start                 start the AWS environment
    aws/stop                  stops the AWS environment
    aws/destroy               destroys the AWS environment
    aws/preview               show project IAC changes
    aws/refresh               refreshes the pulumi state

    aws/inspect               login into aws container for debug pourposes
    aws/logs                  tail container log
    aws/session/jmx           start SSM port forwarding session for jmx protocol
    aws/session/http          start SSM port forwarding session for http protocol
    aws/session/rds           start SSM port forwarding session for rds

    help                      display this help and exit
    name | project-name       print pom's artifactId
    version                   print pom's version
    jar-file                  print pom's jar final name

Parameters:
    skip-tests                If must skip tests during the build. Values: yes | no. Default no.
    skip-build                If must skip the build during the docker targets. Usefull for the CI/CD. Values: yes | no. Default no.
    stack                     The pulumi stack. Default dev.
    interactive               If the pulumi must be interactive. Values: yes | no. Default yes.
    container                 container name to login. Review container list in your docker-compose files and in your fargate config.
    localPortNumber           Local port where starts SSM port forwarding session. Default 9999.

Examples:
    make aws/deploy stack=pro skip-build=yes skip-tests=yes
    make aws/preview interactive=no
    make local/inspect container=app
endef

export HELP_TEXT

help:
ifdef OS
	@powershell '$$env:HELP_TEXT'
else
	@echo "$$HELP_TEXT"
endif
