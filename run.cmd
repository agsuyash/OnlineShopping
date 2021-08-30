curl -X PUT http://localhost:3001/api/products/add -H 'Content-Type:application/json' -d "{\"id\":1,\"name\":\"Laptop\",\"price\":2000.00,\"description\":\"Dell Laptop\"}"
curl -X PUT http://localhost:3001/api/products/add -H 'Content-Type:application/json' -d "{\"id\":2,\"name\":\"Mouse\",\"price\":200.00,\"description\":\"Dell Mouse\"}"

curl -X PUT http://localhost:3001/api/discount/add -H 'Content-Type:application/json' -d "{\"productId\":1,\"strategyName\":\"com.onlinestore.products.discount.BuyOneGetOneFreeDiscountStrategy\",\"strategyParams\":{\"productId\":1,\"freeProductId\":2}}"

curl -X PUT http://localhost:3002/api/order/add -H 'Content-Type:application/json' -d "{\"productId\":1,\"quantity\":2}"
curl -X PUT http://localhost:3002/api/order/add -H 'Content-Type:application/json' -d "{\"productId\":2,\"quantity\":3}"

exit