# Agentic4J

A Manager to manage communication and work between multiple AI Agents

## How the idea came about

I have been exploring Agentic workflows and multi-agent applications since some time and was trying to see how we can have a framework which manages the same.
The key building block which was required was a communication channel which could support logical blocks which act on messages, maintain a global state and support pub/sub model.
Having created a port of Redux for Java called [Dux4j](https://github.com/compscikaran/dux4j) I realized that it would fit this use-case perfectly and hence Agentic4j was born.


## [Get Started](https://github.com/compscikaran/agentic4j/wiki)

## What is Agentic4j

Key Features -
1. Create a simple Agent graph defining how agents should talk to each other
2. Uses Redux to provide a unified communication channel ensuring agents recieve only relevant messages in the conversation
3. Host a centralized memory management supporting all agents
