# Agentic4J

A way to define workflows involving multiple AI Agents

## How the idea came about

I was fascinated by [Andrew NG's talk at Sequioa Capital](https://www.youtube.com/watch?v=sal78ACtGTc) and was trying to see if a framework for doing the same existing in Java which was built around Langchang4j which is an unoffical port of its namesake from python ecosystem. The key building block which was required was a communication channel which could support logical blocks which act on messages, maintain a global state and support pub/sub model. And then it struck me such a global functional state manager already exists in the UI World in the form of Redux / Flux. If only there was a port of Redux available in Java and low and behold I wrote the exact same thing around a year ago called [Dux4j](https://github.com/compscikaran/dux4j). (What a coincidence !) Hence was born Agentic4j.


## [Get Started](https://github.com/compscikaran/agentic4j/wiki)

## What is Agentic4j

Key Features -
1. Create a simple Agent graph defining how agents should talk to each other
2. Uses Redux to provide a unified communication channel ensuring agents recieve only relevant messages in the conversation
3. Host a centralized memory management supporting all agents
