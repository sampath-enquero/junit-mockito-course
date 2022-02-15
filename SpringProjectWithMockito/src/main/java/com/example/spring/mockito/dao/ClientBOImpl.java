package com.example.spring.mockito.dao;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.spring.mockito.models.*;

import com.example.spring.mockito.dao.ProductDO;
import com.example.spring.mockito.dao.ClientDO;

public class ClientBOImpl implements ClientBO {

	@Autowired
	ProductDO productDO;

	@Autowired
	ClientDO clientDO;

	@Override
	public Amount getClientProductsSum(long clientId) {

		List<Product> existingProducts = productDO.getAllProducts(clientId);

		return new AmountImpl(calculateClientProductSum(existingProducts),
				Currency.EURO);
	}


	@Override
	public void saveChangedProducts(long clientId,
			List<Product> userEnteredProducts) {

		List<Product> databaseProducts = productDO.getAllProducts(clientId);

		updateExistingProductsWhichAreModified(clientId, userEnteredProducts,
				databaseProducts);

		insertNewProducts(clientId, userEnteredProducts, databaseProducts);

		deleteStaleProducts(clientId, databaseProducts, userEnteredProducts);

	}

	@Override
	public void calculateAndSaveClientProductSum(Client client) {
		BigDecimal clientProductSum = calculateClientProductSum(client
				.getProducts());
		client.setProductAmount(clientProductSum);
		clientDO.saveClient(client);
	}

	private void deleteStaleProducts(long clientId,
			List<Product> existingProducts, List<Product> newProducts) {
		Map<Long, Product> newProductsMap = convertToMap(newProducts);
		for (Product product1 : existingProducts) {
			if (!newProductsMap.containsKey(product1.getId())) {
				productDO.deleteProduct(clientId, product1);
			}
		}
	}

	private void insertNewProducts(long clientId, List<Product> newProducts,
			List<Product> existingProducts) {
		Map<Long, Product> existingProductsMap = convertToMap(existingProducts);
		for (Product newProduct : newProducts) {
			if (!existingProductsMap.containsKey(newProduct.getId())) {
				productDO.insertProduct(clientId, newProduct);
			}
		}
	}

	private void updateExistingProductsWhichAreModified(long clientId,
			List<Product> newProducts, List<Product> existingProducts) {
		Map<Long, Product> existingProductsMap = convertToMap(existingProducts);
		for (Product newProduct : newProducts) {
			if (existingProductsMap.containsKey(newProduct.getId())) {
				productDO.updateProduct(clientId, newProduct);
			}
		}
	}

	private Map<Long, Product> convertToMap(List<Product> products) {
		Map<Long, Product> productMap = new HashMap<Long, Product>();
		for (Product existingProduct : products) {
			productMap.put(existingProduct.getId(), existingProduct);
		}
		return productMap;
	}

	private BigDecimal calculateClientProductSum(List<Product> existingProducts) {
		BigDecimal clientProductsSum = BigDecimal.ZERO;

		for (Product product : existingProducts) {
			clientProductsSum = clientProductsSum.add(product.getAmount()
					.getValue());
		}
		return clientProductsSum;
	}


}
