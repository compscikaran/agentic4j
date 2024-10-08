<img src="https://github.com/user-attachments/assets/dce9b169-3a2e-4f3b-a0c3-4c162c623ae7" width="400px" height="auto"/>

A way to define workflows with multiple AI Agents declaratively

## How the idea came about

I was fascinated by [Andrew NG's talk at Sequioa Capital](https://www.youtube.com/watch?v=sal78ACtGTc) about Agentic workflows with multiple AI agents collaborating to complete a task. I was trying to see if a framework for doing the same existing in Java which was built around Langchang4j (an unoffical port of its namesake from python). The key building block which was missing was a communication channel which could support consistent flow of messages, maintain a global state and support pub/sub model. And then it struck me that such a global state manager already exists in the UI Development world in the form of Redux / Flux which fits the use case well. Thus I set out to bring my idea to reality.


## [Get Started](https://github.com/compscikaran/agentic4j/wiki)

## What is Agentic4j

Key Features -
1. Create a simple Agent graph defining how agents should talk to each other
2. Uses Redux to provide a unified communication channel ensuring agents recieve only relevant messages in the conversation
3. In-built security features such as Gatekeeper agent, Circuit Breaker, Message timeout
