curl -X PUT http://localhost:3001/api/discount/add -H 'Content-Type:application/json' -d "{\"productId\":1,\"strategyName\":\"com.onlinestore.products.discount.BuyOneGetOneFreeDiscountStrategy\",\"strategyParams\":{\"productId\":1,\"freeProductId\":2}}"