`Алгоритм`:  

запустить по порядку
1. config-server
2. discovery-service
3. api-gateway
4. first-service
5. second-service

выполнить команду. порт 60000 - api-gateway, который перенаправит в first-service
```bash
curl http://localhost:60000/first-service/api/v1
```
  * ограничение по првемени ответа от второго сервиса = 5 сек
  * лимит запросов 1/10sec