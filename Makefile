JC = javac

SRCDIR = src/
PKGDIR = adam/agent/
MKDIR = mkdir -p
CLSDIR = bin/
CLS = $(addprefix ${CLSDIR}${PKGDIR},AgentSystem.class AgentHandler.class Agent.class PingAgent.class PongAgent.class)

.PHONY: all agentsystem clean

all: ${CLSDIR} agentsystem $(CLS) | ${CLSDIR}

${CLSDIR}${PKGDIR}AgentSystem.class: ${CLSDIR} ${SRCDIR}${PKGDIR}AgentSystem.java | ${CLSDIR}
	$(JC) -d ${CLSDIR} ${SRCDIR}${PKGDIR}AgentSystem.java

${CLSDIR}${PKGDIR}AgentHandler.class: ${CLSDIR} ${SRCDIR}${PKGDIR}AgentHandler.java | ${CLSDIR}
	$(JC) -d ${CLSDIR} ${SRCDIR}${PKGDIR}AgentHandler.java

${CLSDIR}${PKGDIR}Agent.class: ${CLSDIR} ${SRCDIR}${PKGDIR}Agent.java | ${CLSDIR}
	$(JC) -d ${CLSDIR} ${SRCDIR}${PKGDIR}Agent.java

${CLSDIR}${PKGDIR}PingAgent.class: ${CLSDIR} ${SRCDIR}${PKGDIR}PingAgent.java | ${CLSDIR}
	$(JC) -d ${CLSDIR} ${SRCDIR}${PKGDIR}PingAgent.java

${CLSDIR}${PKGDIR}PongAgent.class: ${CLSDIR} ${SRCDIR}${PKGDIR}PongAgent.java | ${CLSDIR}
	$(JC) -d ${CLSDIR} ${SRCDIR}${PKGDIR}PongAgent.java

agentsystem: ${CLSDIR} | ${CLSDIR}
	$(JC) -d ${CLSDIR} ${SRCDIR}${PKGDIR}AgentSystem.java ${SRCDIR}${PKGDIR}AgentHandler.java ${SRCDIR}${PKGDIR}Agent.java \
		${SRCDIR}${PKGDIR}PingAgent.java ${SRCDIR}${PKGDIR}PongAgent.java

${CLSDIR}:
	$(MKDIR) ${CLSDIR}
	$(MKDIR) ${CLSDIR}${PKGDIR}

clean:
	$(RM)r ${CLSDIR}
