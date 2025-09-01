# GitHub Copilot Hints

- Project structure follows Clean Architecture (controller-service-repo)
- Use JPA repositories, never write raw SQL unless necessary
- Use Redis for caching room state and real-time scores
- WebSocket endpoint at `/ws`, with STOMP over SockJS
- Each game session is started by host -> all players receive question via `/topic/rooms/{roomId}`
- Answers are submitted via `/app/rooms/{roomId}/answer`
- JWT auth: access token stored in Authorization header
- Recommendation: Call Python FastAPI endpoint (`POST /recommend`) for cosine similarity results
- Log errors with SLF4J, include request ID for tracing
- Test APIs with Postman or Curl before integrating with frontend